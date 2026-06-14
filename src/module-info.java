module ProyectoFinal {
 ParteBritany
    requires javafx.controls;
    
    requires com.google.gson;

    opens business to javafx.graphics, javafx.fxml;
   
    opens domain to com.google.gson;
}

	requires javafx.controls;
	requires javafx.base;
	
	opens business to javafx.graphics, javafx.fxml;
}
 main
