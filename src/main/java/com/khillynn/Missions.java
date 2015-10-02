package com.khillynn;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Missions extends JavaPlugin implements Listener {
    int maxSignLines = 4;
    /*
    ArrayList<Creeper> georgeFamily = new ArrayList<>();
    ArrayList<String> georgeOwners = new ArrayList<String>();
    */

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Missions is Enabled! =D");

        this.saveDefaultConfig();
    }

    //Mission One - send message to player from config.yml
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.sendMessage(this.getConfig().getString("motd"));
        //spawnGeorge(player, player.getLocation());
    }

    /*
    @EventHandler
    public void onPlayerLeaves(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        removePlayerAndGeorge(player);
    }
    */

    //Mission Two - make numbers on sign increase by 1 every time a player clicks it
    @EventHandler
    public void onPlayerClickSign(final PlayerInteractEvent e) {
        Player player = e.getPlayer();

        //below checks if the clicked block is a sign
        if ((e.getClickedBlock().getType() == Material.SIGN
                || e.getClickedBlock().getType() == Material.SIGN_POST
                || e.getClickedBlock().getType() == Material.WALL_SIGN)) {

                final Sign sign = (Sign) e.getClickedBlock().getState();

                if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                //below cycles through the lines of the sign and increases ints by 1
                    for (int lineNum = 0; lineNum < maxSignLines; lineNum++) {
                        String signText = sign.getLine(lineNum);

                        if (!signText.matches("^\\-?[0-9]+$"))
                            continue;

                        int num = Integer.parseInt(signText);
                        int newNum = num + 1;

                        sign.setLine(lineNum, String.valueOf(newNum));
                        sign.update();
                    }
                }

            //Mission Three - make signs scroll text horizontally
            if((e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

                scheduler.scheduleSyncRepeatingTask(this, new Runnable() {

                    public void run() {
                        for (int lineNumber = 0; lineNumber < maxSignLines; lineNumber++) {
                            String message = sign.getLine(lineNumber);

                            if(sign.getLine(lineNumber).isEmpty())
                                continue;

                            int lengthOfSign = 16;

                            //below adds spaces to lines that are smaller than the width of the sign
                            if (message.length() < lengthOfSign) {
                                StringBuilder sb = new StringBuilder(message);
                                while (sb.length() < lengthOfSign)
                                    sb.append(" ");
                                message = sb.toString();
                            }

                            String movingMessage = message.substring(1, message.length() - 1);
                            movingMessage = movingMessage + message.charAt(0);

                            sign.setLine(lineNumber, movingMessage);
                            sign.update();
                        }
                    }
                }, 0L, 3L);
            }
        }
    }
/*
    //adds increase damage potion effect to players within range of George The Charged Creeper
    @EventHandler
    public void playerMoves(PlayerMoveEvent e)
    {
        Player player = e.getPlayer();
        List<Entity> entities = player.getNearbyEntities(5.0D, 5.0D, 5.0D);

        for (Entity entiy : entities)
            if ((entiy.getType() == EntityType.CREEPER) && (Objects.equals(entiy.getCustomName(), "George") && !player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)))
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1));
            else if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    //when a player right clicks george, it will ride them or eject off of them
    @EventHandler
    public void playerRClicksGeorge(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();

        if(e.getRightClicked().getType() == EntityType.CREEPER) {
            Creeper george = (Creeper) e.getRightClicked();

            if(Objects.equals(george.getCustomName(), "George")) {
                if(player.getPassenger() == null)
                    player.setPassenger(george);
                else
                    player.eject();
            }
        }
    }

    //spawns George The Charged Creeper where the player joins in
    private void spawnGeorge(Player player, Location loc) {
        String playerName = player.getName();

        Creeper george = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
        georgeFamily.add(george);
        georgeOwners.add(player.getName());
        george.setPowered(true);
        george.setCustomName("George");
        george.setCustomNameVisible(true);

        //call ironsideGeorge to make George follow their owner
        //ironsideGeorge(player, george, 1.75);
    }


    //stops George The Charged Creeper from attacking players
    @EventHandler
    public void georgeIsAngry(EntityTargetLivingEntityEvent e){
        Creeper george = (Creeper) e.getEntity();

        if(Objects.equals(george.getCustomName(), "George") && e.getTarget().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getTarget();
            e.setCancelled(true);
        }
    }
    
    /*
    //makes George The Charged Creeper follow their owner
    private void ironsideGeorge(final Player player, final LivingEntity george, double speed) {
        final float georgeSpeed = (float) speed;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                ((EntityInsentient) ((CraftEntity) george).getHandle()).getNavigation().a(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), georgeSpeed);
            }
        }, 0L, 40L);
    }

    //removes George The Charged Creeper and their owner from their array lists and kills george
    private void removePlayerAndGeorge(Player player) {
        for (int ownerNum = 0; ownerNum < georgeOwners.size(); ownerNum++) {
            if(Objects.equals(georgeOwners.get(ownerNum), player.getName())){
                georgeFamily.get(ownerNum).remove();
                georgeFamily.remove(ownerNum);
                georgeOwners.remove(ownerNum);
            }
        }
    }
    */
}