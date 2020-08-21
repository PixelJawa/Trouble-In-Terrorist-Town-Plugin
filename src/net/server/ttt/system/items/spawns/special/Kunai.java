package net.server.ttt.system.items.spawns.special;

import net.server.ttt.main.Main;
import net.server.ttt.system.handling.HandlePlayer;
import net.server.ttt.system.items.abstracts.TTTItemWeaponThrowable;
import net.server.ttt.system.utils.enums.WeaponType;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Kunai extends TTTItemWeaponThrowable {

    static Map<Player, Long> lastFireMap = new HashMap<>();
    static double damage = 3.25;
    static double headMultiplier = 2.0;
    static double fireRatePrimary = 6; // shots per second
    static double fireRateSecondary = 0.8; // shots per second
    static String weaponName = ChatColor.DARK_PURPLE + "Kunai";
    static boolean isHoldable = false;

    static double range = 250;
    static double accuracyPrimary = 0.90;
    static double accuracySecondary = 0.40;

    static int shotsSecondary = 4;

    static WeaponType weaponType = WeaponType.SUPER;
    static String weaponId = "ttt_item_weapon_kunai";

    // weapon / ammo (Kunai are throwing knives so that weapon and ammo item is the same)
    static ItemStack weapon = new ItemStack(Material.FEATHER, 1);
    static {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta meta = weapon.getItemMeta();

        meta.setDisplayName(weaponName);

        lore.add(Main.hideText(weaponId));
        lore.add(ChatColor.GRAY + "Mid ranged throwing knives with high damage output");
        lore.add(ChatColor.GRAY + "[Left Click]: Throw " + shotsSecondary + " knives at once.");
        lore.add(ChatColor.GRAY + " ");
        lore.add(ChatColor.GRAY + "damage: " + ChatColor.DARK_GREEN + damage);
        lore.add(ChatColor.GRAY + "head multiplier: " + ChatColor.DARK_GREEN + headMultiplier + "x");
        lore.add(ChatColor.GRAY + "weapon type: " + ChatColor.DARK_GREEN + weaponType.toString());

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        weapon.setItemMeta(meta);
    }

    public boolean canFireAgain(Player player) {
        if(!lastFireMap.containsKey(player)) {
            return true;
        }
        else {
            return System.currentTimeMillis() - lastFireMap.get(player) >= 1 / fireRatePrimary * 1000;
        }
    }
    public boolean hasOneAmmo(Player player) {
        return player.getInventory().containsAtLeast(weapon, 1);
    }
    public boolean conditionsLeftClear(Player player) {
        return hasOneAmmo(player) && canFireAgain(player);
    }
    public boolean conditionsRightClear(Player player) {
        return hasOneAmmo(player) && canFireAgain(player);
    }

    public void consumeAmmo(Player player) {
        ItemStack stack = weapon.clone();
        stack.setAmount(1);

        player.getInventory().removeItem(stack);
    }

    public String getName() {
        return weaponName;
    }
    public String getId() {
        return weaponId;
    }
    public WeaponType getWeaponType() {
        return weaponType;
    }
    public ItemStack getItemStack() {
        return weapon;
    }
    public ItemStack getAmmoStack() {
        return weapon;
    }
    public boolean getIsHoldable() { return isHoldable; }

    public void leftAction(Player player, ItemStack item) {

        for(int i = 0; i< shotsSecondary; i++) {
            if(!hasOneAmmo(player)) break;

            consumeAmmo(player);
            shoot(player, accuracySecondary);
        }

        shotLeftSound(player.getEyeLocation());

        long add = (long) ( (1 / fireRateSecondary * 1000) - (1 / fireRatePrimary * 1000) );
        lastFireMap.put(player, System.currentTimeMillis() + add);
    }
    public void rightAction(Player player, ItemStack item) {
        consumeAmmo(player);
        shotRightSound(player.getEyeLocation());
        shoot(player, accuracyPrimary);
        lastFireMap.put(player, System.currentTimeMillis());
    }
    public void sneakStartAction(Player player, ItemStack item) {
    }
    public void sneakEndAction(Player player, ItemStack item) {
    }

    public void shoot(Player player, double accuracy) {

        World world = player.getWorld();
        Location loc = player.getEyeLocation();
        loc.setY(loc.getY() - 0.3);
        Vector vec = loc.getDirection();

        Item item = world.dropItem(loc, new ItemStack(Material.FEATHER));

        item.setMetadata("ttt_entity_bullet", new FixedMetadataValue(Main.getInstance(), true));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setGravity(true);

        Vector direction = vec.clone().multiply(2); // change to change bullet speed
        Vector v = Vector.getRandom();
        v.setX(v.getX() - 0.5f);
        v.setY(v.getY() - 0.5f);
        v.setZ(v.getZ() - 0.5f);
        v.multiply(1 - accuracy);
        direction.add(v);

        item.setVelocity(direction);

        new BukkitRunnable() {

            Location prevLoc = item.getLocation();
            Location entLoc;
            Vector dir;
            Vector preVec = item.getVelocity();
            int count = 0;

            public void run() {

                dir = item.getVelocity();

                entLoc = item.getLocation();
                //item.setVelocity(item);
                shotParticles(entLoc);

                if(dir.length() == 0) {
                    cancel();
                    item.remove();
                }

                // check for entity collision
                RayTraceResult rayTraceResult = world.rayTraceEntities(prevLoc, dir, prevLoc.distance(entLoc));
                if (rayTraceResult != null
                        && rayTraceResult.getHitEntity() != null
                        && rayTraceResult.getHitEntity() instanceof LivingEntity
                        && rayTraceResult.getHitEntity() != player) {
                    HandlePlayer.damageTarget((LivingEntity) rayTraceResult.getHitEntity(), player, damage, weaponName);

                    item.remove();
                    cancel();
                }

                // check for wall collision
                RayTraceResult blockRay = world.rayTraceBlocks(prevLoc, preVec, prevLoc.distance(entLoc) + 0.3, FluidCollisionMode.NEVER, true);
                if (blockRay != null && blockRay.getHitBlock() != null) {

                    item.remove();
                    cancel();
                }

                // check for range limit
                if(entLoc.distance(loc) > range) {

                    item.remove();
                    cancel();
                }

                prevLoc = item.getLocation();
                preVec = item.getVelocity();
                count ++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
    public void shotParticles(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(Particle.CRIT_MAGIC, loc, 1, 0, 0, 0, 0, null, true);
        world.spawnParticle(Particle.END_ROD, loc, 1, 0, 0, 0, 0.05, null, true);
    }

    public void shotRightSound(Location loc) {
        World world = loc.getWorld();
        world.playSound(loc, Sound.ITEM_TRIDENT_THROW, 20f,0.6f);
    }
    public void shotLeftSound(Location loc) {
        World world = loc.getWorld();
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 20f,1.6f);
        world.playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 20f, 1.2f);
    }

}
