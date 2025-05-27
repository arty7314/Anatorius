package me.alpha432.oyvey.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {

    private static final List<String> friends = new ArrayList<>();

    // Arkadaş eklemek için basit bir method (daha sonra sen genişletebilirsin)
    public static void addFriend(String name) {
        if (!friends.contains(name)) {
            friends.add(name);
        }
    }

    public static void removeFriend(String name) {
        friends.remove(name);
    }

    // Bir entity ölü mü?
    public static boolean isDead(Entity entity) {
        if (entity == null) return true;
        if (!(entity instanceof LivingEntity)) return false;
        LivingEntity livingEntity = (LivingEntity) entity;
        return livingEntity.isRemoved() || livingEntity.getHealth() <= 0.0F;
    }

    // Bir entity dost mu? (isim bazlı kontrol)
    public static boolean isFriend(Entity entity) {
        if (entity == null) return false;
        return friends.contains(entity.getName().getString());
    }
}
