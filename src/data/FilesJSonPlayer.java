package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import domain.Player;
import presentation.GUI;

public class FilesJSonPlayer {

    private GUI gui;
    private ArrayList<Player> aLPlayer;

    public FilesJSonPlayer(GUI gui) {
        this.gui = gui;
        aLPlayer = new ArrayList<Player>();
    }

    public void writeFileJSonPlayer(String addressFile) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(addressFile)) {
            gson.toJson(aLPlayer, writer);
            gui.notify("ARCHIVO GUARDADO");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Player> readJSonALPlayer(String address) {
        Gson gson = new Gson();
        File fileJ = new File(address);

        try (FileReader reader = new FileReader(fileJ)) {
            if (fileJ.exists()) {
                aLPlayer = gson.fromJson(reader, new TypeToken<ArrayList<Player>>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return aLPlayer;
    }

    public ArrayList<Player> getALPlayer() {
        return aLPlayer;
    }
}
