package business;

import java.util.ArrayList;
import domain.GameLevel;
import domain.Match;
import domain.Player;
import data.FilesJSonMatch;
import data.FilesJSonPlayer;
import presentation.GUI;

public class Controller {

	// --- ATRIBUTOS PRIVADOS (Estilo de cátedra) ---
	private GUI gui;
	private LogicGame logGame;
	private FilesJSonPlayer fJsonPlayer;
	private FilesJSonMatch fJsonMatch;
	
	private Match match;
	private boolean isPlayer1Turn; // Mantiene el control de turnos alternos: true = Jugador 1, false = Jugador 2

	/**
	 * CONSTRUCTOR DEL CONTROLLER
	 * Centraliza la inicialización de capas y carga la persistencia antes de arrancar.
	 */
	public Controller(GUI gui) {
		this.gui = gui;
		this.logGame = new LogicGame();
		
		// Instanciamos los archivos pasándoles la referencia de la GUI tal como lo hace tu ejemplo de clase
		this.fJsonPlayer = new FilesJSonPlayer(gui);
		this.fJsonMatch = new FilesJSonMatch(gui);
		
		this.match = null;
		this.isPlayer1Turn = true; // El jugador 1 siempre inicia por defecto

		// CARGA INICIAL (R4 y R5): Leemos los archivos físicos al arrancar el programa
		fJsonPlayer.readJSonALPlayer("Winners Game.json");
		fJsonMatch.readJSonALMatch("Game Statistics - País.json");
	}

	/**
	 * getActions: Método central de eventos. Vincula los clics de la GUI con 
	 * el dominio y actualiza las estructuras persistentes en caliente.
	 */
	public void getActions() {

		// 1. Inicializar la tabla visual con el historial cargado por Britany
		gui.setOLMatches(fJsonMatch.getALMatch());
		gui.setTVMatches(gui.getOLMatches());

		// 2. Inyectar físicamente el TableView en el contenedor Pane, tal cual hacías con los autos
		//gui.getPContainer().getChildren().add(gui.getTVMatches());

		// --- EVENTO 1: BOTÓN INICIAR PARTIDA ---
		gui.getBStartGame().setOnAction(e -> {
			
			// Validación de seguridad obligatoria: Campos vacíos
			if (gui.getTFIdP1().getText().isEmpty() || gui.getTFNameP1().getText().isEmpty() || gui.getTFNicknameP1().getText().isEmpty() ||
				gui.getTFIdP2().getText().isEmpty() || gui.getTFNameP2().getText().isEmpty() || gui.getTFNicknameP2().getText().isEmpty() ||
				gui.getTFNationality().getText().isEmpty()) {
				
				gui.notify("Error: Todos los datos de inscripción son obligatorios.");
				return;
			}

			// Instanciamos los objetos Player leyendo los TextFields
			Player player1 = new Player(
					gui.getTFIdP1().getText(), 
					gui.getTFNameP1().getText(), 
					gui.getTFNicknameP1().getText(), 
					gui.getTFNationality().getText()
			);
			
			Player player2 = new Player(
					gui.getTFIdP2().getText(), 
					gui.getTFNameP2().getText(), 
					gui.getTFNicknameP2().getText(), 
					gui.getTFNationality().getText()
			);

			// Mapeo estricto del GameLevel según el ComboBox de la GUI
			String selectedLevel = gui.getCBLevel().getValue();
			GameLevel level;
			
			if (selectedLevel.equals("Intermedio")) {
				level = new GameLevel("Intermedio", 8, 5, 12, 6);
			} else if (selectedLevel.equals("Máster")) {
				level = new GameLevel("Máster", 10, 6, 14, 7);
			} else {
				level = new GameLevel("Principiante", 5, 4, 10, 5);
			}

			// Construimos la partida y encendemos el reloj
			match = new Match(player1, player2, level);
			logGame.generateRandomSequence(match);
			isPlayer1Turn = true;

			// Actualizamos la GUI dinámicamente con los ComboBoxes e imágenes PNG transparentes
			gui.buildGameSlots(level.getFiguresRandom(), logGame.getAvailableFigures());
			gui.drawAvailableFiguresPanel(level.getFigures(), logGame.getAvailableFigures());
			
			// Arrancamos el AnimationTimer de la GUI pasándole el objeto Match actual
			//gui.startTimerUpdate(match);

			// Control estricto de habilitación de controles
			gui.getBStartGame().setDisable(true);
			gui.getCBLevel().setDisable(true);
			gui.getBSendAttempt().setDisable(false);
			gui.getBAbandon().setDisable(false);

			gui.getLCurrentTurn().setText("🟢 Partida en marcha. Turno de: " + player1.getNickname());
			gui.getTAResultLog().setText("Señores jugadores, la secuencia secreta ha sido generada. ¡Adivinen!\n");
		});

		// --- EVENTO 2: BOTÓN ENVIAR INTENTO ---
		gui.getBSendAttempt().setOnAction(e -> {
			if (match == null || !match.isMatchStatus()) return;

			// Extraemos los valores seleccionados por el jugador en los ComboBox dinámicos
			ArrayList<String> playerAttemptSequence = new ArrayList<>();
			for (int i = 0; i < gui.getCBPlayerAttempt().size(); i++) {
				String val = gui.getCBPlayerAttempt().get(i).getValue();
				if (val == null) {
					gui.notify("Atención: Debe seleccionar una figura para cada casilla.");
					return;
				}
				playerAttemptSequence.add(val);
			}

			// Procesamos el turno aplicando el algoritmo de la capa lógica
			int[] hints = logGame.processTurn(match, playerAttemptSequence, isPlayer1Turn);
			int goods = hints[0];
			int regulars = hints[1];
			
			// REGLA DE EVALUACIÓN (R7): Calculamos las malas restando (Totales - Buenas - Regulares)
			int totalSlots = match.getGameLevel().getFiguresRandom();
			int bads = totalSlots - (goods + regulars);

			// Determinamos quién está jugando en este milisegundo
			String currentNickname = isPlayer1Turn ? match.getPlayer1().getNickname() : match.getPlayer2().getNickname();
			
			// Imprimimos el resultado del intento en el TextArea de la bitácora
			gui.getTAResultLog().appendText("Jugador: " + currentNickname + " -> Envío: " + playerAttemptSequence 
					+ " | Buenas: " + goods + " | Regulares: " + regulars + " | Malas: " + bads + "\n");

			// Validamos si el juego terminó debido al último intento enviado (Victoria o Derrota)
			if (!match.isMatchStatus()) {
				finalizeSessionGame("¡Fin de la partida! Resultado definitivo: " + match.getResult() + " | Ganador: " + match.getWinnerNickname());
				return;
			}

			// Si el juego sigue activo, alternamos el turno estricto de los jugadores
			isPlayer1Turn = !isPlayer1Turn;
			String nextNickname = isPlayer1Turn ? match.getPlayer1().getNickname() : match.getPlayer2().getNickname();
			gui.getLCurrentTurn().setText("🟢 Turno de: " + nextNickname);
		});

		// --- EVENTO 3: BOTÓN ABANDONAR ---
		gui.getBAbandon().setOnAction(e -> {
			if (match != null && match.isMatchStatus()) {
				// Si un jugador abandona, el ganador automático es el jugador rival
				String winnerByAbandono = isPlayer1Turn ? match.getPlayer2().getNickname() : match.getPlayer1().getNickname();
				logGame.endMatch(match, winnerByAbandono, "Abandono");
				finalizeSessionGame("La partida se cerró por abandono. Ganador por penalización: " + winnerByAbandono);
			}
		});
	}

	/**
	 * finalizeSessionGame: Apaga los relojes, almacena los datos de los ganadores 
	 * en JSON y refresca el historial del TableView en caliente.
	 */
	private void finalizeSessionGame(String finalMessage) {
		//gui.stopTimer(); // Detiene el cronómetro asíncrono
		gui.getLCurrentTurn().setText("❌ Partida Concluida");
		gui.notify(finalMessage);

		// Volcamos el resumen estructurado del toString() en la bitácora
		gui.getTAResultLog().appendText("\n" + match.toString());

		// --- PERSISTENCIA GSON (Trabajo unificado con Britany) ---
		
		// 1. Guardar las estadísticas globales de la partida
		fJsonMatch.getALMatch().add(match);
		fJsonMatch.writeFileJSonMatch("Game Statistics - País.json");

		// 2. Si hubo un ganador real (No es empate/sin resolver), guardamos su registro en ganadores
		if (!match.getWinnerNickname().equals("Ninguno") && !match.getWinnerNickname().isEmpty()) {
			// Identificamos cuál objeto Player ganó la partida para persistir su perfil completo
			Player realWinner = match.getWinnerNickname().equals(match.getPlayer1().getNickname()) ? match.getPlayer1() : match.getPlayer2();
			fJsonPlayer.getALPlayer().add(realWinner);
			fJsonPlayer.writeFileJSonPlayer("Winners Game.json");
		}

		// --- REFRESCAR LA INTERFAZ ---
		// Remapeamos el ObservableList para que la tabla pinte la nueva línea al instante sin reiniciar
		gui.getOLMatches().clear();
		gui.getOLMatches().addAll(fJsonMatch.getALMatch());
		gui.getTVMatches().refresh();

		// Reseteamos las cajas de texto de registro para que puedan volver a jugar
		gui.clearFields();
		
		// Habilitamos de nuevo el bloque de registros
		gui.getBStartGame().setDisable(false);
		gui.getCBLevel().setDisable(false);
		gui.getBSendAttempt().setDisable(true);
		gui.getBAbandon().setDisable(true);
	}
}