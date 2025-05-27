package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.manager.NoRenderManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class NoRender extends Module {

    public NoRender() {
        super("NoRender", "End kristali patlama partiküllerini engeller", Category.RENDER, true, false, false);
    }

    @Override
    public void onEnable() {
        // Mod açıldığında aktif hale getiriyoruz
        NoRenderManager.active = true;
    }

    @Override
    public void onDisable() {
        // Mod kapatıldığında devre dışı bırakıyoruz
        NoRenderManager.active = false;
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
}
