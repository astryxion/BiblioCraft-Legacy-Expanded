package at.minecraftschurli.mods.bibliocraft.content.fancyarmorstand;

import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import org.jspecify.annotations.Nullable;

public class FancyArmorStandBlockEntity extends BCMenuBlockEntity {
    @Nullable
    private FancyArmorStandEntity entity;

    public FancyArmorStandBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_ARMOR_STAND.get(), 4, 1, defaultName("fancy_armor_stand"), pos, state);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        entity = new FancyArmorStandEntity(level, this);
        // Facing is applied by FancyArmorStandBER via the PoseStack; keep entity rotation at 0 so
        // LivingEntityRenderer bodyRot interpolation cannot spin equipped armor.
        entity.setId(-1 - getBlockPos().hashCode());
        entity.setYRot(0);
        entity.yRotO = 0;
        entity.setYBodyRot(0);
        entity.yBodyRotO = 0;
        entity.setYHeadRot(0);
        entity.yHeadRotO = 0;
        entity.elytraAnimationState.tick();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new FancyArmorStandMenu(id, inventory, this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (entity != null) {
            entity.setRemoved(Entity.RemovalReason.DISCARDED);
            entity = null;
        }
    }

    @Override
    public boolean isValid(int index, ItemVariant stack) {
        return stack.isBlank() || (stack.toStack(1).get(DataComponents.EQUIPPABLE) instanceof Equippable equippable && equippable.slot().isArmor() && equippable.slot().getIndex() == 3 - index && super.isValid(index, stack));
    }

    /// @return The [FancyArmorStandEntity] used for actually displaying the armor.
    @Nullable
    public FancyArmorStandEntity getDisplayEntity() {
        return entity;
    }
}
