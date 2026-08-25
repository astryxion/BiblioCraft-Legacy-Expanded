package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.IRandomRange;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class EnchantmentLevelsNumberProvider implements IRandomRange {
    private final IRandomRange normalCost;
    private final Optional<IRandomRange> treasureCost;
    private final Map<Enchantment, IRandomRange> overrides;

    public EnchantmentLevelsNumberProvider(IRandomRange normalCost, Optional<IRandomRange> treasureCost, Map<Enchantment, IRandomRange> overrides) {
        this.normalCost = normalCost;
        this.treasureCost = treasureCost;
        this.overrides = overrides;
    }

    public IRandomRange normalCost() { return this.normalCost; }
    public Optional<IRandomRange> treasureCost() { return this.treasureCost; }
    public Map<Enchantment, IRandomRange> overrides() { return this.overrides; }

    /** 1.20.1: lazy codec so getNumberProviderCodec() is only called at decode/encode time (CODEC then set). */
    private static Codec<IRandomRange> numberProviderCodecHolder;

    private static Codec<IRandomRange> getNumberProviderCodec() {
        if (numberProviderCodecHolder == null)
            numberProviderCodecHolder = CODEC.xmap(p -> p, p -> (EnchantmentLevelsNumberProvider) p);
        return numberProviderCodecHolder;
    }

    /** 1.20.1: Codec.of(Decoder, Encoder) not available; use MapCodec that delegates at decode/encode time. */
    private static Codec<IRandomRange> lazyNumberProviderCodec() {
        return new Codec<IRandomRange>() {
            @Override
            public <T> com.mojang.serialization.DataResult<com.mojang.datafixers.util.Pair<IRandomRange, T>> decode(com.mojang.serialization.DynamicOps<T> ops, T input) {
                return getNumberProviderCodec().decode(ops, input);
            }
            @Override
            public <T> com.mojang.serialization.DataResult<T> encode(IRandomRange input, com.mojang.serialization.DynamicOps<T> ops, T prefix) {
                return getNumberProviderCodec().encode(input, ops, prefix);
            }
        };
    }

    public static final Codec<IRandomRange> NUMBER_PROVIDER_CODEC = lazyNumberProviderCodec();

    /**
     * Parses 1.21-style experience_cost JSON. Constants may be raw numbers ({@code 1.0}) instead of loot objects.
     */
    public static IRandomRange parse(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ConstantRange.exactly(0);
        }
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return ConstantRange.exactly(json.getAsInt());
        }
        if (!json.isJsonObject()) {
            return ConstantRange.exactly(0);
        }
        JsonObject obj = json.getAsJsonObject();
        if (obj.has("normal_cost") || (obj.has("type") && "bibliocraft:enchantment_levels".equals(obj.get("type").getAsString()))) {
            IRandomRange normalCost = parse(obj.get("normal_cost"));
            Optional<IRandomRange> treasureCost = obj.has("treasure_cost") ? Optional.of(parse(obj.get("treasure_cost"))) : Optional.empty();
            Map<Enchantment, IRandomRange> overrides = new HashMap<>();
            if (obj.has("overrides") && obj.get("overrides").isJsonObject()) {
                JsonObject overridesObj = obj.getAsJsonObject("overrides");
                for (Map.Entry<String, JsonElement> overrideEntry : overridesObj.entrySet()) {
                    ResourceLocation id = new ResourceLocation(overrideEntry.getKey());
                    Enchantment enchantment = Registry.ENCHANTMENT.get(id);
                    if (enchantment != null) {
                        overrides.put(enchantment, parse(overrideEntry.getValue()));
                    }
                }
            }
            return new EnchantmentLevelsNumberProvider(normalCost, treasureCost, overrides);
        }
        if (obj.has("value")) {
            return ConstantRange.exactly(JSONUtils.getAsInt(obj, "value"));
        }
        try {
            return CodecUtil.decodeJson(CODEC, json);
        } catch (RuntimeException e) {
            return ConstantRange.exactly(0);
        }
    }

    private static final ITag.INamedTag<Enchantment> TREASURE_ENCHANTMENTS = net.minecraftforge.common.ForgeTagHandler.createOptionalTag(net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS, new ResourceLocation("minecraft", "treasure"));

    public static final Codec<EnchantmentLevelsNumberProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            lazyNumberProviderCodec().fieldOf("normal_cost").forGetter(EnchantmentLevelsNumberProvider::normalCost),
            lazyNumberProviderCodec().optionalFieldOf("treasure_cost").forGetter(EnchantmentLevelsNumberProvider::treasureCost),
            Codec.unboundedMap(net.minecraft.util.ResourceLocation.CODEC.xmap(Registry.ENCHANTMENT::get, Registry.ENCHANTMENT::getKey), lazyNumberProviderCodec()).optionalFieldOf("overrides", java.util.Collections.emptyMap()).forGetter(p -> p.overrides())
    ).apply(inst, EnchantmentLevelsNumberProvider::new));

    public EnchantmentLevelsNumberProvider(IRandomRange normalCost, IRandomRange treasureCost) {
        this(normalCost, Optional.of(treasureCost), java.util.Collections.emptyMap());
    }

    public float getFloat(LootContext context) {
        ItemStack stack = context.getParamOrNull(LootParameters.TOOL);
        if (stack == null || stack.isEmpty()) return 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return 0;
        float cost = 0;
        for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
            Enchantment enchantment = e.getKey();
            int level = e.getValue();
            IRandomRange provider = overrides.get(enchantment);
            if (provider != null) {
                cost += floatFrom(provider, context) * level;
            } else if (treasureCost.isPresent() && isTreasure(enchantment)) {
                cost += floatFrom(treasureCost.get(), context) * level;
            } else {
                cost += floatFrom(normalCost, context) * level;
            }
        }
        return cost;
    }

    private static boolean isTreasure(Enchantment enchantment) {
        if (enchantment.isTreasureOnly()) return true;
        return TREASURE_ENCHANTMENTS.contains(enchantment);
    }

    private static float floatFrom(IRandomRange provider, LootContext context) {
        if (provider instanceof EnchantmentLevelsNumberProvider) {
            return ((EnchantmentLevelsNumberProvider) provider).getFloat(context);
        }
        return provider.getInt(context.getRandom());
    }

    public int getInt(LootContext lootContext) {
        float value = getFloat(lootContext);
        return value <= 0 ? 0 : value <= 1 ? 1 : (int) value;
    }

    @Override
    public int getInt(java.util.Random random) {
        return 0;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE_ID;
    }

    public static final ResourceLocation TYPE_ID = new ResourceLocation("bibliocraft", "enchantment_levels");

    /**
     * 1.16.5 has no {@code LootNumberProviderType}; nested Forge registry entry keeps the same serializer registration.
     */
    public static final class LootNumberProviderType extends net.minecraftforge.registries.ForgeRegistryEntry<LootNumberProviderType> {
        private final LootSerializer serializer;

        public LootNumberProviderType(LootSerializer serializer) {
            this.serializer = serializer;
        }

        public LootSerializer serializer() {
            return serializer;
        }
    }

    /** 1.20.1: ILootSerializer expects Serializer, not Codec. */
    public static final class LootSerializer implements ILootSerializer<IRandomRange> {
        @Override
        public IRandomRange deserialize(com.google.gson.JsonObject json, com.google.gson.JsonDeserializationContext context) {
            IRandomRange normalCost = context.deserialize(JSONUtils.getAsJsonObject(json, "normal_cost"), IRandomRange.class);
            Optional<IRandomRange> treasureCost = json.has("treasure_cost")
                    ? Optional.of(context.deserialize(JSONUtils.getAsJsonObject(json, "treasure_cost"), IRandomRange.class))
                    : Optional.empty();
            Map<Enchantment, IRandomRange> overrides = new HashMap<>();
            if (json.has("overrides")) {
                com.google.gson.JsonObject overridesObj = JSONUtils.getAsJsonObject(json, "overrides");
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> overrideEntry : overridesObj.entrySet()) {
                    String key = overrideEntry.getKey();
                    ResourceLocation id = new ResourceLocation(key);
                    if (id != null) {
                        Enchantment e = Registry.ENCHANTMENT.get(id);
                        if (e != null)
                            overrides.put(e, context.deserialize(overridesObj.get(key), IRandomRange.class));
                    }
                }
            }
            return new EnchantmentLevelsNumberProvider(normalCost, treasureCost, overrides);
        }

        @Override
        public void serialize(com.google.gson.JsonObject json, IRandomRange value, com.google.gson.JsonSerializationContext context) {
            if (!(value instanceof EnchantmentLevelsNumberProvider)) return;
            EnchantmentLevelsNumberProvider p = (EnchantmentLevelsNumberProvider) value;
            json.add("normal_cost", context.serialize(p.normalCost()));
            p.treasureCost().ifPresent(t -> json.add("treasure_cost", context.serialize(t)));
            if (!p.overrides().isEmpty()) {
                com.google.gson.JsonObject overrides = new com.google.gson.JsonObject();
                for (Map.Entry<Enchantment, IRandomRange> e : p.overrides().entrySet())
                    overrides.add(Registry.ENCHANTMENT.getKey(e.getKey()).toString(), context.serialize(e.getValue()));
                json.add("overrides", overrides);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnchantmentLevelsNumberProvider other = (EnchantmentLevelsNumberProvider) o;
        return java.util.Objects.equals(this.normalCost, other.normalCost) && java.util.Objects.equals(this.treasureCost, other.treasureCost) && java.util.Objects.equals(this.overrides, other.overrides);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.normalCost, this.treasureCost, this.overrides);
    }

    @Override
    public String toString() {
        return "EnchantmentLevelsNumberProvider[" + "normalCost=" + this.normalCost + ", " + "treasureCost=" + this.treasureCost + ", " + "overrides=" + this.overrides + "]";
    }

}
