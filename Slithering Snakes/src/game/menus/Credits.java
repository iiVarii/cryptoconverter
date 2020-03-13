package game.menus;

import game.menus.background.BackgroundManager;
import game.menus.customnodes.ImageButton;
import game.menus.customnodes.menuitem.MenuItem;
import game.menus.customnodes.texts.HeaderText;
import game.properties.ReadPropertyFile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Credits {

    private static final Font creditsFont = Font.font("", FontWeight.BOLD, FontPosture.ITALIC, 20);

    private static Parent createContent(int width, int height) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(width, height);
        borderPane.setBackground(BackgroundManager.getBackground("menu"));

        HeaderText headerText = new HeaderText("CREDITS");

        VBox textContainer = new VBox();

        Text creditsText = new Text("Created by:");
        creditsText.setFont(creditsFont);
        creditsText.setFill(Color.BLUEVIOLET);

        Text names = new Text("Timo Loomets\nMikk Merimaa");
        names.setFont(creditsFont);
        names.setFill(Color.LAWNGREEN);

        textContainer.setStyle(
                "-fx-background-color: rgba(200, 200, 200, 0.7); "
                        + "-fx-effect: dropshadow(gaussian, blue, 1000, 0, 0, 0);"
                        + "-fx-background-radius: 30; -fx-background-insets: -75;");
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(creditsText, names);

        FlowPane textPane = new FlowPane();
        textPane.setAlignment(Pos.CENTER);
        textPane.getChildren().add(textContainer);

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

        AnchorPane footerPane = new AnchorPane();
        footerPane.getChildren().addAll(backArrowContainer);
        AnchorPane.setLeftAnchor(backArrowContainer, 20.0);

        borderPane.setTop(headerText);
        borderPane.setCenter(textPane);
        borderPane.setBottom(footerPane);

        BorderPane.setAlignment(headerText, Pos.CENTER);
        BorderPane.setAlignment(textPane, Pos.CENTER);

        return borderPane;
    }

    public static Scene createCredits() {
        return new Scene(
                createContent(
                        ReadPropertyFile.getIntValue("width"), ReadPropertyFile.getIntValue("height")));
    }
}
