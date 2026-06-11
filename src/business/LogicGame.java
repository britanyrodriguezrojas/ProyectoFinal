package business;

import java.util.ArrayList;
import domain.GameLevel;
import domain.Match;

public class LogicGame {

	private ArrayList<String> availableFigures;

	public LogicGame() {
		this.availableFigures = new ArrayList<>();

		this.availableFigures.add("Estrella");
		this.availableFigures.add("Diamante");
		this.availableFigures.add("Corazón");
		this.availableFigures.add("Luna");
		this.availableFigures.add("Sol");
		this.availableFigures.add("Nube");
		this.availableFigures.add("Rayo");
		this.availableFigures.add("Escudo");
		this.availableFigures.add("Espada");
		this.availableFigures.add("Corona");
	}

	/**
	 * Genera la secuencia secreta aleatoria para la partida según las reglas del nivel.
	 * Permite figuras repetidas.
	 */
	public void generateRandomSequence(Match match) {
		GameLevel gameLevel = match.getGameLevel();

		// Se limpia el ArrayList random de partidas anteriores
		match.getSequence().clear(); 

		int figuresAmount = gameLevel.getFigures(); 
		int sequenceSize = gameLevel.getFiguresRandom();

		for (int i = 0; i < sequenceSize; i++) {
			// Generamos un índice aleatorio entre 0 y (figuresAmount - 1)
			int randomIndex = (int) (Math.random() * figuresAmount);

			// Tomamos el nombre de la figura de nuestra lista de disponibles
			String randomFigure = availableFigures.get(randomIndex);

			// La metemos en la secuencia de la partida
			match.getSequence().add(randomFigure);
		}
	}

	/**
	 * Busca si una figura específica existe dentro de la secuencia secreta.
	 * Utiliza un ciclo for-each para recorrer los elementos.
	 */
	public boolean searchFigureInSequence(ArrayList<String> secretSequence, String figureToSearch) {
		// Bucle for-each: recorre cada elemento tipo String de la secuencia
		for (String figure : secretSequence) {
			if (figure.equalsIgnoreCase(figureToSearch)) {
				return true; // Si la encuentra, corta el ciclo y retorna verdadero
			}
		}
		return false; // Si termina el ciclo y no la vio, retorna falso
	}

	/**
	 * Evalúa el intento de un jugador comparándolo con la secuencia secreta.
	 * Maneja correctamente las figuras repetidas "tachando" las ya utilizadas.
	 * Retorna un array de enteros con dos posiciones: [0] = Buenas, [1] = Regulares.
	 */
	public int[] evaluatePlayerAttempt(Match match, ArrayList<String> playerFiguresSequence) {
		ArrayList<String> secretSequence = match.getSequence();
		int size = secretSequence.size();

		// Creamos copias de las listas para tachar elementos sin alterar la partida real
		ArrayList<String> secretCopy = new ArrayList<>(secretSequence);
		ArrayList<String> playerFSCopy = new ArrayList<>(playerFiguresSequence);

		int goods = 0;
		int regulars = 0;

		// --- FASE 1: BUSCAR LAS BUENAS (Misma figura en la misma posición) ---
		for (int i = 0; i < size; i++) {
			String secretFig = secretCopy.get(i);
			String playerFig = playerFSCopy.get(i);

			if (secretFig.equalsIgnoreCase(playerFig)) {
				goods++;
				// Tachamos ambas casillas para que queden fuera de la Fase 2
				secretCopy.set(i, "TACHADO_BUENO");
				playerFSCopy.set(i, "PROCESADO_BUENO");
			}
		}

		// --- FASE 2: BUSCAR LAS REGULARES (Existe pero en diferente posición) ---
		for (int i = 0; i < size; i++) {
		    String playerFig = playerFSCopy.get(i);

		    // Lee: "Si la figura del jugador NO es igual a PROCESADO_BUENO..."
		    if (!playerFig.equals("PROCESADO_BUENO")) {
		        
		        // Metemos el ciclo de búsqueda manual aquí adentro, limpiecito:
		        for (int j = 0; j < size; j++) {
		            String secretFig = secretCopy.get(j);

		            if (secretFig.equalsIgnoreCase(playerFig)) {
		                regulars++;
		                secretCopy.set(j, "TACHADO_REGULAR");
		                break;
		            }
		        }
		        
		    }
		}

		// Retornamos el arreglo con los dos resultados [Buenas, Regulares]
		return new int[] { goods, regulars };
	}
	
	/**
	 * Procesa el turno de un jugador. Aumenta sus intentos, evalúa sus figuras
	 * y verifica si la partida debe finalizar (ya sea por victoria o por quedarse sin intentos).
	 * @return int[] Un arreglo con [Buenas, Regulares] para mostrar en la interfaz.
	 */
	public int[] processTurn(Match match, ArrayList<String> playerFiguresSequence, boolean isPlayer1) {
		// 1. Evaluamos el intento usando tu algoritmo de tachado
		int[] hints = evaluatePlayerAttempt(match, playerFiguresSequence);
		int goods = hints[0];
		
		int maxAttempts = match.getGameLevel().getAttemptsPlayer();
		String currentNickname = "";

		// 2. Aumentamos el intento al jugador correspondiente y guardamos su apodo
		if (isPlayer1) {
			match.setPlayer1AttemptsUsed(match.getPlayer1AttemptsUsed() + 1);
			currentNickname = match.getPlayer1().getNickname();
		} else {
			match.setPlayer2AttemptsUsed(match.getPlayer2AttemptsUsed() + 1);
			currentNickname = match.getPlayer2().getNickname();
		}

		// 3. REGLA DE VICTORIA: Si sacó todas buenas (la cantidad de figuras de la secuencia)
		if (goods == match.getGameLevel().getFiguresRandom()) {
			this.endMatch(match, currentNickname, "Ganada");
			return hints; // Retornamos las pistas de una vez
		}

		// 4. REGLA DE DERROTA: Si ambos jugadores gastaron todos sus intentos y nadie ganó
		if (match.getPlayer1AttemptsUsed() >= maxAttempts && match.getPlayer2AttemptsUsed() >= maxAttempts) {
			this.endMatch(match, "Ninguno", "Sin resolver");
		}

		// Retornamos las pistas para que la GUI las pinte en pantalla
		return hints;
	}
	
	/**
	 * Sirve de puente para obtener la duración formateada de la partida
	 * sin que la capa de interfaz gráfica toque directamente el modelo.
	 */
	public String getGameDuration(Match match) {
		if (match == null) {
			return "0:00";
		}
		return match.getMatchDuration();
	}
	
	/**
	 * Finaliza formalmente una partida, deteniendo el cronómetro y registrando
	 * los resultados definitivos de ganadores y estados.
	 */
	public void endMatch(Match match, String winnerNickname, String result) {
		if (match != null) {
			// 1. Detenemos el reloj capturando el milisegundo actual
			match.setEndTime(System.currentTimeMillis());
			
			// 2. Apagamos el estado de juego activo
			match.setMatchStatus(false);
			
			// 3. Asignamos el ganador definitivo ("Vic26", "Bri" o "Empate")
			match.setWinnerNickname(winnerNickname);
			
			// 4. Cambiamos el resultado ("Ganada" o "Sin resolver")
			match.setResult(result);
		}
	}
	
	public ArrayList<String> getAvailableFigures() {
		return this.availableFigures;
	}
}