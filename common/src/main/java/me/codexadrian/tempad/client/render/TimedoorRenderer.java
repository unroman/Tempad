package me.codexadrian.tempad.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.entity.TimedoorEntity;

import me.codexadrian.tempad.common.utils.ShaderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class TimedoorRenderer extends EntityRenderer<TimedoorEntity> {

    public TimedoorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TimedoorEntity entity, float yaw, float deltaTime, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int light) {
        float width = 1.4F;
        float height = 2.3F;
        float depth = .4F;
        int closingTime = entity.getClosingTime();
        int tickLength = TimedoorEntity.ANIMATION_LENGTH;
        int phaseLength = (tickLength) / 2;
        int ticks = entity.tickCount;
        float animation = (ticks + deltaTime) / tickLength;

        if (ticks < phaseLength) {
            width = Mth.lerp(animation * 2, 0, width);
            height = .2F;
        }

        if (ticks >= phaseLength && ticks < tickLength) {
            height = Mth.lerp((animation - 0.5F) * 2, .2F, height);
        }

        if (closingTime != -1) {
            if (ticks > closingTime && ticks < closingTime + phaseLength) {
                height = Mth.lerp(1 - (animation - (float) closingTime / tickLength) * 2, .2F, height);
            }

            if (ticks >= closingTime + phaseLength) {
                width = Mth.lerp(1 - (animation - (float) closingTime / tickLength - 0.5F) * 2, 0, width);
                height = .2F;
            }
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        poseStack.translate(0, 1.15F, 0);
        var model = poseStack.last().pose();
        renderTimedoor(model, multiBufferSource, width, height, depth, light, entity.getColor());
        super.render(entity, yaw, deltaTime, poseStack, multiBufferSource, light);
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TimedoorEntity entity) {
        return new ResourceLocation(Tempad.MODID, "");
    }

    public void renderTimedoor(Matrix4f model, MultiBufferSource multiBufferSource, float width, float height, float depth, int i, int color) {
        float xBound = width * 0.5F;
        float yBound = height * 0.5F - .01F;
        float zBound = depth * -0.5F;

        var buffer = multiBufferSource.getBuffer(ShaderUtils.getTimedoorShaderType());
        //Front
        float red = ((color & 0xFF0000) >> 16) / 255.0f;
        float green = ((color & 0xFF00) >> 8) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        float alpha = 1F;
        buffer.vertex(model, -xBound, yBound, -zBound).color(red, green, blue, alpha).uv(0, 0).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, -zBound).color(red, green, blue, alpha).uv(0, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, -zBound).color(red, green, blue, alpha).uv(1, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, -zBound).color(red, green, blue, alpha).uv(1, 0).uv2(i).endVertex();

        //Back
        buffer.vertex(model, xBound, yBound, zBound).color(red, green, blue, alpha).uv(0, 0).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, zBound).color(red, green, blue, alpha).uv(0, 1).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, zBound).color(red, green, blue, alpha).uv(1, 1).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, zBound).color(red, green, blue, alpha).uv(1, 0).uv2(i).endVertex();

        //Top
        buffer.vertex(model, -xBound, yBound, zBound).color(red, green, blue, alpha).uv(0, 0).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, -zBound).color(red, green, blue, alpha).uv(0, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, -zBound).color(red, green, blue, alpha).uv(1, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, zBound).color(red, green, blue, alpha).uv(1, 0).uv2(i).endVertex();

        //Bottom
        buffer.vertex(model, -xBound, -yBound, -zBound).color(red, green, blue, alpha).uv(0, 0).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, zBound).color(red, green, blue, alpha).uv(0, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, zBound).color(red, green, blue, alpha).uv(1, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, -zBound).color(red, green, blue, alpha).uv(1, 0).uv2(i).endVertex();

        //Left
        buffer.vertex(model, -xBound, yBound, zBound).color(red, green, blue, alpha).uv(0, 0).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, zBound).color(red, green, blue, alpha).uv(0, 1).uv2(i).endVertex();
        buffer.vertex(model, -xBound, -yBound, -zBound).color(red, green, blue, alpha).uv(1, 1).uv2(i).endVertex();
        buffer.vertex(model, -xBound, yBound, -zBound).color(red, green, blue, alpha).uv(1, 0).uv2(i).endVertex();

        //Right
        buffer.vertex(model, xBound, yBound, -zBound).color(red, green, blue, alpha).uv(0, 0).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, -zBound).color(red, green, blue, alpha).uv(0, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, -yBound, zBound).color(red, green, blue, alpha).uv(1, 1).uv2(i).endVertex();
        buffer.vertex(model, xBound, yBound, zBound).color(red, green, blue, alpha).uv(1, 0).uv2(i).endVertex();

    }
}
