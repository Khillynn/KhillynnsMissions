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
    }

    //Mission Two - make numbers on sign increase by 1 every time a player clicks it
    @EventHandler
    public void onPlayerClickSign(final PlayerInteractEvent e) {
        Player player = e.getPlayer();

        //below checks if the clicked block is a sign
        if ((e.getAction().equals(Action.LEFT_CLICK_BLOCK))
                && (e.getClickedBlock().getType() == Material.SIGN
                || e.getClickedBlock().getType() == Material.SIGN_POST
                || e.getClickedBlock().getType() == Material.WALL_SIGN)) {

            Sign sign = (Sign) e.getClickedBlock().getState();

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
        if((e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                && (e.getClickedBlock().getType() == Material.SIGN
                || e.getClickedBlock().getType() == Material.SIGN_POST
                || e.getClickedBlock().getType() == Material.WALL_SIGN)){
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {

                int position = 0;
                Sign sign = (Sign) e.getClickedBlock().getState();

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


                        String origMess = message.substring(1, message.length() - 1);
                        origMess = origMess + message.charAt(0);

                        sign.setLine(lineNumber, origMess);
                        sign.update();
                    }
                }
            }, 0L, 3L);
        }

    }
}