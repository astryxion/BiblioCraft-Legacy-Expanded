package com.github.minecraftschurlimods.bibliocraft.content.swordpedestal;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SwordPedestalBlockEntity extends BCBlockEntity {
    private static final int TICK_INTERVAL = 20;
    private static final int RANGE = 2;
    private static final String COLOR_KEY = "color";
    private SwordPedestalBlock.DyedColor color = SwordPedestalBlock.DEFAULT_COLOR;

    public SwordPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.SWORD_PEDESTAL.get(), 1, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SwordPedestalBlockEntity blockEntity) {
        if (level.isClientSide()) return;
        if (level.getGameTime() % TICK_INTERVAL != 0) return;
        ItemStack stack = blockEntity.getItem(0).copy();
        if (!stack.isDamaged()) return;
        List<Enchantment> list = EnchantmentHelper.getEnchantments(stack).keySet().stream()
            .filter(e -> e == Enchantments.MENDING)
            .toList();
        if (list.isEmpty()) return;
        Vec3 vec = pos.getCenter();
        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, new AABB(vec.add(-RANGE, -RANGE, -RANGE), vec.add(RANGE, RANGE, RANGE)))) {
            int i = blockEntity.repairItem((ServerLevel) level, stack, orb.getValue());
            orb.discard();
            if (!stack.isDamaged()) break;
        }
        blockEntity.setItem(0, stack);
    }

    public SwordPedestalBlock.DyedColor getColor() {
        return color;
    }

    public void setColor(SwordPedestalBlock.DyedColor color) {
        this.color = color;
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (stack.is(BCTags.Items.SWORD_PEDESTAL_SWORDS)) return true;
        return stack.is(ItemTags.SWORDS);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(COLOR_KEY)) {
            setColor(CodecUtil.decodeNbt(SwordPedestalBlock.DyedColor.CODEC, tag.get(COLOR_KEY)));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(COLOR_KEY, CodecUtil.encodeNbt(SwordPedestalBlock.DyedColor.CODEC, getColor()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (!color.equals(SwordPedestalBlock.DEFAULT_COLOR)) {
            tag.put(COLOR_KEY, CodecUtil.encodeNbt(SwordPedestalBlock.DyedColor.CODEC, getColor()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains(COLOR_KEY)) {
            setColor(CodecUtil.decodeNbt(SwordPedestalBlock.DyedColor.CODEC, tag.get(COLOR_KEY)));
        }
    }

    public void saveToItem(ItemStack stack) {
        SwordPedestalBlock.DyedColor.putOnStack(stack, getColor());
    }

    private int repairItem(ServerLevel level, ItemStack stack, int value) {
        // Mending: 2 durability per 1 XP
        int i = Math.min(value * 2, stack.getDamageValue());
        int j = Math.min(i, stack.getDamageValue());
        stack.setDamageValue(stack.getDamageValue() - j);
        if (j > 0) {
            int k = value - (j / 2);
            if (k > 0) {
                return repairItem(level, stack, k);
            }
        }
        return 0;
    }
}
