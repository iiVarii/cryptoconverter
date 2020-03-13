package game.menus.scoreboard;

import game.menus.Options;
import game.menus.background.BackgroundManager;
import game.menus.customnodes.ImageButton;
import game.menus.customnodes.menuitem.MenuItem;
import game.menus.customnodes.texts.HeaderText;
import game.properties.ReadPropertyFile;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ScoreBoard {
    private static ScoreDatabase scoreDatabase = new ScoreDatabase();
    private static GridPane scorePane = scoreDatabase.getGrid();

    private static Parent createContent(int width, int height) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(width, height);
        borderPane.setBackground(BackgroundManager.getBackground("menu"));

        HeaderText optionsText = new HeaderText("SCOREBOARD");

        scorePane.setStyle(Options.getShadeStyleInvertString());
        scorePane.setHgap(25);

        FlowPane gridContainer = new FlowPane(Orientation.HORIZONTAL);
        gridContainer.setHgap(25);
        gridContainer.setAlignment(Pos.CENTER);

        gridContainer.getChildren().add(scorePane);

        ImageButton backArrowButton = new ImageButton("/buttonimages/back.png");
        backArrowButton.setOnAction(actionEvent -> Options.setScene("mainMenu"));
        backArrowButton.setAlignment(Pos.BASELINE_LEFT);

        MenuItem backArrowContainer = new MenuItem(backArrowButton);
        backArrowContainer.setAlignment(Pos.BASELINE_LEFT);
        backArrowContainer.setOnActivate(() -> Options.setScene("mainMenu"));
        backArrowContainer.setBackground(
                new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        backArrowContainer.setStyle(
                "-fx-background-color: rgba(200, 200, 200, 0.7); "
                        + "-fx-effect: dropshadow(gaussian, blue, 1000, 0, 0, 0);"
                        + "-fx-background-radius: 30; -fx-background-insets: -10");

        AnchorPane.setLeftAnchor(backArrowContainer, 20.0);

        AnchorPane footerPane = new AnchorPane();
        footerPane.getChildren().addAll(backArrowContainer);

        borderPane.setTop(optionsText);
        borderPane.setCenter(gridContainer);
        borderPane.setBottom(footerPane);

        BorderPane.setAlignment(optionsText, Pos.CENTER);
        BorderPane.setAlignment(scorePane, Pos.CENTER);

        return borderPane;
    }

    public static void writeScore(String name, double value) {
        scoreDatabase.writeScore(name, value);
        reloadScorePane();
    }

    private static void reloadScorePane() {
        scorePane = scoreDatabase.getGrid();
    }

    public static Scene createScoreBoard() {
        return new Scene(
                createContent(
                        ReadPropertyFile.getIntValue("width"), ReadPropertyFile.getIntValue("height")));
    }
}
