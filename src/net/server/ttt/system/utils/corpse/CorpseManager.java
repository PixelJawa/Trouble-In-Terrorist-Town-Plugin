package net.server.ttt.system.utils.corpse;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CorpseManager {

    public static Map<World, ArrayList<Corpse>> corpseMap = new HashMap<>();

    public static Corpse getCorpseFromId(int id) {

        Corpse corpse = null;

        for(World w : corpseMap.keySet())
            for(Corpse c : corpseMap.get(w) ) {
                if(c.getEntityPlayer().getId() == id)
                 corpse = c;
            }

        return corpse;
    }
    public static void addCorpseToList(World world, Corpse corpse) {

        ArrayList<Corpse> list = new ArrayList<>();

        if (corpseMap.containsKey(world))
            list = corpseMap.get(world);

        list.add(corpse);
        corpseMap.put(world, list);
    }
    public static void removeAll(World world) {

        for(Corpse c : corpseMap.get(world))
            c.remove();
    }
}
