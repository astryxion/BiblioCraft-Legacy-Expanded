package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class BigBookItem extends Item {
    private final boolean isWritten;

    public BigBookItem(boolean isWritten) {
        super(new Properties().stacksTo(1));
        this.isWritten = isWritten;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isWritten && !WrittenBigBookContent.getFromStack(stack).equals(WrittenBigBookContent.DEFAULT);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            ClientUtil.openBigBookScreen(stack, player, hand);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        WrittenBigBookContent content = WrittenBigBookContent.getFromStack(stack);
        if (!content.equals(WrittenBigBookContent.DEFAULT)) {
            String title = content.title();
            if (title != null && !title.trim().isEmpty()) return Component.literal(title);
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        WrittenBigBookContent content = WrittenBigBookContent.getFromStack(stack);
        if (!content.equals(WrittenBigBookContent.DEFAULT)) {
            String author = content.author();
            if (author != null && !author.trim().isEmpty()) {
                tooltipComponents.add(Component.translatable(Translations.VANILLA_BY_AUTHOR_KEY, author).withStyle(ChatFormatting.GRAY));
            }
            tooltipComponents.add(Component.translatable("book.generation." + content.generation()).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
