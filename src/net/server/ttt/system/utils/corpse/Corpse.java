package net.server.ttt.system.utils.corpse;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import net.server.ttt.main.Main;
import net.server.ttt.system.utils.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Corpse {

    public Location loc;
    public Player victim;
    public Player killer;
    public String cause;
    public boolean isHeadShot;
    public boolean identified;
    public boolean scanned; // identified with more information

    private GameProfile gameProfile;
    private EntityPlayer entityPlayer;
    private String texture;
    private String signature;

    public Corpse(Location loc, Player victim, Player killer, String cause, boolean isHeadShot) {

        this.loc = loc;
        this.victim = victim;
        this.killer = killer;
        this.cause = cause;
        this. isHeadShot = isHeadShot;

        spawn();
    }

    public EntityPlayer getEntityPlayer() { return entityPlayer; }

    public void spawn() {
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) loc.getWorld()).getHandle();

        this.gameProfile = new GameProfile(UUID.randomUUID(), " ");
        this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        this.entityPlayer = new EntityPlayer(mcServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        this.entityPlayer.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        CorpseManager.addCorpseToList(loc.getWorld(), this);
    }

    public void show(Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));

        // TODO set skin

        entityPlayer.sleep(new BlockPosition(loc.getX(), loc.getY(), loc.getX()) );
        entityPlayer.setCustomNameVisible(false);
        entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.GRAY + "UNIDENTIFIED"));

        this.identified = false;

        playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), false));
        //playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (entityPlayer.yaw * 256 / 360)));
        removeFromTab();
    }
    public void hide(Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()) );
    }
    public void remove() {

        WorldServer worldServer = ((CraftWorld) loc.getWorld()).getHandle();
        worldServer.removeEntity(entityPlayer);

        for(Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
        }

        CorpseManager.corpseMap.remove(this);
    }

    public void identify() {

        // TODO set skin

        Role role = (Role) victim.getMetadata("ttt_role").get(0).value();

        if(role == Role.TRAITOR)
            entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.DARK_RED + victim.getName()));
        else if(role == Role.DETECTIVE)
            entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.DARK_BLUE + victim.getName()));
        else if(role == Role.INNOCENT)
            entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.GREEN + victim.getName()));


        for(Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), false));
        }

        identified = true;
    }

    public void scan() {

        // TODO set skin

        Role role = (Role) victim.getMetadata("ttt_role").get(0).value();

        if(role == Role.TRAITOR)
            entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.DARK_RED + victim.getName()));
        else if(role == Role.DETECTIVE)
            entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.DARK_BLUE + victim.getName()));
        else if(role == Role.INNOCENT)
            entityPlayer.setCustomName(IChatBaseComponent.ChatSerializer.a(ChatColor.GREEN + victim.getName()));


        for(Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), false));
        }

        scanned = true;
    }

    public void removeFromTab() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player op : Bukkit.getOnlinePlayers()) {
                    PlayerConnection con = ((CraftPlayer)op).getHandle().playerConnection;
                    con.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));

                }

            }

        }.runTaskLater(Main.getInstance(), 2);

    }
}
