package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class NoSlow extends Module {

    public NoSlow() {
        super("NoSlow", "Yemek yerken hız artışı sağlar", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player == null || mc.world == null) return;

        if (player.isUsingItem()) {
            // Paket göndererek yavaşlamayı engelle
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                    player.getBlockPos().up(),
                    Direction.UP
            ));

            // Sprinti zorla açık tut
            player.setSprinting(true);

            // Hareket hızını artır (örneğin 1.2 katı hızla hareket ettir)
            Vec3d velocity = player.getVelocity();

            // Yön vektörünü al, normalize et ve hız çarpanı uygula
            Vec3d look = player.getRotationVec(1.0F).normalize();

            // İstersen sadece XZ ekseninde hız artışı yap
            double speedMultiplier = 1.3; // İstediğin hıza göre ayarla

            double newVelX = look.x * speedMultiplier;
            double newVelZ = look.z * speedMultiplier;

            // Y eksenine dokunmuyoruz (uçma veya zıplama etkilenmesin)
            player.setVelocity(newVelX, velocity.y, newVelZ);
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
}
