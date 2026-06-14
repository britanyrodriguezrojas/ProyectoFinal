package business;
	
import javafx.application.Application;
import javafx.stage.Stage;
import presentation.GUI;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// 1. Instanciamos tu clase de interfaz gráfica (Capa de Presentación)
			GUI gui = new GUI();
			
			// 2. Desplegamos la ventana principal con las configuraciones fijas de cátedra
			gui.start(primaryStage);
			
			// 3. Instanciamos el controlador inyectándole la GUI actual (Capa de Negocio)
			Controller controller = new Controller(gui);
			
			// 4. Encendemos los escuchadores de eventos para bindiar botones, listas y JSONs
			controller.getActions();
			
		} catch(Exception e) {
			// Captura cualquier fallo de inicialización en la consola de Eclipse
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// Lanza la aplicación e inicializa el ciclo de vida nativo de JavaFX
		launch(args);
	}
}