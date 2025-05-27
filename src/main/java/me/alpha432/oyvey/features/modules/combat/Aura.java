package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class Aura extends Module {

    private final float range = 6.0f;
    private final boolean throughWalls = true;

    public Aura() {
        super("KillAura", "Elinde kılıç varken yakındaki entity'leri vurur", Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        // ✅ Eğer elinde kılıç yoksa çık
        Item item = mc.player.getMainHandStack().getItem();
        if (!(item == Items.NETHERITE_SWORD || item == Items.DIAMOND_SWORD ||
                item == Items.IRON_SWORD || item == Items.STONE_SWORD ||
                item == Items.WOODEN_SWORD || item == Items.GOLDEN_SWORD)) return;

        // ✅ Vurma cooldown dolmamışsa çık
        if (mc.player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        LivingEntity target = null;
        double closestDistance = range;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (!entity.isAlive()) continue;
            if (!throughWalls && !mc.player.canSee(entity)) continue;

            double distance = mc.player.distanceTo(entity);
            if (distance <= closestDistance) {
                closestDistance = distance;
                target = (LivingEntity) entity;
            }
        }

        if (target != null) {
            attack(target);
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

    private void attack(LivingEntity entity) {
        if (mc.interactionManager != null && mc.player != null) {
            mc.interactionManager.attackEntity(mc.player, entity);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
