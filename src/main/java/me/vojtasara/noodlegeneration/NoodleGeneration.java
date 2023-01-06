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

public class Globals {
    public static String playerScoresFileName = "playerScores.txt";
}

public final class NoodleGeneration extends JavaPlugin implements Listener {
    public PlayerScores playerScores;
    public MaterialManager materialManager;
    @Override
    public void onEnable() {
        // Load custom material list file
        materialManager = new MaterialManager();

        // Load the playerscores from the default txt file.
        playerScores = new PlayerScores();
        playerScores.loadFromFile(Globals.playerScoresFileName);
        System.out.println(playerScores.scoresTable.get("monibred"));

        System.out.println("PLUGIN STARTED SUCCESFULLY");

        getServer().getPluginManager().registerEvents(this, this);
    }

    // When the server is abruptly closed, save all players' scores
    @Override
    public void onDisable() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        for (Player p :
             players) {
            playerScores.savePlayerScore(p);
        }
        playerScores.saveToFile(Globals.playerScoresFileName);
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
    // When the player respawns, give him back the Underblocker he lost upon death
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        ItemStack theUnderblocker = new ItemStack(Material.STICK);
        ItemMeta meta = (ItemMeta) theUnderblocker.getItemMeta();
        meta.setDisplayName("The Underblocker");
        theUnderblocker.setItemMeta(meta);
        theUnderblocker.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        e.getPlayer().getInventory().addItem(theUnderblocker);
    }

    // When player moves, check whether there is space below him and if he is wielding the Underblocker
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

    // When the player sends the command "sell" in chat, items in his hand get transformed into money
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals("sell") && !playerHoldingTheUnderblocker(player)) {
            int itemsInHand = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(0);
            Score score = player.getScoreboard().getObjective("pointBalance").getScore("Points: ");
            score.setScore(score.getScore() + itemsInHand);
        }
        else if (event.getMessage().equals("list materials")){
            materialManager.listMaterials(event);
        }
        else if (event.getMessage().equals("list modes")){
            materialManager.listModes(event);
        }
        else if (event.getMessage().split(" ")[0].equals("select")){
            materialManager.selectMode(event);
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

    // Calls the material manager to choose a random block to place beneath player
    private Material randomBlock() {
        Material[] choices = materialManager.getChoices();
        int choice = (int) (Math.random() * choices.length);
        return choices[choice];
    }
}
