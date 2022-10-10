package me.vojtasara.noodlegeneration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class PlayerScores {
    PlayerScores (){
        System.out.println("Scores init");
    }
    public Hashtable<String,Integer> scoresTable = new Hashtable<String,Integer>();

    public void loadFromFile(String fileName){
        File f = new File(fileName);
        if (!f.exists() || f.isDirectory()) {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
                writer.write("monibred 4154");
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

    // TODO: save to file
}
