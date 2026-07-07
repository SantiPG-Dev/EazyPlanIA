module com.eazyplan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jakarta.persistence;

    opens com.eazyplan to javafx.fxml;
    opens com.eazyplan.presentation.controllers to javafx.fxml;
    opens com.eazyplan.domain to javafx.fxml, eclipselink;
    opens com.eazyplan.domain.entities to eclipselink;
    opens com.eazyplan.domain.repositories to eclipselink;

    exports com.eazyplan;
}
