package game.menus;

import game.controller.visual.Visual;
import game.menus.background.BackgroundManager;
import game.menus.customnodes.ImageButton;
import game.menus.customnodes.KeyPopup;
import game.menus.customnodes.labels.KeyLabel;
import game.menus.customnodes.labels.StyledLabel;
import game.menus.customnodes.menuitem.MenuItem;
import game.menus.customnodes.texts.HeaderText;
import game.menus.music.MusicManager;
import game.properties.ReadPropertyFile;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Options {

    private static final String shadeStyleString =
            "-fx-background-color: rgba(200, 200, 200, 0.7);"
                    + "-fx-effect: dropshadow(gaussian, blue, 1000, 0, 0, 0);"
                    + "-fx-background-insets: 50;"
                    + "-fx-background-radius: 30";
    private static final String shadeStyleInvertString =
            "-fx-background-color: rgba(200, 200, 200, 0.7);"
                    + "-fx-effect: dropshadow(gaussian, blue, 1000, 0, 0, 0);"
                    + "-fx-background-insets: -100;"
                    + "-fx-background-radius: 30";
    private static final List<Pair<Integer, Integer>> RESOLUTION_LIST =
            Arrays.asList(
                    new Pair<>(1280, 720),
                    new Pair<>(1366, 768),
                    new Pair<>(1440, 900),
                    new Pair<>(1536, 864),
                    new Pair<>(1600, 900),
                    new Pair<>(1920, 1080));

    private static int itemIndex = 0;
    private static int resolutionIndex =
            RESOLUTION_LIST.indexOf(
                    new Pair<>(
                            ReadPropertyFile.getIntValue("width"), ReadPropertyFile.getIntValue("height")));
    private static List<MenuItem> highlightItems;
    private static StyledLabel resolutionInfo;
    private static int newWidth = ReadPropertyFile.getIntValue("width");
    private static int newHeight = ReadPropertyFile.getIntValue("height");
    private static List<KeyCode> forbiddenKeyCodes = new ArrayList<>();

    private static KeyPopup popupBox = new KeyPopup("PRESS NEW KEY TO BIND");

    public static KeyPopup getPopupBox() {
        return popupBox;
    }

    private static Parent createContent(int width, int height) {
        StackPane stackPane = new StackPane();

        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(width, height);

        borderPane.setBackground(BackgroundManager.getBackground("menu"));

        HeaderText optionsText = new HeaderText("OPTIONS");

        StyledLabel difficultyLabel = StyledLabel.italicBoldLimeGreen("DIFFICULTY");

        Slider difficultySlider = new Slider();
        difficultySlider.setMin(1);
        difficultySlider.setMax(3);
        difficultySlider.setValue(ReadPropertyFile.getIntValue("difficulty"));
        difficultySlider.setShowTickLabels(true);
        difficultySlider.setShowTickMarks(true);
        difficultySlider.setMajorTickUnit(1);
        difficultySlider.setMinorTickCount(0);
        difficultySlider.setBlockIncrement(1);

        difficultySlider
                .valueProperty()
                .addListener(
                        (ov, old_val, new_val) -> {
                            ReadPropertyFile.setIntValue("difficulty", Math.round(new_val.floatValue()));
                            difficultySlider.setValue(Math.round(new_val.floatValue()));
                        });

        StyledLabel soundLabel = StyledLabel.italicBoldLimeGreen("SOUND");

        Slider soundSlider = new Slider(0, 1, ReadPropertyFile.getDoubleValue("volume"));
        soundSlider.setBlockIncrement(0.05);
        soundSlider.setShowTickLabels(true);
        soundSlider.setShowTickMarks(true);

        soundSlider
                .valueProperty()
                .addListener(
                        ((ov, old_val, new_val) -> {
                            ReadPropertyFile.setDoubleValue("volume", new_val.doubleValue());
                            MusicManager.getMusicMap()
                                    .forEach(
                                            (key, value) ->
                                                    value.volumeProperty().bindBidirectional(soundSlider.valueProperty()));
                            MusicManager.getAudioMap()
                                    .forEach(
                                            (key, value) ->
                                                    value.volumeProperty().bindBidirectional(soundSlider.valueProperty()));
                            MenuManager.update();
                        }));

        StyledLabel snakeCountLabel = StyledLabel.italicBoldLimeGreen("SNAKE COUNT");

        Slider snakeSlider = new Slider(1, 2, ReadPropertyFile.getIntValue("playercount"));
        snakeSlider.setBlockIncrement(1);
        snakeSlider.setShowTickLabels(true);
        snakeSlider.setShowTickMarks(true);

        snakeSlider
                .valueProperty()
                .addListener(
                        ((ov, old_val, new_val) -> {
                            ReadPropertyFile.setIntValue("playercount", Math.round(new_val.floatValue()));
                            snakeSlider.setValue(Math.round(new_val.intValue()));
                        }));

        StyledLabel resolutionLabel = StyledLabel.italicBoldLimeGreen("RESOLUTION");
        ImageButton leftArrowButton = new ImageButton("/buttonimages/left_arrow.png");
        leftArrowButton.setOnAction(
                actionEvent -> {
                    if (!(resolutionIndex - 1 < 0)) {
                        newWidth = RESOLUTION_LIST.get(resolutionIndex - 1).getKey();
                        newHeight = RESOLUTION_LIST.get(resolutionIndex - 1).getValue();
                        resolutionInfo.setText(newWidth + "X" + newHeight);
                        resolutionIndex -= 1;
                    } else {
                        newWidth = RESOLUTION_LIST.get(0).getKey();
                        newHeight = RESOLUTION_LIST.get(0).getValue();
                        resolutionInfo.setText(newWidth + "X" + newHeight);
                        resolutionIndex = 0;
                    }
                });
        ImageButton rightArrowButton = new ImageButton("/buttonimages/right_arrow.png");
        rightArrowButton.setOnAction(
                actionEvent -> {
                    if (!(resolutionIndex + 1 >= RESOLUTION_LIST.size())) {
                        newWidth = RESOLUTION_LIST.get(resolutionIndex + 1).getKey();
                        newHeight = RESOLUTION_LIST.get(resolutionIndex + 1).getValue();
                        resolutionInfo.setText(newWidth + "X" + newHeight);
                        resolutionIndex += 1;
                    } else {
                        newWidth = RESOLUTION_LIST.get(RESOLUTION_LIST.size() - 1).getKey();
                        newHeight = RESOLUTION_LIST.get(RESOLUTION_LIST.size() - 1).getValue();
                        resolutionInfo.setText(newWidth + "X" + newHeight);
                        resolutionIndex = RESOLUTION_LIST.size() - 1;
                    }
                });

        resolutionInfo =
                StyledLabel.justifiedBoldChartreuse(
                        ReadPropertyFile.getIntValue("width") + "X" + ReadPropertyFile.getIntValue("height"));

        HBox resolutionBox = new HBox(10);
        resolutionBox.setSpacing(10);
        resolutionBox.setAlignment(Pos.CENTER);
        resolutionBox.getChildren().addAll(leftArrowButton, resolutionInfo, rightArrowButton);

        MenuItem difficultyContainer = new MenuItem(difficultySlider);
        MenuItem soundContainer = new MenuItem(soundSlider);
        MenuItem countContainer = new MenuItem(snakeSlider);
        MenuItem resolutionContainer = new MenuItem(resolutionBox);

        List<MenuItem> sliderToStyle =
                Arrays.asList(difficultyContainer, soundContainer, countContainer);
        sliderToStyle.forEach(
                menuItem ->
                        menuItem
                                .getStylesheets()
                                .add(Options.class.getResource("/styles/slider.css").toExternalForm()));

        resolutionBox.addEventHandler(
                KeyEvent.KEY_PRESSED,
                keyEvent -> {
                    keyEvent.consume();
                    if (keyEvent.getCode() == KeyCode.LEFT
                            || keyEvent.getCode()
                            == KeyCode.valueOf(ReadPropertyFile.getStringValue("leftkey"))) {
                        leftArrowButton.fire();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT
                            || keyEvent.getCode()
                            == KeyCode.valueOf(ReadPropertyFile.getStringValue("rightkey"))) {
                        rightArrowButton.fire();
                    }
                });

        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER_LEFT);

        List<Node> leftNodes =
                Arrays.asList(difficultyLabel, soundLabel, snakeCountLabel, resolutionLabel);
        List<Node> rightNodes =
                Arrays.asList(difficultyContainer, soundContainer, countContainer, resolutionContainer);
        for (int i = 0; i < leftNodes.size(); i++) {
            mainGrid.add(leftNodes.get(i), 0, i);
            mainGrid.add(rightNodes.get(i), 1, i);
        }
        mainGrid.setHgap(20);
        mainGrid.setVgap(20);

        mainGrid
                .getChildren()
                .forEach(
                        node -> {
                            GridPane.setHalignment(node, HPos.CENTER);
                            GridPane.setValignment(node, VPos.CENTER);
                        });

        StyledLabel upKeyLabel = StyledLabel.italicBoldLimeGreen("UP");
        StyledLabel downKeyLabel = StyledLabel.italicBoldLimeGreen("DOWN");
        StyledLabel leftKeyLabel = StyledLabel.italicBoldLimeGreen("LEFT");
        StyledLabel rightKeyLabel = StyledLabel.italicBoldLimeGreen("RIGHT");
        StyledLabel muteAudioKeyLabel = StyledLabel.italicBoldLimeGreen("MUTE AUDIO");
        StyledLabel pauseKeyLabel = StyledLabel.italicBoldLimeGreen("PAUSE");

        StyledLabel secondPlayerUpKey = StyledLabel.italicBoldLawnGreen("\u2191");
        KeyLabel upKeyFieldSecondary =
                new KeyLabel(ReadPropertyFile.getStringValue("upkey"), "upkey", popupBox);

        StyledLabel secondPlayerDownKey = StyledLabel.italicBoldLawnGreen("\u2193");
        KeyLabel downKeyFieldSecondary =
                new KeyLabel(ReadPropertyFile.getStringValue("downkey"), "downkey", popupBox);

        StyledLabel secondPlayerLeftKey = StyledLabel.italicBoldLawnGreen("\u2190");
        KeyLabel leftKeyFieldSecondary =
                new KeyLabel(ReadPropertyFile.getStringValue("leftkey"), "leftkey", popupBox);

        StyledLabel secondPlayerRightKey = StyledLabel.italicBoldLawnGreen("\u2192");
        KeyLabel rightKeyFieldSecondary =
                new KeyLabel(ReadPropertyFile.getStringValue("rightkey"), "rightkey", popupBox);

        StyledLabel secondPlayerMuteKey = StyledLabel.italicBoldLawnGreen("M");
        KeyLabel muteAudioKeyFieldSecondary =
                new KeyLabel(ReadPropertyFile.getStringValue("audiomute"), "audiomute", popupBox);

        StyledLabel secondPlayerPauseKey = StyledLabel.italicBoldLawnGreen("P");
        KeyLabel pauseKeyFieldSecondary =
                new KeyLabel(ReadPropertyFile.getStringValue("pausegame"), "pausegame", popupBox);

        MenuItem firstPlayerUpKey = new MenuItem(upKeyFieldSecondary);
        MenuItem firstPlayerDownKey = new MenuItem(downKeyFieldSecondary);
        MenuItem firstPlayerLeftKey = new MenuItem(leftKeyFieldSecondary);
        MenuItem firstPlayerRightKey = new MenuItem(rightKeyFieldSecondary);
        MenuItem firstPlayerMuteKey = new MenuItem(muteAudioKeyFieldSecondary);
        MenuItem firstPlayerPauseKey = new MenuItem(pauseKeyFieldSecondary);

        GridPane keyBindBox = new GridPane();
        keyBindBox.setAlignment(Pos.CENTER_RIGHT);

        StyledLabel firstPlayerLabel = StyledLabel.italicBoldLimeGreen("FIRST SNAKE");
        StyledLabel secondPlayerLabel = StyledLabel.italicBoldLimeGreen("SECOND SNAKE");

        keyBindBox.add(firstPlayerLabel, 1, 0);
        keyBindBox.add(secondPlayerLabel, 2, 0);

        List<List<Object>> keyBindItems =
                Arrays.asList(
                        Arrays.asList(upKeyLabel, firstPlayerUpKey, secondPlayerUpKey),
                        Arrays.asList(downKeyLabel, firstPlayerDownKey, secondPlayerDownKey),
                        Arrays.asList(leftKeyLabel, firstPlayerLeftKey, secondPlayerLeftKey),
                        Arrays.asList(rightKeyLabel, firstPlayerRightKey, secondPlayerRightKey),
                        Arrays.asList(muteAudioKeyLabel, firstPlayerMuteKey, secondPlayerMuteKey),
                        Arrays.asList(pauseKeyLabel, firstPlayerPauseKey, secondPlayerPauseKey));

        for (int i = 0; i < keyBindItems.size(); i++) {
            for (int j = 0; j < keyBindItems.get(i).size(); j++) {
                keyBindBox.add((Node) keyBindItems.get(i).get(j), j, i + 1);
            }
        }

        keyBindBox.setHgap(40);
        keyBindBox.setVgap(10);
        keyBindBox
                .getChildren()
                .forEach(
                        node -> {
                            GridPane.setHalignment(node, HPos.CENTER);
                            GridPane.setValignment(node, VPos.CENTER);
                        });

        FlowPane gridContainer = new FlowPane(Orientation.HORIZONTAL);
        gridContainer.setAlignment(Pos.CENTER);
        gridContainer.setStyle(shadeStyleString);
        gridContainer.setHgap(25);

        gridContainer.getChildren().addAll(mainGrid, keyBindBox);

        AnchorPane footerPane = new AnchorPane();
        footerPane.setBackground(
                new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        footerPane.setStyle("-fx-background-color: rgba(200, 200, 200, 0.7);");

        ImageButton backArrowButton = new ImageButton("/buttonimages/back.png");
        backArrowButton.setOnAction(actionEvent -> setScene("mainMenu"));
        backArrowButton.setAlignment(Pos.BASELINE_LEFT);

        MenuItem backArrowContainer = new MenuItem(backArrowButton);
        backArrowContainer.setAlignment(Pos.BASELINE_LEFT);
        backArrowContainer.setOnActivate(() -> setScene("mainMenu"));
        setImageActionStyle(backArrowContainer);

        ImageButton saveButton = new ImageButton("/buttonimages/save.png");
        saveButton.setOnAction(actionEvent -> saveAction());
        saveButton.setAlignment(Pos.BASELINE_RIGHT);

        MenuItem saveButtonContainer = new MenuItem(saveButton);
        saveButtonContainer.setAlignment(Pos.BASELINE_RIGHT);
        saveButtonContainer.setOnActivate(Options::saveAction);
        setImageActionStyle(saveButtonContainer);

        AnchorPane.setLeftAnchor(backArrowContainer, 20.0);
        AnchorPane.setRightAnchor(saveButtonContainer, 20.0);

        footerPane.getChildren().addAll(backArrowContainer, saveButtonContainer);

        highlightItems =
                Arrays.asList(
                        backArrowContainer,
                        difficultyContainer,
                        soundContainer,
                        countContainer,
                        resolutionContainer,
                        firstPlayerUpKey,
                        firstPlayerDownKey,
                        firstPlayerLeftKey,
                        firstPlayerRightKey,
                        firstPlayerMuteKey,
                        firstPlayerPauseKey,
                        saveButtonContainer);

        for (MenuItem item : highlightItems) {
            item.setFocusTraversable(true);
            item.requestFocus();
            item.addEventFilter(
                    MouseEvent.MOUSE_CLICKED, mouseEvent -> itemIndex = highlightItems.indexOf(item));
            item.addEventFilter(
                    MouseEvent.MOUSE_ENTERED, mouseEvent -> itemIndex = highlightItems.indexOf(item));
        }

        borderPane.setTop(optionsText);
        BorderPane.setAlignment(optionsText, Pos.TOP_CENTER);

        borderPane.setCenter(gridContainer);
        BorderPane.setAlignment(gridContainer, Pos.CENTER);

        borderPane.setBottom(footerPane);
        BorderPane.setAlignment(footerPane, Pos.TOP_CENTER);

        stackPane.getChildren().addAll(borderPane, popupBox);
        popupBox.setVisible(false);

        return stackPane;
    }

    private static MenuItem getMenuItem(int index) {
        return highlightItems.get(index);
    }

    private static void updateForbiddenKeycodes() {
        forbiddenKeyCodes =
                Arrays.asList(
                        KeyCode.valueOf(ReadPropertyFile.getStringValue("upkey")),
                        KeyCode.valueOf(ReadPropertyFile.getStringValue("downkey")),
                        KeyCode.valueOf(ReadPropertyFile.getStringValue("leftkey")),
                        KeyCode.valueOf(ReadPropertyFile.getStringValue("rightkey")),
                        KeyCode.valueOf(ReadPropertyFile.getStringValue("audiomute")),
                        KeyCode.valueOf(ReadPropertyFile.getStringValue("pausegame")),
                        KeyCode.UP,
                        KeyCode.DOWN,
                        KeyCode.LEFT,
                        KeyCode.RIGHT,
                        KeyCode.ENTER,
                        KeyCode.BACK_SPACE,
                        KeyCode.SPACE,
                        KeyCode.ESCAPE);
    }

    private static void setImageActionStyle(MenuItem menuItem) {
        menuItem.addEventFilter(
                KeyEvent.KEY_PRESSED,
                event -> {
                    if (highlightItems.indexOf(menuItem) == itemIndex) {
                        ((ImageButton) menuItem.getChildren().get(1)).setPressed();
                    }
                });

        menuItem.addEventFilter(
                KeyEvent.KEY_RELEASED,
                event -> {
                    if (highlightItems.indexOf(menuItem) == itemIndex) {
                        ((ImageButton) menuItem.getChildren().get(1)).setNormal();
                    }
                });
    }

    public static String getShadeStyleString() {
        return shadeStyleString;
    }

    public static String getShadeStyleInvertString() {
        return shadeStyleInvertString;
    }

    public static void setScene(String path) {
        MenuManager.setCurrentScene(path);
        newWidth = ReadPropertyFile.getIntValue("width");
        newHeight = ReadPropertyFile.getIntValue("height");
        resolutionInfo.setText(newWidth + "X" + newHeight);
    }

    private static void saveAction() {
        ReadPropertyFile.setIntValue("width", newWidth);
        ReadPropertyFile.setIntValue("height", newHeight);
        MenuManager.setResolution(newWidth, newHeight);
        Visual.calculateCoordinates(newWidth, newHeight);
    }

    public static void deactivateMenuItems() {
        for (int i = 0; i < highlightItems.size(); i++) {
            getMenuItem(i).setActive(false);
        }
    }

    public static Scene createOptions() {
        Scene options =
                new Scene(
                        createContent(
                                ReadPropertyFile.getIntValue("width"), ReadPropertyFile.getIntValue("height")));
        options.setOnKeyPressed(
                event -> {
                    event.consume();
                    if (getMenuItem(itemIndex).getChildren().get(1) instanceof KeyLabel
                            && popupBox.isVisible()) {
                        KeyLabel keyLabel = (KeyLabel) getMenuItem(itemIndex).getChildren().get(1);
                        if (keyLabel.isActive()) {
                            updateForbiddenKeycodes();
                            event.consume();
                            if (!forbiddenKeyCodes.contains(event.getCode())) {
                                keyLabel.setText(event.getCode().toString());
                                keyLabel.setPropertyValue(event.getCode().toString());
                                popupBox.setVisible(false);
                                keyLabel.setActive(false);
                            } else {
                                popupBox.setVisible(false);
                                keyLabel.setActive(false);
                            }
                        }
                    } else if (getMenuItem(itemIndex).getChildren().get(1) instanceof KeyLabel
                            && event.getCode().equals(KeyCode.ENTER)) {
                        KeyLabel keyLabel = (KeyLabel) getMenuItem(itemIndex).getChildren().get(1);
                        keyLabel.setActive(true);
                        popupBox.setVisible(true);
                    } else if (!popupBox.isVisible()) {
                        if (event.getCode() == KeyCode.valueOf(ReadPropertyFile.getStringValue("upkey"))
                                || event.getCode() == KeyCode.UP) {
                            deactivateMenuItems();
                            if (itemIndex > 0) {
                                getMenuItem(--itemIndex).setActive(true);
                            } else {
                                getMenuItem(itemIndex).setActive(false);
                                getMenuItem(highlightItems.size() - 1).setActive(true);
                                itemIndex = highlightItems.size() - 1;
                            }
                        } else if (event.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("downkey"))
                                || event.getCode() == KeyCode.DOWN) {
                            deactivateMenuItems();
                            if (itemIndex < highlightItems.size() - 1) {
                                getMenuItem(++itemIndex).setActive(true);
                            } else {
                                getMenuItem(0).setActive(true);
                                itemIndex = 0;
                            }
                        } else if (event.getCode() == KeyCode.LEFT
                                || event.getCode() == KeyCode.valueOf(ReadPropertyFile.getStringValue("leftkey"))) {
                            if (getMenuItem(itemIndex).getChildren().get(1) instanceof Slider) {
                                ((Slider) getMenuItem(itemIndex).getChildren().get(1)).decrement();
                            } else if (getMenuItem(itemIndex).getChildren().get(1) instanceof HBox) {
                                getMenuItem(itemIndex).getChildren().get(1).fireEvent(event);
                            }
                        } else if (event.getCode() == KeyCode.RIGHT
                                || event.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("rightkey"))) {
                            if (getMenuItem(itemIndex).getChildren().get(1) instanceof Slider) {
                                ((Slider) getMenuItem(itemIndex).getChildren().get(1)).increment();
                            } else if (getMenuItem(itemIndex).getChildren().get(1) instanceof HBox) {
                                getMenuItem(itemIndex).getChildren().get(1).fireEvent(event);
                            }
                        } else if (event.getCode() == KeyCode.ENTER) {

                            getMenuItem(itemIndex).activate();
                        }
                    }
                });
        return options;
    }
}
