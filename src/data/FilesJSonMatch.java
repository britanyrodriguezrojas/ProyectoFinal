package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import domain.Match;
import presentation.GUI;

public class FilesJSonMatch {

    private GUI gui;
    private ArrayList<Match> aLMatch;

    public FilesJSonMatch(GUI gui) {
        this.gui = gui;
        aLMatch = new ArrayList<Match>();
    }

    public void writeFileJSonMatch(String addressFile) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(addressFile)) {
            gson.toJson(aLMatch, writer);
            gui.notify("ARCHIVO GUARDADO");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Match> readJSonALMatch(String address) {
        Gson gson = new Gson();
        File fileJ = new File(address);

        try (FileReader reader = new FileReader(fileJ)) {
            if (fileJ.exists()) {
                aLMatch = gson.fromJson(reader, new TypeToken<ArrayList<Match>>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return aLMatch;
    }

    public ArrayList<Match> getALMatch() {
        return aLMatch;
    }
}
