module ProyectoFinal {
    // --- LIBRERÍAS REQUERIDAS POR EL ENTORNO ---
    requires javafx.controls;
    requires javafx.base;
    requires com.google.gson;

    // --- PERMISOS DE ACCESO ENTRE CAPAS ---
    // Permite que JavaFX dibuje las interfaces gráficas de la presentación
    exports presentation; 
    
    // Abre las capas de negocio y dominio para que JavaFX y Gson puedan leerlas internamente
    opens business to javafx.graphics, javafx.fxml;
    opens domain to com.google.gson, javafx.base;
}