package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.event.impl.UpdateEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import com.google.common.eventbus.Subscribe;

public class AutoTotemSmart extends Module {

    public enum OffhandMode {
        TOTEM,
        CRYSTAL,
        GAPPLE
    }
    private OffhandMode mode = OffhandMode.TOTEM;
    private final float healthThreshold = 12.0f; // 6 kalp
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public AutoTotemSmart() {
        super("AutoOffhand", "Dynamic offhand management", Category.COMBAT, true, false, false);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        ClientPlayerEntity player = mc.player;

        float health = player.getHealth() + player.getAbsorptionAmount();
        ItemStack mainHand = player.getMainHandStack();

        int totemSlot = findItem(Items.TOTEM_OF_UNDYING);
        int crystalSlot = findItem(Items.END_CRYSTAL);
        int gappleSlot = findItem(Items.ENCHANTED_GOLDEN_APPLE);

        // Öncelik: Can düşükse her şeyi bırak totem
        if (health <= healthThreshold && !isHoldingOffhand(Items.TOTEM_OF_UNDYING) && totemSlot != -1) {
            swapToOffhand(totemSlot);
            return;
        }

        // Kılıç veya kazma varsa ve sağ tık basılıysa -> Gapple'a geç
        boolean holdingSwordOrPickaxe = mainHand.getItem() == Items.NETHERITE_SWORD
                || mainHand.getItem() == Items.DIAMOND_SWORD
                || mainHand.getItem() == Items.IRON_SWORD
                || mainHand.getItem() == Items.NETHERITE_PICKAXE
                || mainHand.getItem() == Items.DIAMOND_PICKAXE
                || mainHand.getItem() == Items.IRON_PICKAXE;

        if (holdingSwordOrPickaxe && mc.options.useKey.isPressed()) {
            if (!isHoldingOffhand(Items.ENCHANTED_GOLDEN_APPLE) && gappleSlot != -1) {
                swapToOffhand(gappleSlot);
            } else if (isHoldingOffhand(Items.ENCHANTED_GOLDEN_APPLE)) {
                mc.interactionManager.interactItem(player, Hand.OFF_HAND);
            }
            return;
        }

        // Diğer modlara göre item koy
        if (mode == OffhandMode.TOTEM && !isHoldingOffhand(Items.TOTEM_OF_UNDYING) && totemSlot != -1) {
            swapToOffhand(totemSlot);
        } else if (mode == OffhandMode.CRYSTAL && !isHoldingOffhand(Items.END_CRYSTAL) && crystalSlot != -1) {
            swapToOffhand(crystalSlot);
        } else if (mode == OffhandMode.GAPPLE && !isHoldingOffhand(Items.ENCHANTED_GOLDEN_APPLE) && gappleSlot != -1) {
            swapToOffhand(gappleSlot);
        }
    }

    private boolean isHoldingOffhand(net.minecraft.item.Item item) {
        return mc.player.getOffHandStack().getItem() == item;
    }

    private int findItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) return i;
        }
        return -1;
    }

    private void swapToOffhand(int slot) {
        if (slot < 0 || slot > 35) return;
        mc.interactionManager.clickSlot(0, slot < 9 ? slot + 36 : slot, 40, net.minecraft.screen.slot.SlotActionType.SWAP, mc.player);
    }

    public void setMode(OffhandMode mode) {
        this.mode = mode;
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
