package com.github.minecraftschurlimods.bibliocraft.client.model;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.client.ber.TableBER;
import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlock;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.client.renderer.block.model.BlockModel;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Fabric model loading plugin. When a model JSON has "loader": "bibliocraft:table" or
 * "bibliocraft:bookcase", we replace the loaded model with our TableModel.Geometry or
 * BookcaseModel.BookcaseGeometry so it bakes correctly.
 * <p>
 * Replacement requires parsing the JSON into BlockModel for each type; vanilla does not expose
 * a public API for that. So we load the JSON, detect the loader, and build our Geometry by
 * using the modifier context's getOrLoadModel for parent-based sub-models where possible.
 */
public final class BCModelLoadingPlugin implements PreparableModelLoadingPlugin<ResourceManager> {

    @Override
    public void onInitializeModelLoader(ResourceManager resourceManager, net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context pluginContext) {
        addTableClothModels(pluginContext);
        addBookcaseBookModels(pluginContext);
        // Do not call TableBER.rebuildClothModelCache() from modifyModelAfterBake - ModelManager.bakedRegistry
        // is still null during baking. Cache is rebuilt lazily in TableBER when rendering and cleared on resource reload.
        pluginContext.modifyModelOnLoad().register((model, context) -> {
            if (context.resourceId() == null) return model;
            ResourceLocation id = context.resourceId();
            if (!BibliocraftApi.MOD_ID.equals(id.getNamespace())) return model;
            String path = id.getPath();
            if (!path.contains("table") && !path.contains("bookcase")) return model;

            ResourceLocation jsonPath = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/" + path + ".json");
            try {
                var resource = resourceManager.getResource(jsonPath);
                if (resource.isEmpty()) return model;
                com.google.gson.JsonObject json;
                try (InputStreamReader reader = new InputStreamReader(resource.get().open())) {
                    json = GsonHelper.parse(reader);
                }
                String loader = GsonHelper.getAsString(json, "loader", "");
                if (BCUtil.bcLoc("table").toString().equals(loader)) {
                    net.minecraft.client.resources.model.UnbakedModel replacement = loadTableGeometry(json, context);
                    if (replacement != null) return replacement;
                }
                if (BCUtil.bcLoc("bookcase").toString().equals(loader)) {
                    net.minecraft.client.resources.model.UnbakedModel replacement = loadBookcaseGeometry(json, context);
                    if (replacement != null) return replacement;
                }
            } catch (Exception ignored) {
                // Fall back to original model
            }
            return model;
        });
    }

    /**
     * Build TableModel.Geometry from JSON. Each table type (none, one, straight, etc.) is a
     * sub-object with parent + textures; we load each via the context so parents resolve.
     */
    private static net.minecraft.client.resources.model.UnbakedModel loadTableGeometry(com.google.gson.JsonObject json, net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.OnLoad.Context context) {
        Map<TableBlock.Type, BlockModel> baseMap = new HashMap<>();
        for (TableBlock.Type type : TableBlock.Type.values()) {
            String key = type.getSerializedName();
            if (!json.has(key)) continue;
            com.google.gson.JsonObject typeJson = GsonHelper.getAsJsonObject(json, key);
            // Resolve parent to a full model path and load it; typeJson has "parent" + "textures"
            ResourceLocation parentId = ResourceLocation.tryParse(GsonHelper.getAsString(typeJson, "parent"));
            if (parentId == null) continue;
            net.minecraft.client.resources.model.UnbakedModel parentModel = context.getOrLoadModel(parentId);
            if (!(parentModel instanceof BlockModel blockModel)) continue;
            // Parent has texture refs (#texture); apply type's "textures" so planks resolve.
            BlockModel withTextures = tryDeserializeBlockModel(typeJson, context);
            if (withTextures == null) withTextures = tryApplyTexturesToParent(blockModel, typeJson);
            baseMap.put(type, withTextures != null ? withTextures : blockModel);
        }
        if (baseMap.size() != TableBlock.Type.values().length) return null;
        return new TableModel.Geometry(baseMap);
    }

    /**
     * Try to deserialize a BlockModel from JSON (parent + textures). Uses the loader's Gson
     * so texture resolution matches vanilla. Returns null if unavailable.
     */
    @SuppressWarnings("DataFlowIssue")
    private static BlockModel tryDeserializeBlockModel(com.google.gson.JsonObject typeJson, net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.OnLoad.Context context) {
        try {
            // 1) Try loader's Gson (ModelLoader holds the Gson used to parse block models)
            com.google.gson.Gson gson = getLoaderGson(context);
            if (gson != null) {
                BlockModel parsed = gson.fromJson(typeJson, BlockModel.class);
                if (parsed != null) {
                    parsed.resolveParents(context::getOrLoadModel);
                    return parsed;
                }
            }
            // 2) BlockModel static fromJson / deserializer
            for (Method m : BlockModel.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && com.google.gson.JsonElement.class.isAssignableFrom(m.getParameterTypes()[0])
                    && BlockModel.class.isAssignableFrom(m.getReturnType())) {
                    m.setAccessible(true);
                    BlockModel b = (BlockModel) m.invoke(null, typeJson);
                    if (b != null) { b.resolveParents(context::getOrLoadModel); return b; }
                }
            }
            for (Method m : BlockModel.class.getDeclaredMethods()) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length >= 1 && params[0] == com.google.gson.JsonObject.class && BlockModel.class.isAssignableFrom(m.getReturnType())) {
                    m.setAccessible(true);
                    BlockModel b = params.length == 1 ? (BlockModel) m.invoke(null, typeJson) : (params[1].getName().contains("Context") ? (BlockModel) m.invoke(null, typeJson, context) : null);
                    if (b != null) { if (params.length == 1) b.resolveParents(context::getOrLoadModel); return b; }
                }
            }
            // 3) Static Gson on BlockModel (used by vanilla to parse model JSON)
            com.google.gson.Gson modelGson = getBlockModelGson();
            if (modelGson != null) {
                BlockModel parsed = modelGson.fromJson(typeJson, BlockModel.class);
                if (parsed != null) {
                    parsed.resolveParents(context::getOrLoadModel);
                    return parsed;
                }
            }
        } catch (Throwable ignored) {
            // Fall back to parent-only model
        }
        return null;
    }

    /** Get the Gson used by the model loader to parse BlockModel JSON, via reflection. */
    private static com.google.gson.Gson getLoaderGson(net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.OnLoad.Context context) {
        try {
            Method loaderMethod = null;
            for (Method m : context.getClass().getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType() != Void.TYPE && m.getReturnType() != void.class) {
                    String name = m.getName().toLowerCase();
                    if (name.equals("loader") || name.equals("getloader")) { loaderMethod = m; break; }
                }
            }
            if (loaderMethod == null) return null;
            Object loader = loaderMethod.invoke(context);
            if (loader == null) return null;
            for (Field f : loader.getClass().getDeclaredFields()) {
                if (f.getType() == com.google.gson.Gson.class) {
                    f.setAccessible(true);
                    return (com.google.gson.Gson) f.get(loader);
                }
            }
            // Yarn: might be in a superclass or named differently
            Class<?> c = loader.getClass();
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
                    if (com.google.gson.Gson.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        Object g = f.get(loader);
                        if (g instanceof com.google.gson.Gson) return (com.google.gson.Gson) g;
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** Find static Gson on BlockModel or its nested Deserializer used for JSON parsing. */
    private static com.google.gson.Gson getBlockModelGson() {
        try {
            for (Field f : BlockModel.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) && f.getType() == com.google.gson.Gson.class) {
                    f.setAccessible(true);
                    return (com.google.gson.Gson) f.get(null);
                }
            }
            for (Class<?> inner : BlockModel.class.getDeclaredClasses()) {
                for (Field f : inner.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) && f.getType() == com.google.gson.Gson.class) {
                        f.setAccessible(true);
                        return (com.google.gson.Gson) f.get(null);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Apply type JSON "textures" to the parent BlockModel via reflection so #texture resolves.
     * Tries to find BlockModel's texture map field and create a copy with merged textures.
     */
    private static BlockModel tryApplyTexturesToParent(BlockModel parent, com.google.gson.JsonObject typeJson) {
        if (!typeJson.has("textures")) return null;
        com.google.gson.JsonObject texturesJson = GsonHelper.getAsJsonObject(typeJson, "textures");
        Map<String, String> overrides = new HashMap<>();
        for (String key : texturesJson.keySet()) {
            overrides.put(key, GsonHelper.getAsString(texturesJson, key));
        }
        if (overrides.isEmpty()) return null;
        try {
            // Find texture map field on BlockModel (Mojang: might be textureMap or similar)
            for (Field f : BlockModel.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, String> existing = (Map<String, String>) f.get(parent);
                    if (existing != null) {
                        Map<String, String> merged = new HashMap<>(existing);
                        merged.putAll(overrides);
                        // Try to create a new BlockModel with same parent and merged map
                        ResourceLocation parentLoc = getParentLocation(parent);
                        if (parentLoc != null) {
                            for (Constructor<?> c : BlockModel.class.getDeclaredConstructors()) {
                                Class<?>[] params = c.getParameterTypes();
                                if (params.length == 2 && params[0] == ResourceLocation.class && Map.class.isAssignableFrom(params[1])) {
                                    c.setAccessible(true);
                                    return (BlockModel) c.newInstance(parentLoc, merged);
                                }
                            }
                        }
                        return null;
                    }
                }
            }
            // Try getParentLocation + constructor BlockModel(ResourceLocation, Map)
            ResourceLocation parentLoc = getParentLocation(parent);
            if (parentLoc != null) {
                for (Constructor<?> c : BlockModel.class.getDeclaredConstructors()) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 2 && params[0] == ResourceLocation.class && Map.class.isAssignableFrom(params[1])) {
                        c.setAccessible(true);
                        return (BlockModel) c.newInstance(parentLoc, overrides);
                    }
                }
            }
        } catch (Throwable ignored) {
            // Reflection failed
        }
        return null;
    }

    private static ResourceLocation getParentLocation(BlockModel model) {
        try {
            for (Field f : BlockModel.class.getDeclaredFields()) {
                if (f.getType() == ResourceLocation.class && (f.getName().toLowerCase().contains("parent") || f.getName().length() <= 3)) {
                    f.setAccessible(true);
                    ResourceLocation loc = (ResourceLocation) f.get(model);
                    if (loc != null) return loc;
                }
            }
            for (Field f : BlockModel.class.getDeclaredFields()) {
                if (f.getType() == ResourceLocation.class) {
                    f.setAccessible(true);
                    return (ResourceLocation) f.get(model);
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Cannot return BookcaseGeometry here: wood variants parent this template as a BlockModel,
     * and vanilla requires parents to be block models (see latest.log crash).
     * Books are rendered by BookcaseBER instead.
     */
    private static net.minecraft.client.resources.model.UnbakedModel loadBookcaseGeometry(com.google.gson.JsonObject json, net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.OnLoad.Context context) {
        return null;
    }

    private static void addTableClothModels(net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context pluginContext) {
        for (TableBlock.Type type : TableBlock.Type.values()) {
            for (DyeColor color : DyeColor.values()) {
                pluginContext.addModels(ResourceLocation.fromNamespaceAndPath(
                    BibliocraftApi.MOD_ID,
                    "block/color/" + color.getSerializedName() + "/table_cloth_" + type.getSerializedName()
                ));
            }
        }
    }

    /** Registers the 16 book sub-models so they are baked and available for BookcaseBER. */
    private static void addBookcaseBookModels(net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context pluginContext) {
        for (int i = 0; i < 16; i++) {
            pluginContext.addModels(ResourceLocation.fromNamespaceAndPath(
                BibliocraftApi.MOD_ID,
                "block/template/bookcase/book_" + i
            ));
        }
    }
}
