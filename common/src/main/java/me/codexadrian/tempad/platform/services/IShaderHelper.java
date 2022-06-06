package me.codexadrian.tempad.platform.services;

import me.codexadrian.tempad.BlurReloader;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.RenderType;

public interface IShaderHelper {

    EffectInstance getTimedoorShader();

    void setTimeDoorShader(EffectInstance shader);

    EffectInstance getTimedoorWhiteShader();

    void setTimedoorWhiteShader(EffectInstance shader);

    RenderType getTimedoorShaderType();

    BlurReloader getBlurReloader();
}
