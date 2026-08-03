package at.minecraftschurli.mods.bibliocraft.client.model;

import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public final class ConditionalModelLoader {
    public static final Identifier ID = BCUtil.bcLoc("conditional");

    private ConditionalModelLoader() {}

    public static UnbakedModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        JsonArray conditionArray = GsonHelper.getAsJsonArray(json, "neoforge:conditions", new JsonArray());
        if (conditionArray.isEmpty()) {
            conditionArray = GsonHelper.getAsJsonArray(json, "fabric:load_conditions", new JsonArray());
        }
        if (conditionsPass(parseConditions(conditionArray))) {
            json.remove("loader");
            return context.deserialize(json, CuboidModel.class);
        }
        return MissingCuboidModel.missingModel();
    }

    private static boolean conditionsPass(ResourceCondition[] conditions) {
        for (ResourceCondition condition : conditions) {
            if (!condition.test(null)) {
                return false;
            }
        }
        return true;
    }

    private static ResourceCondition[] parseConditions(JsonArray conditionArray) {
        return ResourceCondition.CONDITION_CODEC.listOf()
                .decode(JsonOps.INSTANCE, conditionArray)
                .getOrThrow(error -> new JsonParseException("Failed to parse model conditions: " + error))
                .getFirst()
                .toArray(ResourceCondition[]::new);
    }
}
