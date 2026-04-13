package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StockroomCatalogItem extends Item {
    private static final Comparator<StockroomCatalogItemEntry> COMPARE_NAME = Comparator.comparing(e -> e.item().getDisplayName().getString());
    private static final Comparator<StockroomCatalogItemEntry> COMPARE_COUNT = Comparator.comparingInt(StockroomCatalogItemEntry::count);

    public StockroomCatalogItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @SuppressWarnings("deprecation")
    public static List<BlockPos> calculatePositions(ItemStack stack, Level level, Player player, StockroomCatalogSorting.Container containerSorting) {
        Comparator<BlockPos> COMPARE_DISTANCE = Comparator.comparingDouble(e -> player.position().distanceTo(BCUtil.toVec3(e)));
        Comparator<BlockPos> COMPARE_ALPHABETICAL = Comparator.comparing(e -> BCUtil.getNameAtPos(level, e).getString());
        return StockroomCatalogContent.getFromStack(stack)
                .positions()
                .stream()
                .filter(e -> e.dimension() == level.dimension())
                .map(GlobalPos::pos)
                .filter(level::hasChunkAt)
                .filter(e -> {
                    BlockEntity be = level.getBlockEntity(e);
                    return be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, null).isPresent();
                })
                .sorted(switch (containerSorting) {
                    case ALPHABETICAL_ASC, DISTANCE_ASC -> COMPARE_DISTANCE;
                    case ALPHABETICAL_DESC, DISTANCE_DESC -> BCUtil.reverseComparator(COMPARE_DISTANCE);
                })
                .sorted(switch (containerSorting) {
                    case ALPHABETICAL_ASC -> COMPARE_ALPHABETICAL;
                    case ALPHABETICAL_DESC -> BCUtil.reverseComparator(COMPARE_ALPHABETICAL);
                    default -> Comparator.comparingInt($ -> 0);
                })
                .toList();
    }

    public static List<StockroomCatalogItemEntry> calculateItems(List<BlockPos> positions, Level level, StockroomCatalogSorting.Item itemSorting) {
        Map<ItemStack, StockroomCatalogItemEntry> tempItems = new LinkedHashMap<>();
        for (BlockPos pos : positions) {
            BlockEntity be = level.getBlockEntity(pos);
            IItemHandler cap = be != null ? be.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null) : null;
            if (cap == null) continue;
            for (int i = 0; i < cap.getSlots(); i++) {
                ItemStack originalStack = cap.getStackInSlot(i);
                if (originalStack.isEmpty()) continue;
                ItemStack stack = originalStack.copy();
                int count = stack.getCount();
                stack.setCount(1);
                Optional<ItemStack> optional = tempItems
                        .keySet()
                        .stream()
                        .filter(e -> ItemStack.isSameItemSameTags(e, stack))
                        .findFirst();
                StockroomCatalogItemEntry entry = optional
                        .map(itemStack -> tempItems.get(itemStack).add(count))
                        .orElseGet(() -> new StockroomCatalogItemEntry(originalStack));
                tempItems.put(optional.orElse(stack), entry.add(pos));
            }
        }
        return tempItems.values()
                .stream()
                .sorted(switch (itemSorting) {
                    case ALPHABETICAL_ASC -> COMPARE_COUNT;
                    case ALPHABETICAL_DESC -> BCUtil.reverseComparator(COMPARE_COUNT);
                    case COUNT_ASC -> COMPARE_NAME;
                    case COUNT_DESC -> BCUtil.reverseComparator(COMPARE_NAME);
                })
                .sorted(switch (itemSorting) {
                    case ALPHABETICAL_ASC -> COMPARE_NAME;
                    case ALPHABETICAL_DESC -> BCUtil.reverseComparator(COMPARE_NAME);
                    case COUNT_ASC -> COMPARE_COUNT;
                    case COUNT_DESC -> BCUtil.reverseComparator(COMPARE_COUNT);
                })
                .toList();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && context.isSecondaryUseActive()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();
            BlockState state = level.getBlockState(pos);
            ItemStack stack = context.getItemInHand();
            StockroomCatalogContent list = StockroomCatalogContent.getFromStack(stack);
            boolean hasNeighbor = state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE;
            GlobalPos neighborPos = hasNeighbor ? GlobalPos.of(level.dimension(), pos.offset(ChestBlock.getConnectedDirection(state).getNormal())) : null;
            boolean hasPositionAtNeighbor = hasNeighbor && list.positions().contains(neighborPos);
            GlobalPos globalPos = hasPositionAtNeighbor ? neighborPos : GlobalPos.of(level.dimension(), pos);
            if (list.positions().contains(globalPos)) {
                StockroomCatalogContent.setOnStack(stack, list.remove(globalPos));
                player.displayClientMessage(Component.translatable(Translations.STOCKROOM_CATALOG_REMOVE_CONTAINER_KEY, BCUtil.getNameAtPos(level, pos)), true);
                return InteractionResult.SUCCESS;
            }
            BlockEntity be = level.getBlockEntity(pos);
            IItemHandler cap = be != null ? be.getCapability(ForgeCapabilities.ITEM_HANDLER, context.getClickedFace()).orElse(null) : null;
            if (cap != null) {
                StockroomCatalogContent.setOnStack(stack, list.add(globalPos));
                player.displayClientMessage(Component.translatable(Translations.STOCKROOM_CATALOG_ADD_CONTAINER_KEY, BCUtil.getNameAtPos(level, pos)), true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            ClientUtil.openStockroomCatalogScreen(stack, player, hand);
        }
        return InteractionResultHolder.success(stack);
    }
}
