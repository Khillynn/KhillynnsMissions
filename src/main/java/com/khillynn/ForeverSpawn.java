/*
* Mission 5
* Have players spawn in a random safe area within a radius of a point
* save that location so that even when the server reboots the player will always spawn there
*/
package com.khillynn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ForeverSpawn extends JavaPlugin implements Listener{

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ForeverSpawn is Enabled! =D");

        this.saveDefaultConfig();
    }

    @EventHandler
    public void playerJoin (PlayerJoinEvent e){
        Player player = e.getPlayer();
        World world = player.getWorld();

        File playerFile = new File(getDataFolder(), File.separator + "PlayerDatabase");
        File file = new File(playerFile, File.separator + player.getUniqueId().toString() + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                Location telLoc = createNewSpawn(player, world);
                playerData.createSection("location");
                playerData.set("location.x", telLoc.getX());
                playerData.set("location.y", telLoc.getY());
                playerData.set("location.z", telLoc.getZ());
                playerData.save(file);
            }catch (IOException exception) {

                exception.printStackTrace();
            }
        }
        sendPlayerToSpawn(player);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e){
        sendPlayerToSpawn(e.getEntity());
    }

    private void sendPlayerToSpawn(Player p){
        //find the player's spawn location data from yml file
        File file = new File (getDataFolder() + File.separator + "PlayerDatabase", p.getUniqueId().toString() + ".yml");

        FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);

        World world = p.getWorld();
        double x = (double) playerData.get("location.x");
        double y = (double) playerData.get("location.y");
        double z = (double) playerData.get("location.z");

        Location telLoc = new Location(world, x, y, z);

        //if the chunk isn't loaded then load it
        if(!world.getChunkAt((int) telLoc.getX(),(int) telLoc.getZ()).isLoaded())
            world.loadChunk((int) telLoc.getX(),(int) telLoc.getZ());
        
        p.teleport(telLoc);
    }

    //this method should only be used if the player hasn't logged in before
    private Location createNewSpawn(Player p, World world) {
        boolean safePlace = false;
        Location telLoc = new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ());
        Location spawnBlock = telLoc;

        /*
        * get a random location
        * check if its on a safe block
        * if it is breakout of the loop
        */
        while(!safePlace) {
            telLoc = createLocation(p, world);
            spawnBlock = new Location(world, telLoc.getX(), telLoc.getY() - 1, telLoc.getZ());
            safePlace = isSafe(spawnBlock);
        }

        //if the chunk isn't loaded then load it
        if(!world.getChunkAt((int) telLoc.getX(),(int) telLoc.getZ()).isLoaded())
            world.loadChunk((int) telLoc.getX(),(int) telLoc.getZ());
        //below teleports the player to the new location and makes the block they teleport to made of wool
        spawnBlock.getBlock().setType(Material.WOOL);

        return telLoc;
    }

    //this method gets a random location within a set range of the world spawn
    private Location createLocation(Player p, World world) {
        Random random = new Random();
        Location worldSpawn = world.getSpawnLocation();
        int RADIUS = Integer.parseInt(this.getConfig().getString("radius"));

        int x = random.nextInt(RADIUS * 2);
        x -= (RADIUS + (int) worldSpawn.getX());
        int z = random.nextInt(RADIUS * 2);
        z -= (RADIUS + (int) worldSpawn.getZ());
        int y = p.getWorld().getHighestBlockYAt(x,z);

        return new Location(world, x, y, z);
    }

    //this method checks if the location from createLocation is safe for the player
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
