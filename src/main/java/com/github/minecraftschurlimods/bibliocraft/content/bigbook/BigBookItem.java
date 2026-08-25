package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;

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
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            ClientUtil.openBigBookScreen(stack, player, hand);
        }
        return ActionResult.success(stack);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        WrittenBigBookContent content = WrittenBigBookContent.getFromStack(stack);
        if (!content.equals(WrittenBigBookContent.DEFAULT)) {
            String title = content.title();
            if (title != null && !title.trim().isEmpty()) return new StringTextComponent(title);
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, World level, List<ITextComponent> tooltipComponents, ITooltipFlag tooltipFlag) {
        WrittenBigBookContent content = WrittenBigBookContent.getFromStack(stack);
        if (!content.equals(WrittenBigBookContent.DEFAULT)) {
            String author = content.author();
            if (author != null && !author.trim().isEmpty()) {
                tooltipComponents.add(new TranslationTextComponent(Translations.VANILLA_BY_AUTHOR_KEY, author).withStyle(TextFormatting.GRAY));
            }
            tooltipComponents.add(new TranslationTextComponent("book.generation." + content.generation()).withStyle(TextFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
