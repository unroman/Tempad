package me.codexadrian.tempad.tempad;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TempadItem extends Item {

    public TempadItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) TempadClient.openScreen(interactionHand);
        return InteractionResultHolder.success(stack);
    }

    public static void summonTimeDoor(LocationData locationData, Player player, int color) {
        TimedoorEntity timedoor = new TimedoorEntity(Services.REGISTRY.getTimedoor(), player.level);
        var dir = player.getDirection();
        timedoor.setColor(color);
        timedoor.setLocation(locationData);
        timedoor.setOwner(player.getUUID());
        var position = player.position();
        var distance = Tempad.getTempadConfig().getDistanceFromPlayer();
        timedoor.setPos(position.x() + dir.getStepX() * distance, position.y(), position.z() + dir.getStepZ() * distance);
        timedoor.setYRot(dir.getOpposite().toYRot());
        player.level.addFreshEntity(timedoor);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        MutableComponent componentToAdd = null;
        if(stack.hasTag()) { //IntelliJ wouldnt leave me alone
            if(stack.getTag().contains(Constants.TIMER_NBT)) {
                long cooldownTimeTag = stack.getTag().getLong(Constants.TIMER_NBT);
                Instant time = Instant.ofEpochSecond(cooldownTimeTag);
                if(Instant.now().isBefore(time)) {
                    Duration between = Duration.of(cooldownTimeTag - Instant.now().getEpochSecond(), ChronoUnit.SECONDS);
                    String seconds = (between.toSecondsPart() < 9 ? "0" : "") + between.toSecondsPart();
                    String minutes = (between.toMinutesPart() < 9 ? "0" : "") + between.toMinutesPart();
                    componentToAdd = new TranslatableComponent("tooltip.tempad.timeleft").append(" " + minutes + ":" + seconds);
                }
            }
        }
        componentToAdd = componentToAdd == null ? new TranslatableComponent("tooltip.tempad.fullycharged") : componentToAdd;
        components.add(componentToAdd.withStyle(ChatFormatting.GRAY));
    }
}
