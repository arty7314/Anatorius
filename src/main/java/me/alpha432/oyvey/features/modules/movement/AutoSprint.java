package me.alpha432.oyvey.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.UpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Always sprint when moving", Category.MOVEMENT, true, false, false);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player == null || mc.world == null || player.input == null) return;

        // Oyuncu ileriye gidiyor ve eğilmiyorsa
        player.setSprinting(mc.options.forwardKey.isPressed() && !player.isSneaking());
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
