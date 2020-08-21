package net.server.ttt.system.items.spawns.generic;

import net.server.ttt.main.Main;
import net.server.ttt.system.handling.HandlePlayer;
import net.server.ttt.system.items.abstracts.TTTItemWeapon;
import net.server.ttt.system.items.abstracts.TTTItemWeaponShootable;
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
import java.util.List;
import java.util.Map;

public class Shotgun extends TTTItemWeaponShootable {

    static Map<Player, Long> lastFireMap = new HashMap<>();
    static Map<Player, Integer> magazineMap = new HashMap<>();
    static List<Player> reloading = new ArrayList<>();

    static double damage = 3.2; // per shot
    static double shots = 8;
    static double headMultiplier = 1.2;
    static double fireRate = 0.9; // shots per second
    static String weaponName = ChatColor.LIGHT_PURPLE + "Shotty";
    static boolean isHoldable = false;

    static double reloadTime = 3.5;
    static int magSize = 6;
    static double range = 20;

    static WeaponType weaponType = WeaponType.PRIMARY;
    static int ammoBatchAmount = 8;
    static String weaponId = "ttt_item_weapon_shotgun";
    static String ammoId = "ttt_item_ammo_shotgun";

    // weapon
    static ItemStack weapon = new ItemStack(Material.IRON_HORSE_ARMOR, 1);
    static {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta meta = weapon.getItemMeta();

        meta.setDisplayName(weaponName);

        lore.add(Main.hideText(weaponId));
        lore.add(ChatColor.GRAY + "A short range weapon with extremely high burst damage.");
        lore.add(ChatColor.GRAY + " ");
        lore.add(ChatColor.GRAY + "damage/shot: " + ChatColor.DARK_GREEN + damage);
        lore.add(ChatColor.GRAY + "head multiplier: " + ChatColor.DARK_GREEN + headMultiplier + "x");
        lore.add(ChatColor.GRAY + "pew-pew's/sec: " + ChatColor.DARK_GREEN + fireRate);
        lore.add(ChatColor.GRAY + "weapon type: " + ChatColor.DARK_GREEN + weaponType.toString());

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        weapon.setItemMeta(meta);
    }

    // ammo
    static ItemStack ammo = new ItemStack(Material.MELON_SEEDS, ammoBatchAmount);
    static {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta meta = ammo.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_GRAY + "Shotty Ammo");
        lore.add(Main.hideText(ammoId));

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        ammo.setItemMeta(meta);
    }

    public boolean hasOneAmmo(Player player) {
        if(player.getInventory().containsAtLeast(ammo, 1))
            return true;
        else
            return false;
    }
    public boolean magNotFull(Player player) {
        if(!magazineMap.containsKey(player)) {
            magazineMap.put(player, magSize);
            return false;
        }
        else return magazineMap.get(player) != magSize;
    }
    public boolean conditionsLeftClear(Player player) {
        return hasOneAmmo(player) && magNotFull(player) && isNotReloading(player);
    }

    public boolean isMagLoaded(Player player) {
        // check if the player has ammo in his mag

        if(!magazineMap.containsKey(player)) {
            magazineMap.put(player, magSize);
            return true;
        }
        else
            return magazineMap.get(player) > 0;
    }
    public boolean canFireAgain(Player player) {
        if(!lastFireMap.containsKey(player)) {
            return true;
        }
        else {
            return System.currentTimeMillis() - lastFireMap.get(player) >= 1 / fireRate * 1000;
        }
    }
    public boolean isNotReloading(Player player) {
        return !reloading.contains(player);
    }
    public boolean conditionsRightClear(Player player) {
        return isMagLoaded(player) && canFireAgain(player) && isNotReloading(player);
    }

    public void consumeAmmo(Player player, ItemStack item) {
        magazineMap.put(player, magazineMap.get(player) - 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(weaponName + ChatColor.WHITE + " -|- " + ChatColor.GRAY + magazineMap.get(player));
        item.setItemMeta(meta);
    }

    public String getName() {
        return weaponName;
    }
    public String getId() {
        return weaponId;
    }
    public String getAmmoId() {
        return ammoId;
    }
    public WeaponType getWeaponType() {
        return weaponType;
    }
    public ItemStack getItemStack() {
        return weapon;
    }
    public ItemStack getAmmoStack() {
        return ammo;
    }
    public boolean getIsHoldable() { return isHoldable; }

    public void leftAction(Player player, ItemStack item) {
        reload(player, item);
    }
    public void rightAction(Player player, ItemStack item) {
        consumeAmmo(player, item);
        shotSound(player.getEyeLocation());
        shoot(player);
        lastFireMap.put(player, System.currentTimeMillis());
    }
    public void sneakStartAction(Player player, ItemStack item) {
    }
    public void sneakEndAction(Player player, ItemStack item) {
    }

    public void shoot(Player player) {

        World world = player.getWorld();
        Location loc = player.getEyeLocation();
        loc.setY(loc.getY() - 0.3);

        Vector vec = loc.getDirection();
        Map<Item, Vector> directions = new HashMap<>();

        List<Item> list = new ArrayList<>();

        for(int i = 0; i < shots; i ++) {

            Item item = world.dropItem(loc, new ItemStack(Material.DARK_OAK_BUTTON));

            item.setMetadata("ttt_entity_bullet", new FixedMetadataValue(Main.getInstance(), true));
            item.setPickupDelay(Integer.MAX_VALUE);
            item.setGravity(false);
            list.add(item);

            Vector direction = vec.clone().multiply(2);

            Vector v = Vector.getRandom();
            v.setX(v.getX() - 0.5f);
            v.setY(v.getY() - 0.5f);
            v.setZ(v.getZ() - 0.5f);
            v.multiply(0.5);

            direction.add(v);

            item.setVelocity(direction);
            directions.put(item, direction);
        }

        new BukkitRunnable() {

            Location entLoc;
            Vector diff;
            List<Item> toRemove = new ArrayList<>();
            Map<Item, Location> prevLoc = new HashMap<>();
            int count = 0;

            public void run() {

                if(count == 0) {
                    for (Item i : list)
                        prevLoc.put(i, i.getLocation());
                }

                for(Item ent : list) {
                    entLoc = ent.getLocation();

                    diff = entLoc.toVector().subtract(prevLoc.get(ent).toVector());
                    double angle = Math.abs((float)Math.toDegrees(diff.angle(directions.get(ent))) );

                    Location entPrevLoc = prevLoc.get(ent);

                    // hit detection
                    RayTraceResult rayTraceResult = world.rayTraceEntities(entPrevLoc, directions.get(ent), entPrevLoc.distance(entLoc));
                    if(rayTraceResult != null
                            && rayTraceResult.getHitEntity() != null
                            && rayTraceResult.getHitEntity() instanceof LivingEntity
                            && rayTraceResult.getHitEntity() != player) {
                        HandlePlayer.damageTarget((LivingEntity) rayTraceResult.getHitEntity(), player, damage, weaponName);
                        toRemove.add(ent);
                        continue;
                    }

                    // check for wall collision
                    if(angle > 1 && count > 0) {
                        toRemove.add(ent);
                        continue;
                    }
                    // check for range limit
                    if(entLoc.distance(loc) > range) {
                        toRemove.add(ent);
                        continue;
                    }
                    // check for ground contact
                    if(ent.isOnGround()) {
                        toRemove.add(ent);
                        continue;
                    }
                }

                for(Item ent : toRemove) {
                    list.remove(ent);
                    ent.remove();
                }
                if(list.isEmpty()) cancel();

                for(Item i : list)
                    prevLoc.put(i, i.getLocation());

                count ++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
    public void shotSound(Location loc) {
        // TODO this
        World world = loc.getWorld();
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 20f,0.6f);
        //world.playSound(loc, Sound.ITEM_TRIDENT_HIT_GROUND, 20f, 0.6f);
        //world.playSound(loc, Sound.ENTITY_POLAR_BEAR_WARNING, 20f, 1.6f);
    }

    public void reload(Player player, ItemStack item) {

        reloadStartSound(player);

        reloading.add(player);
        player.setCooldown(item.getType(), (int) (reloadTime * 20));

        new BukkitRunnable() {


            public void run() {

                reloadEndSound(player);

                fillMag(player);
                reloading.remove(player);

                // correct name of item
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(weaponName + ChatColor.WHITE + " -|- " + ChatColor.GRAY + magazineMap.get(player));
                item.setItemMeta(meta);

            }
        }.runTaskLater(Main.getInstance(), (long) reloadTime * 20);
    }
    public void reloadEndSound(Player player) {
        player.playSound(player.getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 2f, 1.8f);
    }
    public void reloadStartSound(Player player) {
        player.playSound(player.getEyeLocation(), Sound.BLOCK_LEVER_CLICK, 2f, 1.8f);
    }
    public void fillMag(Player player) {

        ItemStack item = ammo;
        item.setAmount(1);

        for(int i = magazineMap.get(player); i < magSize; i++) {
            if(!hasOneAmmo(player)) return;

            player.getInventory().removeItem(item);
            magazineMap.put(player, magazineMap.get(player) + 1);
        }
    }

}
