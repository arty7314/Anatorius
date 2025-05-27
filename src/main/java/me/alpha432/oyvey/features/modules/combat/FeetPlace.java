package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.UpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class FeetPlace extends Module {
    private boolean centered = false;

    public FeetPlace() {
        super("FeetPlace", "Places blocks around feet + centers player", Category.COMBAT, true, false, false);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null) return;

        // Zıplıyorsa mod kapansın
        if (player.getVelocity().y > 0.1 && !player.isOnGround()) {

            this.toggle();
            return;
        }

        // Sadece yere temas ediyorsa çalışsın
        if (!player.isOnGround()) return;

        // Oyuncuyu ortala
        if (!centered) {
            Vec3d pos = new Vec3d(player.getBlockX() + 0.5, player.getY(), player.getBlockZ() + 0.5);
            player.setPosition(pos.x, pos.y, pos.z);
            centered = true;
        }

        int obsidianSlot = findItemSlot(Items.OBSIDIAN);
        int enderSlot = findItemSlot(Items.ENDER_CHEST);
        int blockSlot = obsidianSlot != -1 ? obsidianSlot : enderSlot;
        if (blockSlot == -1) return;

        BlockPos base = player.getBlockPos();
        BlockPos[] surround = new BlockPos[] {
                base.add(1, 0, 0),
                base.add(-1, 0, 0),
                base.add(0, 0, 1),
                base.add(0, 0, -1)
        };

        for (BlockPos pos : surround) {
            // Eğer pozisyon doluysa geç
            if (!mc.world.getBlockState(pos).isReplaceable()) continue;

            boolean placed = tryPlaceBlock(pos, blockSlot);

            // Altı da boşsa destek bloğu koy
            if (!placed && mc.world.getBlockState(pos.down()).isReplaceable()) {
                tryPlaceBlock(pos.down(), blockSlot); // support block
                tryPlaceBlock(pos, blockSlot); // sonra asıl surround bloğu tekrar dene
            }
        }
    }

    @Override
    public void onDisable() {
        centered = false;
        super.onDisable();
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

    private boolean tryPlaceBlock(BlockPos pos, int slot) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null) return false;

        for (Direction dir : Direction.values()) {
            BlockPos support = pos.offset(dir);
            if (!mc.world.getBlockState(support).isAir() && !mc.world.getBlockState(support).isReplaceable()) {
                Vec3d eyesPos = player.getCameraPosVec(1.0f);
                Vec3d hitVec = Vec3d.ofCenter(support).add(Vec3d.of(dir.getVector()).multiply(0.5));
                Vec3d diff = hitVec.subtract(eyesPos);

                double distXZ = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
                float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90f;
                float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, distXZ));

                sendSilentRotationPacket(mc, yaw, pitch);

                BlockHitResult bhr = new BlockHitResult(hitVec, dir.getOpposite(), support, false);

                int prevSlot = player.getInventory().getSelectedSlot();
                player.getInventory().setSelectedSlot(slot);

                ActionResult result = mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, bhr);
                if (result.isAccepted()) {
                    player.swingHand(Hand.MAIN_HAND);
                    player.getInventory().setSelectedSlot(prevSlot);
                    return true;
                }

                player.getInventory().setSelectedSlot(prevSlot);
            }
        }
        return false;
    }

    private void sendSilentRotationPacket(MinecraftClient mc, float yaw, float pitch) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.LookAndOnGround(
                normalizeAngle(yaw),
                normalizeAngle(pitch),
                mc.player.isOnGround(),
                true
        );
        mc.getNetworkHandler().sendPacket(packet);
    }

    private float normalizeAngle(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) angle -= 360.0f;
        if (angle < -180.0f) angle += 360.0f;
        return angle;
    }

    private int findItemSlot(Item item) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) return i;
        }
        return -1;
    }
}
