package com.github.minecraftschurlimods.bibliocraft.util.block;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.INameable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

/**
 * Abstract superclass for block entities with an associated menu.
 */
@SuppressWarnings("unused")
public abstract class BCMenuBlockEntity extends BCBlockEntity implements INamedContainerProvider, INameable {
    private static final String NAME_KEY = "CustomName";
    private final ITextComponent defaultName;
    private ITextComponent name;

    /**
     * @param type          The {@link TileEntityType} to use.
     * @param containerSize The size of the container.
     * @param defaultName   The title of the title, shown in GUIs.
     * @param pos           The position of this BE.
     * @param state         The state of this BE.
     */
    public BCMenuBlockEntity(TileEntityType<?> type, int containerSize, ITextComponent defaultName, BlockPos pos, BlockState state) {
        super(type, containerSize, pos, state);
        this.defaultName = defaultName;
    }

    /**
     * Creates a menu instance for this block entity.
     *
     * @param id        The menu id.
     * @param inventory The player inventory to use.
     * @return A menu instance for this block entity.
     */
    protected abstract Container createMenu(int id, PlayerInventory inventory);

    @Override
    public ITextComponent getName() {
        return name != null ? name : defaultName;
    }

    @Override
    @Nullable
    public ITextComponent getCustomName() {
        return name;
    }

    /**
     * Sets a custom name for this block entity.
     *
     * @param name The name to set.
     */
    public void setCustomName(ITextComponent name) {
        this.name = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return getName();
    }

    /**
     * @param name The name to use.
     * @return A title component of the format {@code "container.bibliocraft.<name>"}.
     */
    public static ITextComponent defaultName(String name) {
        return new TranslationTextComponent("container." + BibliocraftApi.MOD_ID + "." + name);
    }

    @Override
    @Nullable
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return LockableTileEntity.canUnlock(player, getLockKey(), getDisplayName()) ? this.createMenu(id, inventory) : null;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains(NAME_KEY, net.minecraftforge.common.util.Constants.NBT.TAG_STRING)) {
            this.name = ITextComponent.Serializer.fromJson(tag.getString(NAME_KEY));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        if (name != null) {
            tag.putString(NAME_KEY, ITextComponent.Serializer.toJson(name));
        }
        return tag;
    }
}
