package game.menus.customnodes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static javafx.scene.paint.Color.CHARTREUSE;

public class KeyPopup extends StackPane {
    private static final Font mainFont = Font.font("", FontWeight.BOLD, 22);
    private static final int WIDTH = 450;
    private static final int HEIGHT = 125;
    private static final int ARC_DIMENSIONS = 100;

    public KeyPopup(String text) {
        super();

        Rectangle backgroundRectangle = new Rectangle();
        backgroundRectangle.setArcWidth(ARC_DIMENSIONS);
        backgroundRectangle.setArcHeight(ARC_DIMENSIONS);
        backgroundRectangle.setWidth(WIDTH);
        backgroundRectangle.setHeight(HEIGHT);

        Label popUpText = new Label(text);
        popUpText.setFont(mainFont);
        popUpText.setTextFill(CHARTREUSE);
        popUpText.setEffect(new GaussianBlur(2));
        getChildren().addAll(backgroundRectangle, popUpText);
        backgroundRectangle.setStyle(
                "-fx-fill: rgba(255, 255, 255, 0.4);"
                        + "-fx-effect: dropshadow(gaussian, black, 50, 0, 0, 0);"
                        + "-fx-background-insets: -50;"
                        + "-fx-background-radius: 45; "
                        + "-fx-arc-height: 100; "
                        + "-fx-arc-width: 100; ");

        setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.4);"
                        + "-fx-effect: dropshadow(gaussian, black, -50, 0, 0, 0);");

        popUpText.setAlignment(Pos.CENTER);
    }
}
