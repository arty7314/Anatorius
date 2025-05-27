package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.client.network.ClientPlayerEntity;

public class FullBright extends Module {

    public FullBright() {
        super("FullBright", "Sonsuz gece görüşü verir", Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player != null) {
            StatusEffectInstance effect = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (effect == null || effect.getDuration() <= 210) {
                // 6000 = 5 dakika. Sürekli yenilenmesi için 10 saniye altına düşünce yeniden verilir.
                mc.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 6000, 0, false, false));
            }
        }
    }

    @Override
    public void onRender(MatrixStack matrices, float tickDelta) {

    }

    @Override
    public void onRenderWorldLast(MatrixStack matrices, float tickDelta) {

    }

    @Override
    public void onRender3D(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float tickDelta) {

    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
