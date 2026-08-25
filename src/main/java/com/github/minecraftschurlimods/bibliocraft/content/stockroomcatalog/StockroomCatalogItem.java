package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.ChestType;
import net.minecraftforge.items.CapabilityItemHandler;
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
    public static List<BlockPos> calculatePositions(ItemStack stack, World level, PlayerEntity player, StockroomCatalogSorting.Container containerSorting) {
        Comparator<BlockPos> COMPARE_DISTANCE = Comparator.comparingDouble(e -> player.position().distanceTo(BCUtil.toVec3(e)));
        Comparator<BlockPos> COMPARE_ALPHABETICAL = Comparator.comparing(e -> BCUtil.getNameAtPos(level, e).getString());
        Comparator<BlockPos> distanceSort;
        switch (containerSorting) {
            case ALPHABETICAL_ASC:
            case DISTANCE_ASC:
                distanceSort = COMPARE_DISTANCE;
                break;
            case ALPHABETICAL_DESC:
            case DISTANCE_DESC:
            default:
                distanceSort = BCUtil.reverseComparator(COMPARE_DISTANCE);
                break;
        }
        Comparator<BlockPos> alphaSort;
        switch (containerSorting) {
            case ALPHABETICAL_ASC:
                alphaSort = COMPARE_ALPHABETICAL;
                break;
            case ALPHABETICAL_DESC:
                alphaSort = BCUtil.reverseComparator(COMPARE_ALPHABETICAL);
                break;
            default:
                alphaSort = Comparator.comparingInt($ -> 0);
                break;
        }
        return StockroomCatalogContent.getFromStack(stack)
                .positions()
                .stream()
                .filter(e -> e.dimension() == level.dimension())
                .map(GlobalPos::pos)
                .filter(level::hasChunkAt)
                .filter(e -> {
                    TileEntity be = level.getBlockEntity(e);
                    return be != null && be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent();
                })
                .sorted(distanceSort)
                .sorted(alphaSort)
                .collect(java.util.stream.Collectors.toList());
    }

    public static List<StockroomCatalogItemEntry> calculateItems(List<BlockPos> positions, World level, StockroomCatalogSorting.Item itemSorting) {
        Map<ItemStack, StockroomCatalogItemEntry> tempItems = new LinkedHashMap<>();
        for (BlockPos pos : positions) {
            TileEntity be = level.getBlockEntity(pos);
            IItemHandler cap = be != null ? be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null) : null;
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
                        .filter(e -> (ItemStack.isSame(e, stack) && ItemStack.tagMatches(e, stack)))
                        .findFirst();
                StockroomCatalogItemEntry entry = optional
                        .map(itemStack -> tempItems.get(itemStack).add(count))
                        .orElseGet(() -> new StockroomCatalogItemEntry(originalStack));
                tempItems.put(optional.orElse(stack), entry.add(pos));
            }
        }
        Comparator<StockroomCatalogItemEntry> firstSort;
        switch (itemSorting) {
            case ALPHABETICAL_ASC:
                firstSort = COMPARE_COUNT;
                break;
            case ALPHABETICAL_DESC:
                firstSort = BCUtil.reverseComparator(COMPARE_COUNT);
                break;
            case COUNT_ASC:
                firstSort = COMPARE_NAME;
                break;
            case COUNT_DESC:
            default:
                firstSort = BCUtil.reverseComparator(COMPARE_NAME);
                break;
        }
        Comparator<StockroomCatalogItemEntry> secondSort;
        switch (itemSorting) {
            case ALPHABETICAL_ASC:
                secondSort = COMPARE_NAME;
                break;
            case ALPHABETICAL_DESC:
                secondSort = BCUtil.reverseComparator(COMPARE_NAME);
                break;
            case COUNT_ASC:
                secondSort = COMPARE_COUNT;
                break;
            case COUNT_DESC:
            default:
                secondSort = BCUtil.reverseComparator(COMPARE_COUNT);
                break;
        }
        return tempItems.values()
                .stream()
                .sorted(firstSort)
                .sorted(secondSort)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && context.isSecondaryUseActive()) {
            BlockPos pos = context.getClickedPos();
            World level = context.getLevel();
            BlockState state = level.getBlockState(pos);
            ItemStack stack = context.getItemInHand();
            StockroomCatalogContent list = StockroomCatalogContent.getFromStack(stack);
            boolean hasNeighbor = state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE;
            GlobalPos neighborPos = hasNeighbor ? GlobalPos.of(level.dimension(), pos.offset(ChestBlock.getConnectedDirection(state).getNormal())) : null;
            boolean hasPositionAtNeighbor = hasNeighbor && list.positions().contains(neighborPos);
            GlobalPos globalPos = hasPositionAtNeighbor ? neighborPos : GlobalPos.of(level.dimension(), pos);
            if (list.positions().contains(globalPos)) {
                StockroomCatalogContent.setOnStack(stack, list.remove(globalPos));
                player.displayClientMessage(new TranslationTextComponent(Translations.STOCKROOM_CATALOG_REMOVE_CONTAINER_KEY, BCUtil.getNameAtPos(level, pos)), true);
                return ActionResultType.SUCCESS;
            }
            TileEntity be = level.getBlockEntity(pos);
            IItemHandler cap = be != null ? be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, context.getClickedFace()).orElse(null) : null;
            if (cap != null) {
                StockroomCatalogContent.setOnStack(stack, list.add(globalPos));
                player.displayClientMessage(new TranslationTextComponent(Translations.STOCKROOM_CATALOG_ADD_CONTAINER_KEY, BCUtil.getNameAtPos(level, pos)), true);
                return ActionResultType.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            ClientUtil.openStockroomCatalogScreen(stack, player, hand);
        }
        return ActionResult.success(stack);
    }
}
