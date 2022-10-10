package me.vojtasara.noodlegeneration;

import java.util.Hashtable;

public class PlayerScores {
    PlayerScores (){
        System.out.println("Scores init");
    }
    public Hashtable<String,Integer> scoresTable = new Hashtable<String,Integer>();

    public void loadFromFile(String fileName){

    }
}
