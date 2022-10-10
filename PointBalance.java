import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import org.json.simple.JSONObject;

public class PointBalance {
    private configFileName = "playerScores.txt"
    public writeScore(playerName, playerScore)
    {
        try {
            File myObj = new File(configFileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            public List<String> lines = Files.readAllLines(Paths.get(configFileName), StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
