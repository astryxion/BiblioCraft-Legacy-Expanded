package com.github.minecraftschurlimods.bibliocraft.content.swordpedestal;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class SwordPedestalBlockEntity extends BCBlockEntity implements net.minecraft.tileentity.ITickableTileEntity {
    private static final int TICK_INTERVAL = 20;
    private static final int RANGE = 2;
    private static final String COLOR_KEY = "color";
    private SwordPedestalBlock.DyedColor color = SwordPedestalBlock.DEFAULT_COLOR;

    public SwordPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.SWORD_PEDESTAL.get(), 1, pos, state);
    }

    @Override
    public void tick() {
        if (level != null) {
            tick(level, getBlockPos(), getBlockState(), this);
        }
    }

    public static void tick(World level, BlockPos pos, BlockState state, SwordPedestalBlockEntity blockEntity) {
        if (level.isClientSide()) return;
        if (level.getGameTime() % TICK_INTERVAL != 0) return;
        ItemStack stack = blockEntity.getItem(0).copy();
        if (!stack.isDamaged()) return;
        List<Enchantment> list = EnchantmentHelper.getEnchantments(stack).keySet().stream()
            .filter(e -> e == Enchantments.MENDING)
            .collect(java.util.stream.Collectors.toList());
        if (list.isEmpty()) return;
        Vector3d vec = Vector3d.atCenterOf(pos);
        for (ExperienceOrbEntity orb : level.getEntitiesOfClass(ExperienceOrbEntity.class, new AxisAlignedBB(vec.add(-RANGE, -RANGE, -RANGE), vec.add(RANGE, RANGE, RANGE)))) {
            int i = blockEntity.repairItem((ServerWorld) level, stack, orb.getValue());
            orb.remove();
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
        if (BCTags.Items.contains(BCTags.Items.SWORD_PEDESTAL_SWORDS, stack.getItem())) return true;
        if (stack.getItem() instanceof net.minecraft.item.SwordItem) return true;
        net.minecraft.tags.ITag<net.minecraft.item.Item> swords = ItemTags.getAllTags().getTag(new net.minecraft.util.ResourceLocation("minecraft", "swords"));
        return swords != null && swords.contains(stack.getItem());
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains(COLOR_KEY)) {
            setColor(CodecUtil.decodeNbt(SwordPedestalBlock.DyedColor.CODEC, tag.get(COLOR_KEY)));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put(COLOR_KEY, CodecUtil.encodeNbt(SwordPedestalBlock.DyedColor.CODEC, getColor()));
            return tag;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        if (!color.equals(SwordPedestalBlock.DEFAULT_COLOR)) {
            tag.put(COLOR_KEY, CodecUtil.encodeNbt(SwordPedestalBlock.DyedColor.CODEC, getColor()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        if (tag.contains(COLOR_KEY)) {
            setColor(CodecUtil.decodeNbt(SwordPedestalBlock.DyedColor.CODEC, tag.get(COLOR_KEY)));
        }
    }

    public void saveToItem(ItemStack stack) {
        SwordPedestalBlock.DyedColor.putOnStack(stack, getColor());
    }

    private int repairItem(ServerWorld level, ItemStack stack, int value) {
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
