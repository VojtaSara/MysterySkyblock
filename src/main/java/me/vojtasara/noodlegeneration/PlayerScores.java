package me.vojtasara.noodlegeneration;

import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class PlayerScores {
    PlayerScores (){
        System.out.println("Scores init");
    }

    private final int startingAmount = 100;
    public Hashtable<String,Integer> scoresTable = new Hashtable<String,Integer>();

    // Loads player scores table from file - to make this data persistent across server reloads
    public void loadFromFile(String fileName){
        File f = new File(fileName);
        if (!f.exists() || f.isDirectory()) {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
                writer.write("");
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                scoresTable.put(line.split(" ")[0],Integer.parseInt(line.split(" ")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerScore(Player p){
        int playerScore = p.getScoreboard().getObjective("pointBalance").getScore("Points: ").getScore();
        scoresTable.put(p.getName(), playerScore);
    }

    public boolean newPlayer(Player p) {
        return !scoresTable.containsKey(p.getName());
    }

    public int getPlayerScore(Player p) {
        if (!newPlayer(p)){
            return scoresTable.get(p.getName());
        }
        return startingAmount;
    }

    public void saveToFile(String fileName){
        File f = new File(fileName);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            scoresTable.forEach((key, value)
                    -> {
                try {
                    writer.write(key + " " + value.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
