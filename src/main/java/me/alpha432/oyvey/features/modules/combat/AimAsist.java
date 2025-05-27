package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class AimAsist extends Module {

    public AimAsist() {
        super("AimAssist", "Yakındaki hedefe nişan almanı kolaylaştırır", Category.COMBAT, true, false, false);
    }

    private final float range = 6.0f; // maksimum hedef algılama mesafesi
    private final float smooth = 5.0f; // yumuşaklık değeri: ne kadar yüksekse, o kadar yavaş döner

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.currentScreen != null)
            return;

        LivingEntity target = getClosestTarget();
        if (target == null)
            return;

        rotateTowards(target);
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

    private LivingEntity getClosestTarget() {
        LivingEntity closest = null;
        double closestDistance = range;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player
                    && player != mc.player
                    && !player.isDead()
                    && mc.player.squaredDistanceTo(player) <= range * range) {

                if (closest == null || mc.player.distanceTo(player) < closestDistance) {
                    closest = player;
                    closestDistance = mc.player.distanceTo(player);
                }
            }
        }

        return closest;
    }

    private void rotateTowards(LivingEntity target) {
        double deltaX = target.getX() - mc.player.getX();
        double deltaY = (target.getY() + target.getEyeHeight(target.getPose())) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double deltaZ = target.getZ() - mc.player.getZ();

        double dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yawToTarget = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0);
        float pitchToTarget = (float) -Math.toDegrees(Math.atan2(deltaY, dist));

        mc.player.setYaw(smoothAngle(mc.player.getYaw(), yawToTarget, smooth));
        mc.player.setPitch(smoothAngle(mc.player.getPitch(), pitchToTarget, smooth));
    }

    private float smoothAngle(float current, float target, float smoothFactor) {
        float delta = MathHelper.wrapDegrees(target - current);
        return current + delta / smoothFactor;
    }
}
