package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class FreeCam extends Module {

    private Vec3d oldPos;
    private float oldYaw, oldPitch;
    private boolean oldFlying;
    private OtherClientPlayerEntity fakePlayer;

    public FreeCam() {
        super("FreeCam", "Kamerayı bedenden ayırır ve özgürce uçmanı sağlar", Category.MISC, false, false, false);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        // Pozisyonu ve yönü kaydet
        oldPos = mc.player.getPos();
        oldYaw = mc.player.getYaw();
        oldPitch = mc.player.getPitch();
        oldFlying = mc.player.getAbilities().flying;

        // Fake player oluştur
        fakePlayer = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
        fakePlayer.copyPositionAndRotation(mc.player);
        mc.world.addEntity(fakePlayer); // Fake player'ı dünyaya ekle

        // Kamera serbestçe uçsun
        mc.player.noClip = true;
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().setFlySpeed(2.5f); // Ayarlanabilir hız
        mc.setCameraEntity(mc.player); // Kamera kendi üzerinden çalışsın
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        // Eski konuma geri dön
        mc.player.setPosition(oldPos);
        mc.player.setYaw(oldYaw);
        mc.player.setPitch(oldPitch);
        mc.player.noClip = false;
        mc.player.getAbilities().flying = oldFlying;

        // Fake player'ı sil
        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // noClip aktif kalsın
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
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
