package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record EnchantmentLevelsNumberProvider(NumberProvider normalCost, Optional<NumberProvider> treasureCost, Map<Enchantment, NumberProvider> overrides) implements NumberProvider {
    /** 1.20.1: lazy codec so getNumberProviderCodec() is only called at decode/encode time (CODEC then set). */
    private static Codec<NumberProvider> numberProviderCodecHolder;

    private static Codec<NumberProvider> getNumberProviderCodec() {
        if (numberProviderCodecHolder == null)
            numberProviderCodecHolder = CODEC.xmap(p -> p, p -> (EnchantmentLevelsNumberProvider) p);
        return numberProviderCodecHolder;
    }

    /** 1.20.1: Codec.of(Decoder, Encoder) not available; use MapCodec that delegates at decode/encode time. */
    private static Codec<NumberProvider> lazyNumberProviderCodec() {
        return new Codec<NumberProvider>() {
            @Override
            public <T> com.mojang.serialization.DataResult<com.mojang.datafixers.util.Pair<NumberProvider, T>> decode(com.mojang.serialization.DynamicOps<T> ops, T input) {
                return getNumberProviderCodec().decode(ops, input);
            }
            @Override
            public <T> com.mojang.serialization.DataResult<T> encode(NumberProvider input, com.mojang.serialization.DynamicOps<T> ops, T prefix) {
                return getNumberProviderCodec().encode(input, ops, prefix);
            }
        };
    }

    public static final Codec<NumberProvider> NUMBER_PROVIDER_CODEC = lazyNumberProviderCodec();

    private static final TagKey<Enchantment> TREASURE_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("minecraft", "enchantment/treasure"));

    public static final Codec<EnchantmentLevelsNumberProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            lazyNumberProviderCodec().fieldOf("normal_cost").forGetter(EnchantmentLevelsNumberProvider::normalCost),
            lazyNumberProviderCodec().optionalFieldOf("treasure_cost").forGetter(EnchantmentLevelsNumberProvider::treasureCost),
            Codec.unboundedMap(BuiltInRegistries.ENCHANTMENT.byNameCodec(), lazyNumberProviderCodec()).optionalFieldOf("overrides", Map.of()).forGetter(EnchantmentLevelsNumberProvider::overrides)
    ).apply(inst, EnchantmentLevelsNumberProvider::new));

    public EnchantmentLevelsNumberProvider(NumberProvider normalCost, NumberProvider treasureCost) {
        this(normalCost, Optional.of(treasureCost), Map.of());
    }

    @Override
    public float getFloat(LootContext context) {
        ItemStack stack = context.getParam(LootContextParams.TOOL);
        if (stack.isEmpty()) return 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return 0;
        float cost = 0;
        for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
            Enchantment enchantment = e.getKey();
            int level = e.getValue();
            NumberProvider provider = overrides.get(enchantment);
            if (provider != null) {
                cost += provider.getFloat(context) * level;
            } else if (treasureCost.isPresent() && BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment).flatMap(BuiltInRegistries.ENCHANTMENT::getHolder).map(h -> h.is(TREASURE_ENCHANTMENTS)).orElse(false)) {
                cost += treasureCost.get().getFloat(context) * level;
            } else {
                cost += normalCost.getFloat(context) * level;
            }
        }
        return cost;
    }

    @Override
    public int getInt(LootContext lootContext) {
        float value = getFloat(lootContext);
        return value <= 0 ? 0 : value <= 1 ? 1 : (int) value;
    }

    @Override
    public LootNumberProviderType getType() {
        return BCRecipes.ENCHANTMENT_LEVELS_NUMBER_PROVIDER.get();
    }

    /** 1.20.1: LootNumberProviderType expects Serializer, not Codec. */
    public static final class LootSerializer implements Serializer<NumberProvider> {
        @Override
        public NumberProvider deserialize(com.google.gson.JsonObject json, com.google.gson.JsonDeserializationContext context) {
            NumberProvider normalCost = context.deserialize(GsonHelper.getAsJsonObject(json, "normal_cost"), NumberProvider.class);
            Optional<NumberProvider> treasureCost = json.has("treasure_cost")
                    ? Optional.of(context.deserialize(GsonHelper.getAsJsonObject(json, "treasure_cost"), NumberProvider.class))
                    : Optional.empty();
            Map<Enchantment, NumberProvider> overrides = new HashMap<>();
            if (json.has("overrides")) {
                com.google.gson.JsonObject overridesObj = GsonHelper.getAsJsonObject(json, "overrides");
                for (String key : overridesObj.keySet()) {
                    ResourceLocation id = ResourceLocation.tryParse(key);
                    if (id != null) {
                        Enchantment e = BuiltInRegistries.ENCHANTMENT.get(id);
                        if (e != null)
                            overrides.put(e, context.deserialize(overridesObj.get(key), NumberProvider.class));
                    }
                }
            }
            return new EnchantmentLevelsNumberProvider(normalCost, treasureCost, overrides);
        }

        @Override
        public void serialize(com.google.gson.JsonObject json, NumberProvider value, com.google.gson.JsonSerializationContext context) {
            if (!(value instanceof EnchantmentLevelsNumberProvider p)) return;
            json.add("normal_cost", context.serialize(p.normalCost()));
            p.treasureCost().ifPresent(t -> json.add("treasure_cost", context.serialize(t)));
            if (!p.overrides().isEmpty()) {
                com.google.gson.JsonObject overrides = new com.google.gson.JsonObject();
                for (Map.Entry<Enchantment, NumberProvider> e : p.overrides().entrySet())
                    overrides.add(BuiltInRegistries.ENCHANTMENT.getKey(e.getKey()).toString(), context.serialize(e.getValue()));
                json.add("overrides", overrides);
            }
        }
    }
}
