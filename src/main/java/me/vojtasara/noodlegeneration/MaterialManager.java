package me.vojtasara.noodlegeneration;

import org.bukkit.Material;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MaterialManager {
    public MaterialManager(){
        loadMaterialsFromFile();
    }
    private Material[] materialsList;

    // Loads the materials from the "allowedMaterials" file provided by user
    public void loadMaterialsFromFile(){
        String fileName = "allowedMaterials.txt";
        File f = new File(fileName);
        if (!f.exists() || f.isDirectory()) {
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
}
