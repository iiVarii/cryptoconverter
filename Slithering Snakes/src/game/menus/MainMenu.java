package game.menus;

import game.menus.background.BackgroundManager;
import game.menus.customnodes.menuitem.MainMenuItem;
import game.menus.customnodes.menuitem.MenuItem;
import game.properties.ReadPropertyFile;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;

public class MainMenu {

    private static final String LOGO_PATH = "/images/menu_logo.png";

    private static VBox menuBox;
    private static int itemIndex = 0;

    private static Parent createContent(int width, int height) {

        StackPane mainStackPane = new StackPane();
        mainStackPane.setPrefSize(width, height);

        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(width, height);

        MediaView backgroundVideoView = BackgroundManager.getMediaView("mainMenu");

        Image logoImage = new Image(MainMenu.class.getResourceAsStream(LOGO_PATH));
        ImageView logoView = new ImageView(logoImage);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(width / 2.5d);

        MainMenuItem start = new MainMenuItem("START", "game", "clickSound");
        MainMenuItem scoreBoard = new MainMenuItem("SCOREBOARD", "scoreBoard", "clickSound");
        MainMenuItem options = new MainMenuItem("OPTIONS", "options", "clickSound");
        MainMenuItem credits = new MainMenuItem("CREDITS", "credits", "clickSound");
        MainMenuItem itemExit = new MainMenuItem("QUIT", "exit", "clickSound");

        menuBox = new VBox(15, start, scoreBoard, options, credits, itemExit);
        for (int i = 0; i < menuBox.getChildren().size(); i++) {
            MenuItem item = getMenuItem(i);
            item.setFocusTraversable(true);
            item.requestFocus();
            item.addEventFilter(
                    MouseEvent.MOUSE_CLICKED, mouseEvent -> itemIndex = menuBox.getChildren().indexOf(item));
            item.addEventFilter(
                    MouseEvent.MOUSE_ENTERED, mouseEvent -> itemIndex = menuBox.getChildren().indexOf(item));
        }

        menuBox.setStyle(
                "-fx-background-color: rgba(200, 200, 200, 0.7);"
                        + "-fx-effect: dropshadow(gaussian, blue, 1000, 0, 0, 0);"
                        + "-fx-background-insets: -25;"
                        + "-fx-background-radius: 30");

        FlowPane menuContainer = new FlowPane(Orientation.HORIZONTAL);
        menuContainer.getChildren().add(menuBox);
        menuContainer.setAlignment(Pos.CENTER);

        FlowPane logoContainer = new FlowPane(logoView);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(20));

        borderPane.setTop(logoContainer);
        BorderPane.setAlignment(logoContainer, Pos.CENTER);

        borderPane.setCenter(menuContainer);
        BorderPane.setAlignment(menuBox, Pos.CENTER);

        mainStackPane.getChildren().addAll(backgroundVideoView, borderPane);

        return mainStackPane;
    }

    private static MenuItem getMenuItem(int index) {
        return (MenuItem) menuBox.getChildren().get(index);
    }

    public static void deactivateMenuItems() {
        for (int i = 0; i < menuBox.getChildren().size(); i++) {
            getMenuItem(i).setActive(false);
        }
    }

    static Scene createMainMenu() {
        Scene mainMenu =
                new Scene(
                        createContent(
                                ReadPropertyFile.getIntValue("width"), ReadPropertyFile.getIntValue("height")));

        mainMenu.setOnKeyPressed(
                event -> {
                    if (event.getCode() == KeyCode.valueOf(ReadPropertyFile.getStringValue("upkey"))
                            || event.getCode() == KeyCode.UP) {
                        if (itemIndex > 0) {
                            deactivateMenuItems();
                            getMenuItem(--itemIndex).setActive(true);
                        } else {
                            deactivateMenuItems();
                            getMenuItem(menuBox.getChildren().size() - 1).setActive(true);
                            itemIndex = menuBox.getChildren().size() - 1;
                        }
                    } else if (event.getCode() == KeyCode.valueOf(ReadPropertyFile.getStringValue("downkey"))
                            || event.getCode() == KeyCode.DOWN) {
                        if (itemIndex < menuBox.getChildren().size() - 1) {
                            deactivateMenuItems();
                            getMenuItem(++itemIndex).setActive(true);
                        } else {
                            deactivateMenuItems();
                            getMenuItem(0).setActive(true);
                            itemIndex = 0;
                        }
                    } else if (event.getCode() == KeyCode.ENTER) {
                        getMenuItem(itemIndex).activate();
                    }
                });
        return mainMenu;
    }
}
