package domain;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GameLevel {

    private String level; // "Principiante", "Intermedio", "Máster"
    private int figures; // 5, 8 o 10 Figuras Disponibles
    private int figuresRandom; // 4, 5 o 6 Figuras Aleatorias
    private int totalAttempts; // 10, 12 o 14 Intentos Totales
    private int attemptsPlayer; // 5, 6 o 7 Intentos por Jugador

    public GameLevel() {}

    public GameLevel(String level, int figures, int figuresRandom, 
    		int totalAttempts, int attemptsPlayer) {
    	
        this.level = level;
        this.figures = figures;
        this.figuresRandom = figuresRandom;
        this.totalAttempts = totalAttempts;
        this.attemptsPlayer = attemptsPlayer;
    }
    
    public StringProperty levelProperty() {
        return new SimpleStringProperty(level);
    }
    
    public IntegerProperty figuresProperty() {
        return new SimpleIntegerProperty(figures);
    }
    
    public IntegerProperty figuresRandomProperty() {
        return new SimpleIntegerProperty(figuresRandom);
    }

    public IntegerProperty totalAttemptsProperty() {
        return new SimpleIntegerProperty(totalAttempts);
    }
    
    public IntegerProperty attemptsPlayerProperty() {
        return new SimpleIntegerProperty(attemptsPlayer);
    }
    
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getFigures() {
        return figures;
    }

    public void setFigures(int figures) {
        this.figures = figures;
    }

    public int getFiguresRandom() {
        return figuresRandom;
    }

    public void setFiguresRandom(int figuresRandom) {
        this.figuresRandom = figuresRandom;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public int getAttemptsPlayer() {
        return attemptsPlayer;
    }

    public void setAttemptsPlayer(int attemptsPlayer) {
        this.attemptsPlayer = attemptsPlayer;
    }

    @Override
    public String toString() {
        return "Nivel: " + level + " - Figuras Disponibles: " + figures + 
        		" - Sequencia de Figuras Aleatorias: " + figuresRandom + 
        		" - Total de Intentos: " + totalAttempts + "\n";
    }
}