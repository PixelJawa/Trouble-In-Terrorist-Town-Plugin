package net.server.ttt.system.handling;

import net.minecraft.server.v1_15_R1.*;
import net.server.ttt.main.Main;
import net.server.ttt.system.items.TTTItem;
import net.server.ttt.system.items.TTTItemList;
import net.server.ttt.system.utils.corpse.CorpseManager;
import net.server.ttt.system.utils.enums.*;
import net.server.ttt.system.utils.corpse.Corpse;
import net.server.ttt.system.utils.threads.GameThread;
import net.server.ttt.system.utils.threads.SuperItemBoxThread;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class HandleGame implements Listener {

    // this class handles everything that happens in game

    public static Map<World, GameThread> gameThreadMap = new HashMap<>();
    public static Map<ArmorStand, SuperItemBoxThread>  superItemThreadMap = new HashMap<>();
    public static Map<World, List<Player> > readyMap = new HashMap<>();

    // start the game in the given world
    public static void startGame(World world) {

        // check if world is ttt world
        if(!world.hasMetadata("ttt_world")) return;

        // set world state to closed
        world.setMetadata("ttt_state", new FixedMetadataValue(Main.getInstance(), GameState.CLOSED));

        // start GameThread and put it into the map
        GameThread thread = new GameThread(world);
        thread.runTaskTimer(Main.getInstance(), 0L, Main.getInstance().getConfig().getInt("Tread.TickRate"));
        //BukkitTask thread = new GameThread(world).runTaskTimer(Main.getInstance(), 0L, Main.getInstance().getConfig().getInt("Tread.TickRate"));
        gameThreadMap.put(world, thread);
    }
    // create a new game and transfer all players to that game
    public static void restartGame(World world) {

        for(Player p : world.getPlayers())
            p.sendMessage(ChatColor.DARK_AQUA + "A new game is being created please be patient.");

        // stop the game thread and remove the world from the map
        if(gameThreadMap.containsKey(world)) {
            GameThread thread = gameThreadMap.get(world);
            thread.cancel();
            gameThreadMap.remove(world);
        }

        // create new game
        World newWorld = HandleWorldCreation.createRand();

        // transfer all players to new game
        for(Player p : world.getPlayers())
            joinGame(p, newWorld);

        // remove any player that couldn't join (because of player cap for example)
        removePlayers(world);
        // remove corpses
        CorpseManager.removeAll(world);

        // delete old world
        HandleWorldCreation.delete(world);
    }
    // stops the GameThread of the given world if it is running, removes all players from that world and deletes it
    public static void stopGame(World world) {

        // check if world is ttt world
        if(!world.hasMetadata("ttt_world")) return;

        // remove players form the world
        removePlayers(world);
        // remove corpses
        CorpseManager.removeAll(world);

        // stop the game thread and remove the world from the map
        if(gameThreadMap.containsKey(world)) {
            GameThread thread = gameThreadMap.get(world);
            thread.cancel();
            gameThreadMap.remove(world);
        }

        // delete world
        HandleWorldCreation.delete(world);
    }

    // place the given player in the given world and start the game if max players is reached
    public static void joinGame(Player player, World world) {

        // abort if no world is given
        if(world == null) {
            player.sendMessage(ChatColor.RED + "Sorry something went wrong! Please contact a dev is you encounter this message.");
            return;
        }

        // abort if world is no ttt world
        if(!world.hasMetadata("ttt_world")) {
            player.sendMessage(ChatColor.RED + "The world you tried to join is not a TTT world.");
            return;
        }

        // abort if the world is closed
        if(world.getMetadata("ttt_state").get(0).value().equals(GameState.CLOSED) ) {
            player.sendMessage(ChatColor.RED + "Sorry but that world is closed.");
            return;
        }

        Location loc = world.getSpawnLocation();
        player.teleport(loc);
        player.setGameMode(GameMode.ADVENTURE);

        List<Player> players = world.getPlayers();

        // lock world and start game if max players is reached
        if(players.size() >= Main.getInstance().getConfig().getInt("Max.Players")) {
            world.setMetadata("ttt_state", new FixedMetadataValue(Main.getInstance(), "closed"));
            startGame(world);
        }
    }
    // removes all players from the given ttt-world
    public static void removePlayers(World world) {

        // init vars
        Location spawn = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
        List<Player> players = world.getPlayers();

        // teleport all players in the world to lobby spawn
        for(Player p : players) {
            p.teleport(spawn);
        }
    }
    // adds the player to the ready list and checks if the game can start
    public static void readyPlayer(Player player, World world) {

        // abort if world is no TTT world
        if(!player.getWorld().hasMetadata("ttt_world")) {
            player.sendMessage(ChatColor.RED + "You are not in a TTT world.");
            return;
        }

        // abort if game is in progress
        if(world.getMetadata("ttt_state").get(0).value().equals(GameState.CLOSED)) {
            player.sendMessage(ChatColor.RED + "DUDE! Focus on the game!");
            return;
        }

        // create now list if map doesn't know key
        if(!readyMap.containsKey(world)) {
            readyMap.put(world, new ArrayList<>());
        }

        List<Player> readies = readyMap.get(world);

        // remove player if in ready queue
        if(readies.contains(player)) {
            readies.remove(player);
            player.sendMessage(ChatColor.DARK_AQUA + "You are no longer ready!");
        }
        // add player if not in ready queue
        else {
            readies.add(player);
            player.sendMessage(ChatColor.AQUA + "You are now ready!");
        }

        // add list to map
        readyMap.put(world, readies);

        // check if game can be started
        int playerNr = world.getPlayers().size();
        if(playerNr >= Main.getInstance().getConfig().getInt("Min.Players") && readies.size() >= playerNr)
            startGame(world);
    }

    // distributes the roles the the players
    public static void distributeRoles(List<Player> players) {

        int maxPlayers = Main.getInstance().getConfig().getInt("Max.Players");
        double traitors = 0;
        double detectives = 0;
        double innocents = 0;
        double traitPercent = 0.0;
        double pAmount = players.size();

        int pIndex = 0;

        if (pAmount < maxPlayers) return;
        else {

            // randomize the amount of traitors a bit
            if(Main.randBoolean()) traitPercent = 0.3;
            else traitPercent = 0.4;

            // calculate how many of each class is in the game
            detectives = Math.round( pAmount * 0.25 );
            traitors = Math.round(pAmount * traitPercent);
            innocents = maxPlayers - detectives - traitors;

            // shuffle list
            Collections.shuffle(players);

            // apply roles as metadata: ttt_role
            //traitors
            for(int i = 0; i < traitors; i++) {
                Player p = players.get(pIndex);
                p.setMetadata("ttt_role", new FixedMetadataValue(Main.getInstance(), Role.TRAITOR));
                p.sendMessage(ChatColor.DARK_RED + "You are Traitor");
                p.playSound(p.getEyeLocation(), Sound.ENTITY_VINDICATOR_CELEBRATE, 2f ,1.3f);
                players.remove(p);
            }
            // detectives
            for(int i = 0; i < detectives; i++) {
                Player p = players.get(pIndex);
                p.setMetadata("ttt_role", new FixedMetadataValue(Main.getInstance(), Role.DETECTIVE));
                p.sendMessage(ChatColor.DARK_BLUE + "You are Detective");
                p.playSound(p.getEyeLocation(), Sound.ENTITY_WANDERING_TRADER_DISAPPEARED, 2f ,0.6f);
                players.remove(p);
            }
            // innocents
            for(int i = 0; i < innocents; i++) {
                Player p = players.get(pIndex);
                p.setMetadata("ttt_role", new FixedMetadataValue(Main.getInstance(), Role.INNOCENT));
                p.sendMessage(ChatColor.GREEN + "You are Innocent");
                p.playSound(p.getEyeLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 2f ,1.4f);
                players.remove(p);
            }

        }
    }
    // declare the win for the given role
    public static void declareWin(World world, Role role) {

        if(role.equals(Role.TRAITOR)) {
            for (Player p : world.getPlayers()) {
                p.sendTitle(ChatColor.DARK_RED + "Traitors Win!", "", 1, 6, 1);
                p.playSound(p.getEyeLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.4f ,1.2f);
                p.playSound(p.getEyeLocation(), Sound.ENTITY_WITCH_CELEBRATE, 2 ,0.8f);
            }
        }
        else if(role.equals(Role.INNOCENT)) {
            for (Player p : world.getPlayers()) {
                p.sendTitle(ChatColor.GREEN + "Innocents Win!", "",1,6, 1 );
                p.playSound(p.getEyeLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f ,1.8f);
                p.playSound(p.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 20f,0.3f);
            }
        }
    }

    // spread the given players all over the map
    public static void spreadPlayers(World world, List<Player> players) {

        // init file vars
        String path = world.getWorldFolder().getAbsolutePath() + File.separator + "TTT_data" + File.separator + "spawns.yml";
        File file = new File(path);

        if(!file.exists()) {
            System.out.println(ChatColor.RED + "[TTT] -- HandleGame -- Players could not be spread! File missing! ");
            return;
        }

        YamlConfiguration spawnsYML = YamlConfiguration.loadConfiguration(file);

        // init list var
        List<Location> list = (List<Location>) spawnsYML.getList("playerSpawnList");

        // tp players
        for(Player p : players) {
            int index = Main.randomInt(0, list.size() -1 );

            p.teleport(list.get(index));
            list.remove(index);

            // refill list when there are not enough spawn points
            if(list.isEmpty())
                list = (List<Location>) spawnsYML.getList("playerSpawnList");
        }

    }
    // spawn the game items
    public static void spawnItems(World world) {
        // init file vars
        String path = world.getWorldFolder().getAbsolutePath() + File.separator + "TTT_data" + File.separator + "spawns.yml";
        File file = new File(path);

        if(!file.exists()) {
            System.out.println(ChatColor.RED + "[TTT] -- HandleGame -- Players could not be spread! File missing! ");
            return;
        }

        YamlConfiguration spawnsYML = YamlConfiguration.loadConfiguration(file);

        // init list var
        List<Location> itemList = (List<Location>) spawnsYML.getList("itemSpawnList");
        List<String> itemKeys = (List<String>) TTTItemList.genericSpawnMap.keySet();
        List<Location> superList = (List<Location>) spawnsYML.getList("superSpawnList");
        List<String> superKeys = (List<String>) TTTItemList.superSpawnMap.keySet();

        // generic items
        for(Location loc : itemList) {

            loc.setX(loc.getX() + 0.5);
            loc.setY(loc.getY() + 0.2);
            loc.setZ(loc.getZ() + 0.5);

            // get a random item
            int index = Main.randomInt(0, itemKeys.size() - 1);
            TTTItem tttItem = TTTItemList.genericSpawnMap.get(itemKeys.get(index));
            ItemStack weapon = tttItem.getItem();
            ItemStack ammo = tttItem.getAmmo();

            // drop weapon
            Item weaponEnt = loc.getWorld().dropItemNaturally(loc, weapon);
            weaponEnt.setMetadata("ttt_entity_item", new FixedMetadataValue(Main.getInstance(), true));
            weaponEnt.setMetadata("ttt_entity_weapon_type", new FixedMetadataValue(Main.getInstance(), tttItem.getWeaponType()));
            weaponEnt.setCustomName(tttItem.getName());
            weaponEnt.setCustomNameVisible(false);
            weaponEnt.setPickupDelay(1);

            // drop between 3 and 5 ammo stacks
            for(int i = 0; i < Main.randomInt(3, 5); i++) {
                // drop weapon
                Item ammoEnt = loc.getWorld().dropItemNaturally(loc, ammo);
                ammoEnt.setMetadata("ttt_entity_item", new FixedMetadataValue(Main.getInstance(), true));
                ammoEnt.setPickupDelay(0);
            }
        }

        // super items
        int loop = 0;
        for(Location loc : superList) {

            // make this only loop 2 times
            loop++;
            if(loop > 2) break;

            loc.setX(loc.getX() + 0.5);
            loc.setZ(loc.getZ() + 0.5);

            // get a random item
            int index = Main.randomInt(0, superKeys.size() - 1);
            TTTItem tttItem = TTTItemList.superSpawnMap.get(superKeys.get(index));
            ItemStack item = tttItem.getItem();

            ArmorStand stand = world.spawn(loc, ArmorStand.class);

            SuperItemBoxThread thread = new SuperItemBoxThread();
            thread.runTaskTimer(Main.getInstance(), 0, 1);

            superItemThreadMap.put(stand, thread);

            stand.setMetadata("ttt_entity_superItemBox", new FixedMetadataValue(Main.getInstance(), item));
            stand.setVisible(false);
            stand.setCustomName(ChatColor.LIGHT_PURPLE + "?");
            stand.setCustomNameVisible(false);
            stand.setGravity(false);
            stand.setSmall(true);
            stand.getEquipment().setHelmet(new ItemStack(Material.CHORUS_FLOWER));
        }

    }

    // spawn a dead at the given location
    public static void spawnCorpse(Location loc, Player victim, Player killer, String cause, boolean isHeadShot) {

        World world = loc.getWorld();

        if(!gameThreadMap.containsKey(world)) return;
        GameThread thread = gameThreadMap.get(world);

        Corpse corpse = new Corpse(loc, victim, killer, cause, isHeadShot);

        for(Player p : thread.players)
            corpse.show(p);
    }

    // update the tab list for the good team
    public static void updateGoodTeamTab(World world) {

        if(!world.hasMetadata("ttt_world")) return;
        if(!gameThreadMap.containsKey(world)) return;

        GameThread thread = gameThreadMap.get(world);

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        // init receiver list
        List<Player> receivers = thread.players;
        for(Player p : receivers) {
            if(!p.hasMetadata("ttt_role")) receivers.remove(p);

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            assert role != null;
            if(!role.equals(Role.TRAITOR)) receivers.remove(p);
        }

        // set Header and footer
        try {
            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);

            Object header = new ChatComponentText(ChatColor.GOLD + "Trouble In Terrorist Town");
            a.set(packet, header);
            Object footer = new ChatComponentText(ChatColor.AQUA + "Objective: Find and kill all traitors.");
            b.set(packet, footer);

            for (Player player : receivers) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // clear tab list
        for(Player p : thread.aliveGood) {
            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,  ((CraftPlayer) p).getHandle() );

            for(Player p2 : receivers) {
                ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(infoPacket);
            }
        }

        // fill tab list
        for(Player p : thread.alive) {

            // get role of player
            if(!p.hasMetadata("ttt_role")) continue;
            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            // add colors
            if (role == Role.DETECTIVE) {
                p.setPlayerListName(ChatColor.DARK_BLUE + p.getName());
            } else {
                p.setPlayerListName(ChatColor.GREEN + p.getName());
            }

            // create packet
            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle() );

            // send packet
            for(Player p2 : receivers) {
                ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(infoPacket);
            }
        }

    }
    // update the tab list for the bad team
    public static void updateBadTeamTab(World world) {
        if(!world.hasMetadata("ttt_world")) return;
        if(!gameThreadMap.containsKey(world)) return;

        GameThread thread = gameThreadMap.get(world);

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        // init receiver list
        List<Player> receivers = thread.players;
        for(Player p : receivers) {
            if(!p.hasMetadata("ttt_role")) receivers.remove(p);

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            assert role != null;
            if(!role.equals(Role.TRAITOR)) receivers.remove(p);
        }

        // set Header and footer
        try {
            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);

            Object header = new ChatComponentText(ChatColor.GOLD + "Trouble In Terrorist Town");
            a.set(packet, header);
            Object footer = new ChatComponentText(ChatColor.AQUA + "Objective: Kill the innocents and detectives, before they kill you.");
            b.set(packet, footer);

            for (Player player : receivers) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // clear tab list
        for(Player p : world.getPlayers()) {
            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,  ((CraftPlayer) p).getHandle() );

            for(Player p2 : receivers) {
                ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(infoPacket);
            }
        }

        // fill tab list
        for(Player p : thread.alive) {

            // get role of player
            if(!p.hasMetadata("ttt_role")) continue;
            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            // add colors
            switch (Objects.requireNonNull(role)) {
                case DETECTIVE: {
                    p.setPlayerListName(ChatColor.DARK_BLUE + p.getName());
                    break;
                }
                case INNOCENT: {
                    p.setPlayerListName(ChatColor.GREEN + p.getName());
                    break;
                }
                case TRAITOR: {
                    p.setPlayerListName(ChatColor.DARK_RED + p.getName());
                    break;
                }
            }

            // create packet
            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle() );

            // send packet
            for(Player p2 : receivers) {
                ((CraftPlayer) p2).getHandle().playerConnection.sendPacket(infoPacket);
            }
        }
    }
    // update the tab list for all dead players
//    public static void updateDeadTab(World world) {
//        if(!world.hasMetadata("ttt_world")) return;
//        if(!threadMap.containsKey(world)) return;
//
//        GameThread thread = threadMap.get(world);
//
//        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
//
//        // set Header and footer
//        try {
//            Field a = packet.getClass().getDeclaredField("a");
//            a.setAccessible(true);
//            Field b = packet.getClass().getDeclaredField("b");
//            b.setAccessible(true);
//
//            Object header = new ChatComponentText(ChatColor.GOLD + "Trouble In Terrorist Town");
//            a.set(packet, header);
//            Object footer = new ChatComponentText(ChatColor.AQUA + "Objective: Kill the innocents and detectives, before they kill you.");
//            b.set(packet, footer);
//
//            for (Player player : thread.aliveGood) {
//                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//            }
//
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        // clear tab list
//        for(Player p : thread.aliveBad) {
//            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,  ((CraftPlayer) p).getHandle() );
//
//            for(Player p2 : thread.aliveGood) {
//                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(infoPacket);
//            }
//        }
//
//        // fill tab list
//        for(Player p : thread.alive) {
//
//            // get role of player
//            if(!p.hasMetadata("ttt_role")) continue;
//            Roles role = (Roles) p.getMetadata("ttt_role").get(0).value();
//
//            // add colors
//            switch (Objects.requireNonNull(role)) {
//                case DETECTIVE: {
//                    p.setPlayerListName(ChatColor.DARK_BLUE + p.getName());
//                    break;
//                }
//                case INNOCENT: {
//                    p.setPlayerListName(ChatColor.GREEN + p.getName());
//                    break;
//                }
//                case TRAITOR: {
//                    p.setPlayerListName(ChatColor.DARK_RED + p.getName());
//                    break;
//                }
//            }
//
//            // create packet
//            PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle() );
//
//            // send packet
//            for(Player p2 : thread.aliveGood) {
//                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(infoPacket);
//            }
//        }
//    }

// EventHandler
    // stop dead players from chatting
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!gameThreadMap.containsKey(world)) return;

        GameThread thread = gameThreadMap.get(world);

        if(thread.dead.contains(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can chat right now, since I am to lazy to implement a death Chat. Next time just don't die. ;-)");
        }
    }

    // stop crops from being destroyed by players
    @EventHandler
    public void onCropDestroy(BlockBreakEvent event) {

        World world = event.getBlock().getWorld();
        Material mat = event.getBlock().getType();

        if(!world.hasMetadata("ttt_world")) return;

        switch (mat) {

            case PUMPKIN_STEM:
            case MELON_STEM:
            case POTATOES:
            case FARMLAND:
            case CARROTS:
            case BEETROOTS:
            case WHEAT:
                event.setCancelled(true);
                break;
        }
    }

    // handle a player leaving the world or game
    @EventHandler
    public void onPlayerLeaveWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!world.getMetadata("ttt_state").get(0).value().equals(GameState.CLOSED)) return;
        if(!gameThreadMap.containsKey(world)) return;

        GameThread thread = gameThreadMap.get(world);

        HandlePlayer.resetPlayer(player, thread);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!world.getMetadata("ttt_state").get(0).value().equals(GameState.CLOSED)) return;
        if(!gameThreadMap.containsKey(world)) return;

        GameThread thread = gameThreadMap.get(world);
        HandlePlayer.resetPlayer(player, thread);
    }

    // stop items from merging
    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        Item item = event.getEntity();
        World world = item.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!item.hasMetadata("ttt_entity_item")) return;

        event.setCancelled(true);
    }

    // stop items from despawning
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        Item item = event.getEntity();
        World world = item.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!item.hasMetadata("ttt_entity_item")) return;

        event.setCancelled(true);
    }

    // stop player from picking up two weapons of the same class
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        // check if entity is a player
        if(event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();

        // init item var
        Item item = event.getItem();
        if(!item.hasMetadata("ttt_entity_item")) return;
        if(!item.hasMetadata("ttt_entity_weapon_type")) return;

        // init other vars
        WeaponType wType = (WeaponType) item.getMetadata("ttt_weapon").get(0).value();
        ItemStack[] inv = player.getInventory().getContents();
        ItemMeta meta;
        String id;

        if(wType == WeaponType.MELEE)
            for(ItemStack i : inv) {
                // check for meta and lore
                if(!i.hasItemMeta()) continue;
                meta = i.getItemMeta();
                assert meta != null;

                if(!meta.hasLore()) continue;

                id = Main.revealText(meta.getLore().get(0));

                if(id.contains("ttt_item_weapon_melee_")) event.setCancelled(true);
            }
        else if(wType == WeaponType.PRIMARY)
            for(ItemStack i : inv) {
                // check for meta and lore
                if(!i.hasItemMeta()) continue;
                meta = i.getItemMeta();
                assert meta != null;

                if(!meta.hasLore()) continue;

                id = Main.revealText(meta.getLore().get(0));

                if(id.contains("ttt_item_weapon_primary_")) event.setCancelled(true);
            }
        else if(wType == WeaponType.SECONDARY)
            for(ItemStack i : inv) {
                // check for meta and lore
                if(!i.hasItemMeta()) continue;
                meta = i.getItemMeta();
                assert meta != null;

                if(!meta.hasLore()) continue;

                id = Main.revealText(meta.getLore().get(0));

                if(id.contains("ttt_item_weapon_secondary_")) event.setCancelled(true);
            }
    }

    // handle players clicking on a super item box
    @EventHandler
    public void onInteractWithAS(PlayerInteractAtEntityEvent event) {

        if(!event.getPlayer().hasMetadata("ttt_role")) return;
        if(!(event.getRightClicked() instanceof ArmorStand)) return;

        // init vars
        Player player = event.getPlayer();
        ArmorStand armorStand = (ArmorStand) event.getRightClicked();

        if(!armorStand.hasMetadata("ttt_entity_superItemBox")) return;
        if(superItemThreadMap.containsKey(armorStand)) return;

        // init item var
        ItemStack item = (ItemStack) armorStand.getMetadata("ttt_entity_superItemBox").get(0).value();

        // stop superItemThread and remove the armor stand
        superItemThreadMap.get(armorStand).cancel();
        superItemThreadMap.remove(armorStand);
        armorStand.remove();

        player.getInventory().addItem(item);
    }

}
