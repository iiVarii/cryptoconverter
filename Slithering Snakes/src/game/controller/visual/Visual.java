package game.controller.visual;

import game.controller.Controller;
import game.controller.food.Food;
import game.controller.snake.Snake;
import game.controller.tile.Tile;
import game.menus.MenuManager;
import game.menus.background.BackgroundManager;
import game.menus.customnodes.menuitem.MainMenuItem;
import game.menus.customnodes.menuitem.MenuItem;
import game.menus.customnodes.texts.HeaderText;
import game.menus.music.MusicManager;
import game.properties.ReadPropertyFile;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

public class Visual {
    private static final int PIXELS_BETWEEN_TILES = -1;
    private static Scene myScene;
    private static int myWidth;
    private static int myHeight;
    private static int X_0;
    private static int Y_0;
    private static int topPadding = 75;

    private static Pane tilePane;
    private static StackPane escapeMenu;
    private static VBox escapeMenuBox;
    private static int escapeMenuIndex = 0;

    private static StackPane deathMenu;

    private static VBox deathMenuScoreContainer;

    private static VBox deathMenuBox;
    private static HeaderText scoreText;
    private static int deathMenuIndex = 0;
    private static ScoreLabel scoreLabel;
    private static HeaderText scoreInfoLabel = new HeaderText("SCORE: " + ScoreLabel.getScore());

    private static void Visualizer(int width, int height) {
        myWidth = width;
        myHeight = height;
        calculateCoordinates(width, height);

        myScene = new Scene(createContent());
    }

    public static void calculateCoordinates(int width, int height) {
        topPadding = (int) (height * 0.1);
        Y_0 =
                (int)
                        round(
                                -(sin(atan(0.5) - atan(2d * topPadding / width))
                                        * sqrt(pow(width / 2d, 2) + pow(topPadding, 2)))
                                        / sin(PI - 2 * atan(0.5)));
        X_0 =
                (int)
                        round(
                                (sin(atan(0.5) + atan(2d * topPadding / width))
                                        * sqrt(pow(width / 2d, 2) + pow(topPadding, 2)))
                                        / sin(PI - 2 * atan(0.5)));
    }

    private static void Visualizer() {
        Visualizer(ReadPropertyFile.getIntValue("width"), ReadPropertyFile.getIntValue("height"));
    }

    private static void setup() {
        Visualizer();
        resetGame();
    }

    public static void resetGame() {
        Controller.bindKeys(myScene);
        Controller.setup();
        displayTiles(Controller.getTiles());
        Visual.resetIndex();
        Visual.displaySnakes(Controller.getSnakes());
        Visual.displayFoods(Controller.getFoods());
        Visual.displayIndex();
        scoreLabel.setScore(0);
    }

    private static Parent createContent() {
        StackPane root = new StackPane();
        root.setPrefSize(myWidth, myHeight);

        tilePane = new Pane();
        tilePane.setPrefSize(myWidth, myHeight);

        scoreLabel = new ScoreLabel();

        root.setBackground(BackgroundManager.getBackground("game"));

        MenuItem resume = new MenuItem("RESUME");
        resume.setOnActivate(
                () -> {
                    toggleEscapeMenu();
                    Controller.toggleClock();
                    MusicManager.getAudio("clickSound").play();
                });
        resume.setOnMouseClicked(
                mouseEvent -> {
                    toggleEscapeMenu();
                    Controller.toggleClock();
                    MusicManager.getAudio("clickSound").play();
                });

        escapeMenuBox = new VBox(15, resume, new MainMenuItem("MAIN MENU", "mainMenu", "clickSound"));

        for (int i = 0; i < escapeMenuBox.getChildren().size(); i++) {
            MenuItem item = getEscapeMenuItem(i);
            item.setFocusTraversable(true);
            item.requestFocus();
            item.addEventFilter(
                    MouseEvent.MOUSE_CLICKED,
                    mouseEvent -> escapeMenuIndex = escapeMenuBox.getChildren().indexOf(item));
            item.addEventFilter(
                    MouseEvent.MOUSE_ENTERED,
                    mouseEvent -> escapeMenuIndex = escapeMenuBox.getChildren().indexOf(item));
        }

        escapeMenu = new StackPane();
        escapeMenu.getChildren().add(escapeMenuBox);

        HeaderText headerText = new HeaderText("ENTER NAME:");

        scoreText = new HeaderText("");
        scoreText.setFill(Color.web("#83FDFF"));

        HBox deathMenuScoreBox = new HBox(20);
        deathMenuScoreBox.getChildren().addAll(headerText, scoreText);
        deathMenuScoreBox.setAlignment(Pos.CENTER);

        MenuItem newGame = new MenuItem("NEW GAME");
        newGame.setOnActivate(
                () -> {
                    MusicManager.getAudio("clickSound").play();
                    MenuManager.setCurrentScene("game");
                });
        newGame.setOnMouseClicked(
                mouseEvent -> {
                    MusicManager.getAudio("clickSound").play();
                    MenuManager.setCurrentScene("game");
                });

        deathMenuBox = new VBox(15, newGame, new MainMenuItem("MAIN MENU", "mainMenu", "clickSound"));

        for (int i = 0; i < deathMenuBox.getChildren().size(); i++) {
            MenuItem item = getDeathMenuItem(i);
            item.setFocusTraversable(true);
            item.requestFocus();
            item.addEventFilter(
                    MouseEvent.MOUSE_CLICKED,
                    mouseEvent -> deathMenuIndex = deathMenuBox.getChildren().indexOf(item));
            item.addEventFilter(
                    MouseEvent.MOUSE_ENTERED,
                    mouseEvent -> deathMenuIndex = deathMenuBox.getChildren().indexOf(item));
        }

        scoreInfoLabel.setFill(Color.WHITESMOKE);

        deathMenuScoreContainer = new VBox(15, scoreInfoLabel, deathMenuScoreBox);

        List<Pane> toStyleAndAlign =
                Arrays.asList(escapeMenuBox, deathMenuScoreContainer, deathMenuBox);
        toStyleAndAlign.forEach(
                pane -> {
                    pane.setStyle(
                            "-fx-background-color: rgba(200, 200, 200, 0.7);"
                                    + "-fx-effect: dropshadow(gaussian, blue, 1000, 0, 0, 0);"
                                    + "-fx-background-insets: -25;"
                                    + "-fx-background-radius: 30");
                    if (pane instanceof VBox) {
                        ((VBox) pane).setAlignment(Pos.CENTER);
                    } else if (pane instanceof HBox) {
                        ((HBox) pane).setAlignment(Pos.CENTER);
                    }
                });

        deathMenu = new StackPane(deathMenuScoreContainer, deathMenuBox);

        deathMenuScoreContainer.setVisible(false);
        deathMenuBox.setVisible(false);

        deathMenu.setVisible(false);

        BorderPane mainBorder = new BorderPane();
        mainBorder.setTop(scoreLabel);
        BorderPane.setAlignment(scoreLabel, Pos.CENTER_RIGHT);

        StackPane mainStack = new StackPane();
        mainStack.getChildren().addAll(mainBorder, escapeMenu, deathMenu);

        setEscapeBoxListeners();
        toggleEscapeMenu();

        root.getChildren().addAll(tilePane, mainStack);
        return root;
    }

    public static HeaderText getScoreText() {
        return scoreText;
    }

    public static VBox getDeathMenuBox() {
        return deathMenuBox;
    }

    public static int getDeathMenuIndex() {
        return deathMenuIndex;
    }

    public static void setDeathMenuIndex(int deathMenuIndex) {
        Visual.deathMenuIndex = deathMenuIndex;
    }

    public static void setEscapeBoxListeners() {
        escapeMenu.setOnKeyPressed(
                keyEvent -> {
                    if (isEscapeMenuShown()) {
                        if (keyEvent.getCode() == KeyCode.valueOf(ReadPropertyFile.getStringValue("upkey"))
                                || keyEvent.getCode().equals(KeyCode.UP)) {
                            if (escapeMenuIndex > 0) {
                                deactivateEscapeItems();
                                getEscapeMenuItem(--escapeMenuIndex).setActive(true);
                            } else {
                                deactivateEscapeItems();
                                getEscapeMenuItem(escapeMenuBox.getChildren().size() - 1).setActive(true);
                                escapeMenuIndex = escapeMenuBox.getChildren().size() - 1;
                            }
                        } else if (keyEvent.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("downkey"))
                                || keyEvent.getCode() == KeyCode.DOWN) {
                            if (escapeMenuIndex < escapeMenuBox.getChildren().size() - 1) {
                                deactivateEscapeItems();
                                getEscapeMenuItem(++escapeMenuIndex).setActive(true);
                            } else {
                                deactivateEscapeItems();
                                getEscapeMenuItem(0).setActive(true);
                                escapeMenuIndex = 0;
                            }
                        } else if (keyEvent.getCode() == KeyCode.ENTER) {
                            getEscapeMenuItem(escapeMenuIndex).activate();
                        }
                    }
                });
    }

    public static void deactivateEscapeItems() {
        for (int i = 0; i < escapeMenuBox.getChildren().size(); i++) {
            getEscapeMenuItem(i).setActive(false);
        }
    }

    public static void deactivateDeathItems() {
        for (int i = 0; i < deathMenuBox.getChildren().size(); i++) {
            getDeathMenuItem(i).setActive(false);
        }
    }

    private static MenuItem getEscapeMenuItem(int i) {
        return (MenuItem) escapeMenuBox.getChildren().get(i);
    }

    public static MenuItem getDeathMenuItem(int i) {
        return (MenuItem) deathMenuBox.getChildren().get(i);
    }

    public static void displayTiles(List<List<Tile>> tiles) {
        List<Group> tilesToDisplay = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            for (int j = 0; j < tiles.get(i).size(); j++) {
                tiles.get(i).get(j).setX(X_0 + (Tile.DEFAULT_SIZE + PIXELS_BETWEEN_TILES) * i);
                tiles.get(i).get(j).setY(Y_0 + (Tile.DEFAULT_SIZE + PIXELS_BETWEEN_TILES) * j);
                tilesToDisplay.add(tiles.get(i).get(j).getVisual());
            }
        }
        tilePane.getChildren().clear();
        tilePane.getChildren().addAll(tilesToDisplay);
    }

    public static void resetIndex() {
        tilePane.getChildren().removeAll(IndexManager.getShapes());
        IndexManager.clear();
    }

    public static void displaySnakes(List<Snake> snakes) {
        for (Snake snake : snakes) {
            snake.addSegmentsToIndex();
        }
    }

    public static void displayFoods(List<Food> foods) {
        for (Food food : foods) {
            food.addFoodToIndex();
        }
    }

    public static void displayIndex() {
        tilePane.getChildren().addAll(IndexManager.getShapes());
    }

    public static Scene getScene() {
        if (myScene == null) setup();
        return myScene;
    }

    public static void toggleEscapeMenu() {
        toggleEscapeMenu(!escapeMenu.isVisible());
    }

    public static void hideDeathMenu() {
        deathMenu.setVisible(false);
        deathMenuScoreContainer.setVisible(false);
        deathMenuBox.setVisible(false);
    }

    public static void toggleDeathMenu(boolean bool) {
        scoreInfoLabel.setText("SCORE: " + ScoreLabel.getScore());
        deathMenu.setVisible(bool);
        deathMenuScoreContainer.setVisible(bool);
    }

    public static void toggleEscapeMenu(boolean bool) {
        escapeMenu.setVisible(bool);
    }

    public static Boolean isEscapeMenuShown() {
        return escapeMenu.isVisible();
    }

    public static Boolean isDeathMenuShown() {
        return deathMenu.isVisible();
    }

    public static ScoreLabel getScoreLabel() {
        return scoreLabel;
    }

    public static StackPane getDeathMenu() {
        return deathMenu;
    }

    public static VBox getDeathMenuScoreContainer() {
        return deathMenuScoreContainer;
    }
}
