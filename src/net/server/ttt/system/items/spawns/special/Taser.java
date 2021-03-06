package net.server.ttt.system.items.spawns.special;

import net.server.ttt.main.Main;
import net.server.ttt.system.handling.HandlePlayer;
import net.server.ttt.system.items.abstracts.TTTItemWeapon;
import net.server.ttt.system.items.abstracts.TTTItemWeaponShootable;
import net.server.ttt.system.utils.enums.WeaponType;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Taser extends TTTItemWeaponShootable {

    static Map<Player, Long> lastFireMap = new HashMap<>();
    static Map<Player, Integer> magazineMap = new HashMap<>();
    static List<Player> reloading = new ArrayList<>();

    static double damage = 2;
    static double headMultiplier = 1.0;
    static double fireRate = 1; // shots per second
    static String weaponName = ChatColor.DARK_PURPLE + "Taser";
    static boolean isHoldable = false;

    static double reloadTime = 4;
    static int magSize = 1;
    static double range = 6;
    static double duration = 3.5;

    static WeaponType weaponType = WeaponType.SUPER;
    static int ammoBatchAmount = 2;
    static String weaponId = "ttt_item_weapon_taser";
    static String ammoId = "ttt_item_ammo_taser";

    // weapon
    static ItemStack weapon = new ItemStack(Material.LEATHER_HORSE_ARMOR, 1);
    static {
        ArrayList<String> lore = new ArrayList<>();
        LeatherArmorMeta meta = (LeatherArmorMeta) weapon.getItemMeta();

        meta.setDisplayName(weaponName);

        lore.add(Main.hideText(weaponId));
        lore.add(ChatColor.GRAY + "A short range stun gun.");
        lore.add(ChatColor.GRAY + " ");
        lore.add(ChatColor.GRAY + "damage: " + ChatColor.DARK_GREEN + damage);
        lore.add(ChatColor.GRAY + "stun duration: " + ChatColor.DARK_GREEN + duration + " sec");
        lore.add(ChatColor.GRAY + "pew-pew's/sec: " + ChatColor.DARK_GREEN + fireRate);
        lore.add(ChatColor.GRAY + "weapon type: " + ChatColor.DARK_GREEN + weaponType.toString());

        meta.setLore(lore);
        meta.setColor(Color.PURPLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        weapon.setItemMeta(meta);
    }

    // ammo
    static ItemStack ammo = new ItemStack(Material.PRISMARINE_SHARD, ammoBatchAmount);
    static {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta meta = ammo.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_GRAY + "Taser Ammo");
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

        double spacing = 0.2;

        World world = player.getWorld();
        Location loc = player.getEyeLocation();
        Vector vec = loc.getDirection().multiply(spacing);

        for(double d = 0; d < range; d += spacing) {

            loc.add(vec);

            // action start

            // effects
            shotParticles(loc);

            // damage
            for(LivingEntity e : world.getLivingEntities()) {
                if(e == player) continue;
                if (Main.isWithinEntityBoundingBox(loc, e, 1)) {
                    HandlePlayer.damageTarget(e, player, damage, weaponName);
                    HandlePlayer.shockTarget(e, player, duration, weaponName); // TODO test shockTarget()
                    return;
                }
            }
            //action end

            if (loc.getBlock().getType().isSolid())
                return;
        }
    }
    public void shotParticles(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(Particle.CRIT_MAGIC, loc, 1, 0, 0, 0, 0, null, true);
        world.spawnParticle(Particle.WATER_BUBBLE, loc, 1, 0, 0, 0, 0, null, true);
        world.spawnParticle(Particle.FLASH, loc, 1, 0, 0, 0, 0, null, true);
    }
    public void shotSound(Location loc) {
        World world = loc.getWorld();
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 20f,1.6f);
        world.playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 20f, 1.2f);
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
