package me.vojtasara.noodlegeneration;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialManager {
    public MaterialManager(){
        // By default, select the full mode
        selectedMode = "full.txt";
        loadMaterialsFromFile(selectedMode);
        File modeDirectory = new File("modes");
        File[] filesList = modeDirectory.listFiles();
        if (filesList == null){
            System.out.println("Please add at least one materials file to /modes/");
            return;
        }
        modes = new ArrayList<String>();
        for (File f: filesList) {
            System.out.println(f.getName());
            modes.add(f.getName());
        }
    }
    private ArrayList<String> modes;
    private String selectedMode;
    private Material[] materialsList;

    // Loads the materials from the "allowedMaterials" file provided by user
    public void loadMaterialsFromFile(String name){
        String fileName = "modes/" + name;
        File f = new File(fileName);
        if (!f.exists() || f.isDirectory()) {
            // default value of the materialsList if not loaded from file
            materialsList = new Material[]{
                    Material.DIAMOND_ORE,
                    Material.ACACIA_LOG,
                    Material.DIRT
            };
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            ArrayList<Material> materials = new ArrayList<Material>();
            while ((line = br.readLine()) != null) {
                materials.add(Material.getMaterial(line));
            }
            materialsList = materials.toArray(new Material[0]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Material[] getChoices() {
        return materialsList;
    }

    public void listMaterials(AsyncPlayerChatEvent e) {
        for (Material m:
             materialsList) {
            e.getPlayer().sendMessage(ChatColor.GOLD + m.name());
        }
    }

    public void listModes(AsyncPlayerChatEvent e) {
        e.getPlayer().sendMessage(ChatColor.AQUA + "--- Available modes: ---");
        for (String m:
                modes) {
            if (m.equals(selectedMode)){
                e.getPlayer().sendMessage(ChatColor.GOLD + m);
            }
            else{
                e.getPlayer().sendMessage(m);
            }
        }
        e.getPlayer().sendMessage(ChatColor.AQUA + "--- **************** ---");

    }

    public void selectMode(AsyncPlayerChatEvent e){
        if (modes.contains(e.getMessage().split(" ")[1])){
            selectedMode = e.getMessage().split(" ")[1];
            loadMaterialsFromFile(selectedMode);
        } else {
            e.getPlayer().sendMessage(ChatColor.RED + "That mode doesn't exist, or you forgot to add it to the /modes folder.");
        }
    }
}
