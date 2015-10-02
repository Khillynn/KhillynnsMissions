package com.khillynn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ForeverSpawn extends JavaPlugin implements Listener{
    final int RADIUS = 500;

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ForeverSpawn is Enabled! =D");

        this.saveDefaultConfig();
    }



    @EventHandler
    public void playerJoin (PlayerJoinEvent e){
        Player player = e.getPlayer();
        World world = player.getWorld();

        //check if this player has a location saved already and then call teleportNewPlayer
        teleportNewPlayer(player, world);
    }

    private void teleportNewPlayer(Player p, World world) {
        boolean safePlace = false;
        Location telLoc = new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ());
        Location spawnBlock = telLoc;

        while(!safePlace) {
            telLoc = createLocation(p, world);
            spawnBlock = new Location(world, telLoc.getX(), telLoc.getY() - 1, telLoc.getZ());
            safePlace = isSafe(spawnBlock);
        }


        if(!world.getChunkAt((int) telLoc.getX(),(int) telLoc.getZ()).isLoaded())
            world.loadChunk((int) telLoc.getX(),(int) telLoc.getZ());
        spawnBlock.getBlock().setType(Material.WOOL);
        p.teleport(telLoc);
    }

    private Location createLocation(Player p, World world) {
        Random random = new Random();
        Location worldSpawn = world.getSpawnLocation();
        int x = random.nextInt(RADIUS * 2);
        x -= (RADIUS + (int) worldSpawn.getX());
        int z = random.nextInt(RADIUS * 2);
        z -= (RADIUS + (int) worldSpawn.getZ());

        int y = p.getWorld().getHighestBlockYAt(x,z);

        return new Location(world, x, y, z);
    }

    private boolean isSafe(Location spawnBlock) {
        Material block = spawnBlock.getBlock().getType();

        return !(block == Material.LAVA
                || block == Material.WATER
                || block == Material.STATIONARY_WATER
                || block == Material.FIRE
                || block == Material.WEB
                || block == Material.CACTUS);
    }
}
