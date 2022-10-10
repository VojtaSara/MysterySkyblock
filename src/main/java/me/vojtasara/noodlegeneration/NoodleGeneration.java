package me.vojtasara.noodlegeneration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public final class NoodleGeneration extends JavaPlugin implements Listener {
    public PlayerScores playerScores;
    @Override
    public void onEnable() {
        // Load the playerscores from the default txt file.
        playerScores = new PlayerScores();
        playerScores.loadFromFile("playerScores.txt");
        System.out.println(playerScores.scoresTable.get("monibred"));

        // Plugin startup logic
        System.out.println("PLUGIN STARTED SUCCESFULLY");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        for (Player p :
             players) {
            System.out.println( p.getName());
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        initializeScoreboard(event.getPlayer());

        // Give the player the magic wand
        ItemStack theUnderblocker = new ItemStack(Material.STICK);
        ItemMeta meta = (ItemMeta) theUnderblocker.getItemMeta();
        meta.setDisplayName("The Underblocker");
        theUnderblocker.setItemMeta(meta);
        event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), theUnderblocker);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        Score score = p.getScoreboard().getObjective("pointBalance").getScore("Points: ");
        System.out.println(score.getScore());
    }

    /**
     * Initializes the player scoreboard - a permanent GUI element visible on screen displaying how many points
     * does the player have
     * @param player
     */
    private void initializeScoreboard(Player player) {
        if (player.getScoreboard().getObjective("pointBalance") == null) {
            Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = sc.registerNewObjective("pointBalance","s","Point balance");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            Score score = objective.getScore("Points: ");
            score.setScore(50);
            player.setScoreboard(sc);
        }
    }


    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        if (event.getPlayer().getLocation().add(0,-1,0).getBlock().getType() == Material.AIR && playerHoldingTheUnderblocker(event.getPlayer())) {
            event.getPlayer().getLocation().add(0, -1, 0).getBlock().setType(randomBlock());
            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount()-1);
        }
    }

    private boolean playerHoldingTheUnderblocker(Player player) {
        ItemStack theUnderblocker = new ItemStack(Material.STICK);
        if (player.getInventory().getItemInMainHand().getItemMeta() != null){
            // TODO: subtract from the total Point count
            // Each player has his own score that he can increase by selling items, this score shall be displayed in the
            // players corresponding scoreboard
            Score score = player.getScoreboard().getObjective("pointBalance").getScore("Points: ");
            score.setScore(score.getScore() - 1);

            return player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("The Underblocker");
        }
        else {
            return false;
        }
    }

    private Material randomBlock() {
        Material[] choices = {
                Material.DIAMOND_ORE,
                Material.ACACIA_LOG,
                Material.DIRT,

        };
        int choice = (int) (Math.random() * choices.length);
        return choices[choice];
    }
}
