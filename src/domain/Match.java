package domain;

import java.util.ArrayList;

public class Match {

	private Player player1;
	private Player player2;
	private GameLevel gameLevel;
	private ArrayList<String> sequence; //Array para las figuras aleatorias
	private int player1AttemptsUsed;
	private int player2AttemptsUsed;
	private long startTime; //Guarda el milisegundo exacto en que inició la partida
	private long endTime; //Guarda el milisegundo exacto en que terminó
	private String winnerNickname;
	private String result;
	private boolean matchStatus; //Estado de la partida, si ya finalizó o siguen jugando

	public Match() {
		this.sequence = new ArrayList<>();
		this.player1AttemptsUsed = 0;
		this.player2AttemptsUsed = 0;
		this.matchStatus = true;
	}

	public Match(Player player1, Player player2, GameLevel gameLevel) {
		this.player1 = player1;
		this.player2 = player2;
		this.gameLevel = gameLevel;
		this.sequence = new ArrayList<>();
		this.player1AttemptsUsed = 0;
		this.player2AttemptsUsed = 0;
		this.matchStatus = true;
		//Aqui el cronometro empieza a contar al iniciar la partida
		this.startTime = System.currentTimeMillis();
		this.endTime = 0;
		this.winnerNickname = "";
		this.result = "Sin resolver"; //Estado por defecto hasta que alguien gane
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public GameLevel getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(GameLevel gameLevel) {
		this.gameLevel = gameLevel;
	}

	public ArrayList<String> getSequence() {
		return sequence;
	}

	public void setSequence(ArrayList<String> sequence) {
		this.sequence = sequence;
	}

	public int getPlayer1AttemptsUsed() {
		return player1AttemptsUsed;
	}

	public void setPlayer1AttemptsUsed(int player1AttemptsUsed) {
		this.player1AttemptsUsed = player1AttemptsUsed;
	}

	public int getPlayer2AttemptsUsed() {
		return player2AttemptsUsed;
	}

	public void setPlayer2AttemptsUsed(int player2AttemptsUsed) {
		this.player2AttemptsUsed = player2AttemptsUsed;
	}

	public boolean isMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(boolean matchStatus) {
		this.matchStatus = matchStatus;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getWinnerNickname() {
		return winnerNickname;
	}

	public void setWinnerNickname(String winnerNickname) {
		this.winnerNickname = winnerNickname;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	/**
	 * Calcula la duración de la partida y la retorna en formato de minutos y segundos (MM:SS).
	 */
	public String getMatchDuration() {
		long totalSegundos = 0;
		
		if (this.endTime > 0) {
			// Si la partida ya terminó, restamos fin menos inicio
			totalSegundos = (this.endTime - this.startTime) / 1000;
		} else {
			// Si la partida sigue activa, calcula el tiempo transcurrido hasta este milisegundo
			totalSegundos = (System.currentTimeMillis() - this.startTime) / 1000;
		}
		
		long minutos = totalSegundos / 60;
		long segundosRestantes = totalSegundos % 60;
		
		// Si los segundos son menores a 10, le agregamos un 0 a la izquierda para que no se vea "1:5" sino "1:05"
		if (segundosRestantes < 10) {
			return minutos + ":0" + segundosRestantes;
		} else {
			return minutos + ":" + segundosRestantes;
		}
	}

	@Override
	public String toString() {
		// Creamos variables locales con textos por defecto por si están vacíos (null)
		String nombreNivel = "No asignado";
		String apodoP1 = "No asignado";
		String apodoP2 = "No asignado";
		String nombreGanador = "Ninguno aún";
		
		// Variables para controlar los intentos totales del nivel
		int intentosTotalesPorJugador = 0;

		// Validamos con IF tradicionales, justo como se ve en clase
		if (this.gameLevel != null) {
			nombreNivel = this.gameLevel.getLevel();
			// Jalamos los intentos que permite el nivel por jugador
			intentosTotalesPorJugador = this.gameLevel.getAttemptsPlayer();
		}
		
		if (this.player1 != null) {
			apodoP1 = this.player1.getNickname();
		}
		
		if (this.player2 != null) {
			apodoP2 = this.player2.getNickname();
		}
		
		if (this.winnerNickname != null && !this.winnerNickname.isEmpty()) {
			nombreGanador = this.winnerNickname;
		}

		// Armamos la cadena final mostrando "usados / totales"
		return "** DETALLES DE LA PARTIDA ** \n" +
				"Secuencia Aleatoria: " + this.sequence + "\n" +
				"Nivel: " + nombreNivel + "\n" +
				"Jugador 1: " + apodoP1 + " (Intentos: " + this.player1AttemptsUsed + "/" 
				+ intentosTotalesPorJugador + ")\n" +
				"Jugador 2: " + apodoP2 + " (Intentos: " + this.player2AttemptsUsed + "/" 
				+ intentosTotalesPorJugador + ")\n" +
				"Resultado: " + this.result + "\n" +
				"Ganador: " + nombreGanador + "\n" +
				"Duración de la partida: " + getMatchDuration() + " segundos\n" +
				"==============================\n";
	}
}