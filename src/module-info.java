module ProyectoFinal {
    requires javafx.controls;
    
    requires com.google.gson;

    opens business to javafx.graphics, javafx.fxml;
   
    opens domain to com.google.gson;
}