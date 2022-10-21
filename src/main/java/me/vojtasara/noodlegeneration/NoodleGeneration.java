package me.vojtasara.noodlegeneration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Random;

public final class NoodleGeneration extends JavaPlugin implements Listener {
    public PlayerScores playerScores;
    public MaterialManager materialManager;
    @Override
    public void onEnable() {
        // Load custom material list file
        materialManager = new MaterialManager();

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
            playerScores.savePlayerScore(p);
        }
        playerScores.saveToFile("playerScores.txt");
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        if (playerScores.newPlayer(event.getPlayer())){
            // Give the player the magic wand
            ItemStack theUnderblocker = new ItemStack(Material.STICK);
            ItemMeta meta = (ItemMeta) theUnderblocker.getItemMeta();
            meta.setDisplayName("The Underblocker");
            theUnderblocker.setItemMeta(meta);
            theUnderblocker.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            event.getPlayer().getInventory().addItem(theUnderblocker);
            event.getPlayer().getLocation().add(0, -2, 0).getBlock().setType(Material.GLASS);
        }
        initializeScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        playerScores.savePlayerScore(event.getPlayer());
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
            score.setScore(playerScores.getPlayerScore(player));
            player.setScoreboard(sc);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        ItemStack theUnderblocker = new ItemStack(Material.STICK);
        ItemMeta meta = (ItemMeta) theUnderblocker.getItemMeta();
        meta.setDisplayName("The Underblocker");
        theUnderblocker.setItemMeta(meta);
        theUnderblocker.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        e.getPlayer().getInventory().addItem(theUnderblocker);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        if (event.getPlayer().getLocation().add(0,-1,0).getBlock().getType() == Material.AIR && playerHoldingTheUnderblocker(event.getPlayer())) {
            Score score = event.getPlayer().getScoreboard().getObjective("pointBalance").getScore("Points: ");
            if (score.getScore() > 0) {
                score.setScore(score.getScore() - 1);
                event.getPlayer().getLocation().add(0, -1, 0).getBlock().setType(randomBlock());
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals("sell") && !playerHoldingTheUnderblocker(player)) {
            int itemsInHand = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(0);
            Score score = player.getScoreboard().getObjective("pointBalance").getScore("Points: ");
            score.setScore(score.getScore() + itemsInHand);
        }

        if (event.getMessage().equals("getstick")){
            ItemStack theUnderblocker = new ItemStack(Material.STICK);
            ItemMeta meta = (ItemMeta) theUnderblocker.getItemMeta();
            meta.setDisplayName("The Underblocker");
            theUnderblocker.setItemMeta(meta);
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), theUnderblocker);
        }
    }


    private boolean playerHoldingTheUnderblocker(Player player) {
        if (player.getInventory().getItemInMainHand().getItemMeta() != null){
            return player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("The Underblocker");
        }
        else {
            return false;
        }
    }

    private Material randomBlock() {
        Material[] choices = materialManager.getChoices();
        int choice = (int) (Math.random() * choices.length);
        return choices[choice];
    }
    private Material totallyRandomBlock() {
        return Material.values()[new Random().nextInt(Material.values().length)];
    }

}
