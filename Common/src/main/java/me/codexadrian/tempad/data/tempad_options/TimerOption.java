package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TimerOption extends TempadOption {
    public static final TimerOption NORMAL_INSTANCE = new TimerOption(TempadType.NORMAL);
    public static final TimerOption ADVANCED_INSTANCE = new TimerOption(TempadType.HE_WHO_REMAINS);

    protected TimerOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        if(stack.getTag() != null && stack.getTag().contains(Constants.TIMER_NBT)) {
            long cooldownTimeTag = stack.getTag().getLong(Constants.TIMER_NBT);
            Instant cooldownTime = Instant.ofEpochSecond(cooldownTimeTag);
            return Instant.now().isAfter(cooldownTime);
        }
        return true;
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        stack.getOrCreateTag().putLong(Constants.TIMER_NBT, Instant.now().plusSeconds(getType().getOptionConfig().getCooldownTime()).getEpochSecond());
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        MutableComponent componentToAdd = null;
        if(stack.getTag() != null) {
            if(stack.getTag().contains(Constants.TIMER_NBT)) {
                if(!this.canTimedoorOpen(null, stack)) {
                    long seconds = Instant.now().until(Instant.ofEpochSecond(stack.getTag().getLong(Constants.TIMER_NBT)), ChronoUnit.MILLIS);
                    componentToAdd = Component.translatable("tooltip.tempad.timeleft").append(DurationFormatUtils.formatDuration(seconds, "mm:ss", true));
                }
            }
        }
        componentToAdd = componentToAdd == null ? Component.translatable("tooltip.tempad.fullycharged") : componentToAdd;
        components.add(componentToAdd.withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int durabilityBarWidth(ItemStack stack) {
        return 0;
    }
}
