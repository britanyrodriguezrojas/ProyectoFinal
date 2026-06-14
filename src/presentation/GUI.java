package presentation;

import java.io.File;
import java.util.ArrayList;
import domain.Match;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GUI {

	// --- ATRIBUTOS DE CONFIGURACIÓN DE LA VENTANA ---
	private Scene myScene;       // Escena principal que contendrá los elementos visuales.
	private Pane pContainer;     // Contenedor libre (coordenadas fijas) obligatorio de la clase.

	// --- ATRIBUTOS DE INTERFAZ: REGISTRO DE JUGADORES (Sección Superior) ---
	private Label lTitle;        // Etiqueta del título principal del juego.
	private Label lIdP1, lNameP1, lNicknameP1; // Etiquetas para identificar los campos del Jugador 1.
	private Label lIdP2, lNameP2, lNicknameP2; // Etiquetas para identificar los campos del Jugador 2.
	private Label lNationality, lLevel;        // Etiquetas para la nacionalidad y la selección de nivel.
	
	private TextField tFIdP1, tFNameP1, tFNicknameP1; // Cuadros de texto para capturar los datos del Jugador 1.
	private TextField tFIdP2, tFNameP2, tFNicknameP2; // Cuadros de texto para capturar los datos del Jugador 2.
	private TextField tFNationality;                  // Cuadro de texto para la nacionalidad (compartida por ambos).
	
	private ComboBox<String> cBLevel; // Selector desplegable para elegir el nivel ("Principiante", "Intermedio", "Máster").
	private Button bStartGame;        // Botón para validar registros e iniciar el cronómetro y la partida.

	// --- ATRIBUTOS DE INTERFAZ: TABLERO DE JUEGO ACTIVO (Sección Central) ---
	private Label lCurrentTurn;  // Indica textualmente a quién le toca jugar (Ej: "Turno de: Vic26").
	private Label lCronometro;   // Label especial que simulará el reloj digital en la esquina de la pantalla.
	private Label lInstructions; // Texto de guía para saber qué figuras están permitidas usar en el nivel actual.
	private Button bSendAttempt; // Botón para confirmar y evaluar la combinación de figuras seleccionada.
	private Button bAbandon;     // Botón para rendirse y finalizar la partida prematuramente.
	
	// Lista dinámica de ComboBoxes. Cambiará de tamaño (4, 5 o 6 campos) según el nivel del juego.
	private ArrayList<ComboBox<String>> cBPlayerAttempt;
	
	// Contenedor secundario libre donde dibujaremos las imágenes de las figuras permitidas según el nivel.
	private Pane pAvailableFiguresImages;
	
	// --- ATRIBUTOS DE INTERFAZ: HISTORIAL Y BITÁCORA (Sección Inferior) ---
	private TableView<Match> tVMatches;       // Tabla visual que renderizará el historial de partidas terminadas.
	private ObservableList<Match> oLMatches; // Lista observable especial de JavaFX vinculada directamente a la tabla.
	private TextArea tAResultLog;             // Área de texto grande para imprimir los aciertos (Buenas y Regulares) de cada intento.
	
	// --- ATRIBUTOS LÓGICOS DE CONTROL DE TIEMPO ---
	private AnimationTimer gameTimer; // Hilo nativo de JavaFX que refrescará la pantalla 60 veces por segundo sin congelar el programa.
	private Match currentMatch;       // Guarda la partida activa para leer su duración en tiempo real.

	/**
	 * CONSTRUCTOR DE LA CLASE GUI
	 * Se encarga de inicializar los contenedores e invocar los métodos de creación de componentes.
	 */
	public GUI() {
		// Inicialización de contenedores y colecciones de la vista
		pContainer = new Pane();
		cBPlayerAttempt = new ArrayList<>();
		pAvailableFiguresImages = new Pane();

		// 1. Inicialización de los textos estáticos en pantalla (Labels) con sus posiciones iniciales
		setLTitle("🎯 ADIVINAR FIGURAS - PROGRAMACIÓN II 🎯");
		setLIdP1("Cédula Jugador 1:");
		setLNameP1("Nombre Jugador 1:");
		setLNicknameP1("Nickname J1:");
		setLIdP2("Cédula Jugador 2:");
		setLNameP2("Nombre Jugador 2:");
		setLNicknameP2("Nickname J2:");
		setLNationality("Nacionalidad (Ambos):");
		setLLevel("Seleccionar Nivel:");
		
		// Textos que cambian de valor dinámicamente durante el flujo del juego
		setLCurrentTurn("Esperando inicio de partida...", 20, 220);
		setLCronometro("⏳ Tiempo: 0:00", 620, 20);
		setLInstructions("Figuras disponibles en este nivel:", 20, 430);

		// 2. Inicialización de los campos de texto y desplegables de datos
		setTFIdP1(); setTFNameP1(); setTFNicknameP1();
		setTFIdP2(); setTFNameP2(); setTFNicknameP2();
		setTFNationality();
		setCBLevel();

		// 3. Inicialización de los botones de interacción
		setBStartGame("Iniciar Partida");
		setBSendAttempt("Enviar Intento");
		setBAbandon("Abandonar Partida");

		// Regla de negocio inicial: No se puede jugar ni abandonar si la partida no ha empezado.
		bSendAttempt.setDisable(true);
		bAbandon.setDisable(true);

		// 4. Inicialización de la bitácora de texto inferior
		setTAResultLog();

		// 5. Montaje de todos los elementos iniciales dentro del contenedor principal
		setPContainer();
		setMyScene(pContainer);
	}

	/**
	 * start: Método oficial de JavaFX para desplegar la ventana principal en el sistema operativo.
	 */
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Proyecto 1: Adivinar Figuras");
		primaryStage.setMinWidth(820);  // Ancho mínimo seguro para que quepan las tablas y paneles
		primaryStage.setMinHeight(680); // Alto mínimo seguro
		primaryStage.setScene(getMyScene());
		primaryStage.show();
	}

	// --- MÉTODOS DE ASIGNACIÓN (SETTERS) Y RETORNO (GETTERS) EN FORMATO DE CÁTEDRA ---

	public void setMyScene(Pane pContainer) {
		myScene = new Scene(pContainer);
	}

	public Scene getMyScene() {
		return myScene;
	}

	public Pane getPContainer() {
		return pContainer;
	}

	/**
	 * setPContainer: Limpia el panel e incrusta los elementos iniciales. 
	 * Se usa para reestructurar la ventana cada vez que se arranca un juego nuevo.
	 */
	public void setPContainer() {
		pContainer.getChildren().clear(); // Evita duplicar elementos en la memoria visual de JavaFX
		
		// Inyección de componentes del bloque de registro superior
		pContainer.getChildren().addAll(lTitle, lIdP1, tFIdP1, lNameP1, tFNameP1, lNicknameP1, tFNicknameP1);
		pContainer.getChildren().addAll(lIdP2, tFIdP2, lNameP2, tFNameP2, lNicknameP2, tFNicknameP2);
		pContainer.getChildren().addAll(lNationality, tFNationality, lLevel, cBLevel, bStartGame);
		
		// Inyección de componentes de la sección del juego activo
		pContainer.getChildren().addAll(lCurrentTurn, bSendAttempt, bAbandon, lInstructions, pAvailableFiguresImages);
		pContainer.getChildren().addAll(tAResultLog);
	}

	// --- MÉTODOS DE INICIALIZACIÓN PROPIOS (Establecen texto, dimensiones y coordenadas exactas) ---

	public void setLTitle(String text) {
		lTitle = new Label(text);
		lTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		lTitle.setPrefSize(500, 30);
		lTitle.setTranslateX(20); lTitle.setTranslateY(15);
	}

	public void setLIdP1(String text) {
		lIdP1 = new Label(text);
		lIdP1.setPrefSize(120, 20); lIdP1.setTranslateX(20); lIdP1.setTranslateY(60);
	}

	public void setTFIdP1() {
		tFIdP1 = new TextField();
		tFIdP1.setPrefSize(110, 20); tFIdP1.setTranslateX(20); tFIdP1.setTranslateY(85);
	}

	public void setLNameP1(String text) {
		lNameP1 = new Label(text);
		lNameP1.setPrefSize(120, 20); lNameP1.setTranslateX(145); lNameP1.setTranslateY(60);
	}

	public void setTFNameP1() {
		tFNameP1 = new TextField();
		tFNameP1.setPrefSize(110, 20); tFNameP1.setTranslateX(145); tFNameP1.setTranslateY(85);
	}

	public void setLNicknameP1(String text) {
		lNicknameP1 = new Label(text);
		lNicknameP1.setPrefSize(120, 20); lNicknameP1.setTranslateX(270); lNicknameP1.setTranslateY(60);
	}

	public void setTFNicknameP1() {
		tFNicknameP1 = new TextField();
		tFNicknameP1.setPrefSize(110, 20); tFNicknameP1.setTranslateX(270); tFNicknameP1.setTranslateY(85);
	}

	public void setLIdP2(String text) {
		lIdP2 = new Label(text);
		lIdP2.setPrefSize(120, 20); lIdP2.setTranslateX(20); lIdP2.setTranslateY(120);
	}

	public void setTFIdP2() {
		tFIdP2 = new TextField();
		tFIdP2.setPrefSize(110, 20); tFIdP2.setTranslateX(20); tFIdP2.setTranslateY(145);
	}

	public void setLNameP2(String text) {
		lNameP2 = new Label(text);
		lNameP2.setPrefSize(120, 20); lNameP2.setTranslateX(145); lNameP2.setTranslateY(120);
	}

	public void setTFNameP2() {
		tFNameP2 = new TextField();
		tFNameP2.setPrefSize(110, 20); tFNameP2.setTranslateX(145); tFNameP2.setTranslateY(145);
	}

	public void setLNicknameP2(String text) {
		lNicknameP2 = new Label(text);
		lNicknameP2.setPrefSize(120, 20); lNicknameP2.setTranslateX(270); lNicknameP2.setTranslateY(120);
	}

	public void setTFNicknameP2() {
		tFNicknameP2 = new TextField();
		tFNicknameP2.setPrefSize(110, 20); tFNicknameP2.setTranslateX(270); tFNicknameP2.setTranslateY(145);
	}

	public void setLNationality(String text) {
		lNationality = new Label(text);
		lNationality.setPrefSize(140, 20); lNationality.setTranslateX(400); lNationality.setTranslateY(60);
	}

	public void setTFNationality() {
		tFNationality = new TextField();
		tFNationality.setPrefSize(130, 20); tFNationality.setTranslateX(400); tFNationality.setTranslateY(85);
	}

	public void setLLevel(String text) {
		lLevel = new Label(text);
		lLevel.setPrefSize(140, 20); lLevel.setTranslateX(400); lLevel.setTranslateY(120);
	}

	public void setCBLevel() {
		cBLevel = new ComboBox<>();
		cBLevel.getItems().addAll("Principiante", "Intermedio", "Máster");
		cBLevel.setValue("Principiante");
		cBLevel.setPrefSize(130, 20); cBLevel.setTranslateX(400); cBLevel.setTranslateY(145);
	}

	public void setBStartGame(String text) {
		bStartGame = new Button(text);
		bStartGame.setPrefSize(180, 40);
		bStartGame.setTranslateX(560); bStartGame.setTranslateY(110);
		bStartGame.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	}

	public void setLCurrentTurn(String text, int x, int y) {
		lCurrentTurn = new Label(text);
		lCurrentTurn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
		lCurrentTurn.setPrefSize(400, 25);
		lCurrentTurn.setTranslateX(x); lCurrentTurn.setTranslateY(y);
	}

	/**
	 * setLCronometro: Configura el panel del reloj dándole un estilo retro digital 
	 * (fondo negro y letras verde neón) para que resalte en la esquina superior derecha.
	 */
	public void setLCronometro(String text, int x, int y) {
		lCronometro = new Label(text);
		lCronometro.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
		lCronometro.setStyle("-fx-background-color: black; -fx-text-fill: #00FF00; -fx-padding: 5;");
		lCronometro.setPrefSize(160, 35);
		lCronometro.setTranslateX(x); lCronometro.setTranslateY(y);
	}

	public void setLInstructions(String text, int x, int y) {
		lInstructions = new Label(text);
		lInstructions.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
		lInstructions.setPrefSize(300, 20);
		lInstructions.setTranslateX(x); lInstructions.setTranslateY(y);
	}

	public void setBSendAttempt(String text) {
		bSendAttempt = new Button(text);
		bSendAttempt.setPrefSize(120, 30);
		bSendAttempt.setTranslateX(520); bSendAttempt.setTranslateY(260);
	}

	public void setBAbandon(String text) {
		bAbandon = new Button(text);
		bAbandon.setPrefSize(120, 30);
		bAbandon.setTranslateX(660); bAbandon.setTranslateY(260);
	}

	public void setTAResultLog() {
		tAResultLog = new TextArea();
		tAResultLog.setEditable(false); // Bloquea que el usuario borre los logs de intentos
		tAResultLog.setPrefSize(760, 110);
		tAResultLog.setTranslateX(20); tAResultLog.setTranslateY(310);
		tAResultLog.setPromptText("Resultados e historial de intentos aquí...");
	}

	// --- MÉTODOS DE GENERACIÓN DINÁMICA DE ELEMENTOS GRÁFICOS ---

	/**
	 * buildGameSlots: Este método es el corazón gráfico del juego. 
	 * Borra los selectores viejos e inyecta en caliente tantos ComboBoxes nuevos como requiera el nivel.
	 * (Principiante = 4 casillas secretas, Intermedio = 5 casillas, Máster = 6 casillas).
	 */
	public void buildGameSlots(int amountOfSlots, ArrayList<String> availableFigures) {
		// 1. Remover visualmente los ComboBox de la partida anterior del panel de JavaFX
		for (ComboBox<String> cb : cBPlayerAttempt) {
			pContainer.getChildren().remove(cb);
		}
		cBPlayerAttempt.clear(); // Vaciar el arreglo dinámico

		int startX = 20;
		int startY = 260;
		int separation = 80; // Controla la distancia en pixeles entre cada casilla en el eje X

		// 2. Generar y posicionar las nuevas casillas según los parámetros del nivel
		for (int i = 0; i < amountOfSlots; i++) {
			ComboBox<String> cBFig = new ComboBox<>();
			cBFig.getItems().addAll(availableFigures); // Carga las figuras válidas del nivel
			if (!availableFigures.isEmpty()) cBFig.setValue(availableFigures.get(0));
			
			cBFig.setPrefSize(75, 25);
			cBFig.setTranslateX(startX + (i * separation)); // Multiplica el desplazamiento para alinearlos horizontalmente
			cBFig.setTranslateY(startY);

			cBPlayerAttempt.add(cBFig);          // Lo guarda en el ArrayList para que el Controller lea su selección
			pContainer.getChildren().add(cBFig); // Lo añade físicamente a la ventana activa
		}
	}

	/**
	 * drawAvailableFiguresPanel: Escanea el disco duro buscando los archivos .png de las figuras permitidas
	 * y los dibuja en filas ordenadas con su respectivo nombre abajo. Cumple con la solicitud estética.
	 */
	public void drawAvailableFiguresPanel(int totalFiguresAllowed, ArrayList<String> allFigures) {
		pAvailableFiguresImages.getChildren().clear(); // Limpia los dibujos del juego anterior
		pAvailableFiguresImages.setPrefSize(760, 50);
		pAvailableFiguresImages.setTranslateX(20); pAvailableFiguresImages.setTranslateY(440);

		int xOffset = 0; // Controla la separación horizontal de cada imagen con su texto
		for (int i = 0; i < totalFiguresAllowed; i++) {
			String figureName = allFigures.get(i);
			
			// Busca el recurso local en la carpeta raíz del proyecto
			String path = "images/" + figureName + ".png";
			File file = new File(path);

			if (file.exists()) {
				// Carga la imagen nativamente y define dimensiones uniformes
				Image img = new Image(file.toURI().toString());
				ImageView iView = new ImageView(img);
				iView.setFitWidth(35);
				iView.setFitHeight(35);
				iView.setTranslateX(xOffset); iView.setTranslateY(0);
				
				// Agrega la etiqueta con el nombre debajo de la imagen
				Label lName = new Label(figureName);
				lName.setFont(Font.font("Arial", 9));
				lName.setTranslateX(xOffset); lName.setTranslateY(35);

				// Inyecta el par imagen-texto en el mini panel
				pAvailableFiguresImages.getChildren().addAll(iView, lName);
				xOffset += 75; // Desplazamiento para pintar la siguiente figura a la derecha
			}
		}
	}

	// --- MÉTODOS DE MANEJO ASÍNCRONO DEL CRONÓMETRO ---

	/**
	 * startTimerUpdate: Ejecuta el reloj digital en segundo plano. 
	 * Se conecta directamente al método `getMatchDuration()` del Match sin colgar los botones.
	 */
	public void startTimerUpdate(Match match) {
		this.currentMatch = match;
		if (gameTimer != null) {
			gameTimer.stop(); // Resetea cualquier conteo remanente previo
		}

		// Inicializa un hilo gráfico de alta velocidad nativo de JavaFX
		gameTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				// Si la partida está activa, extrae los segundos y actualiza el texto en negro y verde
				if (currentMatch != null && currentMatch.isMatchStatus()) {
					lCronometro.setText("⏳ Tiempo: " + currentMatch.getMatchDuration());
				} else {
					stop(); // Si la partida se apaga (Fin, Victoria o Abandono), destruye el hilo de tiempo
				}
			}
		};
		gameTimer.start(); // Enciende el cronómetro gráfico
	}

	public void stopTimer() {
		if (gameTimer != null) {
			gameTimer.stop();
		}
	}

	// --- CONSTRUCCIÓN DEL HISTORIAL MEDIANTE TABLEVIEW (Estilo de Clase) ---

	/**
	 * setTVMatches: Crea las columnas obligatorias del historial y bindea las celdas
	 * con las propiedades nativas del objeto Match de la rama main.
	 */
	public void setTVMatches(ObservableList<Match> oLMatches) {
		tVMatches = new TableView<>(oLMatches);

		// Columna que extrae recursivamente el StringProperty del nivel del GameLevel
		TableColumn<Match, String> levelCol = new TableColumn<>("Nivel");
		levelCol.setCellValueFactory(cell -> cell.getValue().getGameLevel().levelProperty());

		// Columna que encapsula el apodo del ganador en un StringProperty en caliente
		TableColumn<Match, String> winnerCol = new TableColumn<>("Ganador");
		winnerCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWinnerNickname()));

		// Columna que renderiza el tiempo de duración acumulado de la partida
		TableColumn<Match, String> durationCol = new TableColumn<>("Duración");
		durationCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMatchDuration()));

		// Registra las columnas, fija el tamaño y añade la tabla al contenedor de la ventana
		tVMatches.getColumns().addAll(levelCol, winnerCol, durationCol);
		tVMatches.setPrefSize(760, 110);
		tVMatches.setTranslateX(20); tVMatches.setTranslateY(510);
		
		pContainer.getChildren().add(tVMatches);
	}

	public void setOLMatches(ArrayList<Match> aLMatches) {
		oLMatches = FXCollections.observableArrayList(aLMatches);
	}

	public ObservableList<Match> getOLMatches() {
		return oLMatches;
	}

	public TableView<Match> getTVMatches() {
		return tVMatches;
	}

	// --- GETTERS EXPLICITOS QUE USARÁ LA CLASE CONTROLLER PARA CAPTURAR EVENTOS ---

	public TextField getTFIdP1() { return tFIdP1; }
	public TextField getTFNameP1() { return tFNameP1; }
	public TextField getTFNicknameP1() { return tFNicknameP1; }
	public TextField getTFIdP2() { return tFIdP2; }
	public TextField getTFNameP2() { return tFNameP2; }
	public TextField getTFNicknameP2() { return tFNicknameP2; }
	public TextField getTFNationality() { return tFNationality; }
	public ComboBox<String> getCBLevel() { return cBLevel; }
	public Button getBStartGame() { return bStartGame; }
	public Button getBSendAttempt() { return bSendAttempt; }
	public Button getBAbandon() { return bAbandon; }
	public Label getLCurrentTurn() { return lCurrentTurn; }
	public ArrayList<ComboBox<String>> getCBPlayerAttempt() { return cBPlayerAttempt; }
	public TextArea getTAResultLog() { return tAResultLog; }

	/**
	 * clearFields: Limpia todas las cajas de texto de inscripción de jugadores.
	 */
	public void clearFields() {
		tFIdP1.clear(); tFNameP1.clear(); tFNicknameP1.clear();
		tFIdP2.clear(); tFNameP2.clear(); tFNicknameP2.clear();
		tFNationality.clear();
	}

	/**
	 * notify: Despliega ventanas emergentes flotantes de información en el monitor.
	 */
	public void notify(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Notificación del Juego");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}