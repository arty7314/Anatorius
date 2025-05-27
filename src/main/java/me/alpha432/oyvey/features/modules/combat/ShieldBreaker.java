package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;

public class ShieldBreaker extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public ShieldBreaker() {
        super("ShieldBreaker", "Rakibin kalkanını kırmaya çalışır", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null) return;

        // Eğer elimizde kılıç yoksa çık
        if (player.getMainHandStack().getItem() != Items.IRON_SWORD &&
                player.getMainHandStack().getItem() != Items.DIAMOND_SWORD &&
                player.getMainHandStack().getItem() != Items.NETHERITE_SWORD) {
            return;
        }

        // Oyuncuya yakın (örneğin 4 blok) diğer oyuncuları bul
        Box box = player.getBoundingBox().expand(4.0, 2.0, 4.0);
        for (PlayerEntity target : mc.world.getEntitiesByClass(PlayerEntity.class, box, e -> e != player)) {
            // Eğer hedef kalkanı kullanıyorsa (elinde kalkan varsa ve blok yapıyorsa)
            if (target.getActiveItem().getItem() == Items.SHIELD) {
                // Hedefi vurmaya çalış
                attack(target);
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

    private void attack(PlayerEntity target) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // Hedefe saldırı paketi gönder
        mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(target, false));
        // Oyuncu da animasyon oynasın (isteğe bağlı)
        player.swingHand(Hand.MAIN_HAND);
    }
}
