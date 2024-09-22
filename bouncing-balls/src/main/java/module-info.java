module edu.brown.bouncingballs {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens edu.brown.bouncingballs to javafx.fxml;
    exports edu.brown.bouncingballs;
}