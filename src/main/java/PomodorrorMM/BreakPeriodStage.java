package PomodorrorMM;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by stephen on 8/24/2014.
 */
public class BreakPeriodStage extends Stage {

    Stage stage;
    StackPane layout;


    public BreakPeriodStage(final Screen screen, Double opacity, Label breakTimerLbl) {

        setStage(new Stage());

        layout = new StackPane();

        Rectangle2D bounds = screen.getBounds();

        /* put the countdown clock on primary monitor */
        if (screen.hashCode() == Screen.getPrimary().hashCode()) {
            breakTimerLbl.setId("breakTimerLbl");
            layout.getChildren().addAll(breakTimerLbl);
        }

        layout.setStyle("-fx-background-color: rgba(0, 0, 0," + opacity + ");");

        Scene scene = new Scene(layout,
                                bounds.getWidth(),
                                bounds.getHeight());

        scene.getStylesheets().add("main.css");

        scene.setFill(null);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.toFront();
    }



    public Stage getStage() {return this.stage;}
    public void setStage(Stage stage) {this.stage = stage;}


    public StackPane getLayout() { return layout; }
    public void setLayout(StackPane layout) { this.layout = layout; }
}