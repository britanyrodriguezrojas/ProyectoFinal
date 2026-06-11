package domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player {

    private String id;
    private String name;
    private String nickname;
    private String nationality;

    public Player() {}

    public Player(String id, String name, String nickname, String nationality) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.nationality = nationality;
    }

    // --- Propiedades para JavaFX (Por si usamos tablas o bindeos) ---
    public StringProperty idProperty() {
        return new SimpleStringProperty(id);
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public StringProperty nicknameProperty() {
        return new SimpleStringProperty(nickname);
    }

    public StringProperty nationalityProperty() {
        return new SimpleStringProperty(nationality);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "Cédula: " + id + " - Nombre: " + name + 
        		" - Nickname: " + nickname + " - Nacionalidad: " + nationality + "\n"; 
    }
}