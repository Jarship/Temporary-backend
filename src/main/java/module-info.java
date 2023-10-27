module com.example.temporary {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.temporary to javafx.fxml;
    exports com.example.temporary;
}