package net.server.ttt.system.utils.threads;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

public class SuperItemBoxThread extends BukkitRunnable {

    float rot = 3;
    ArmorStand stand;

    @Override
    public void run() {

        Location loc = stand.getLocation();
        loc.setYaw(loc.getYaw() + rot);

        World world = loc.getWorld();

        Location loc2 = loc.clone();
        loc2.setY(loc2.getY() + 0.5);

        world.spawnParticle(Particle.NAUTILUS, loc2,3,0,0,0,0.5, null, true);

        if(isCancelled()) {
            world.spawnParticle(Particle.END_ROD, loc2,50,0,0,0,0.1, null, true);
            world.playSound(loc2, Sound.ENTITY_ENDER_EYE_DEATH, 2, 0.2f);
        }

    }
}
