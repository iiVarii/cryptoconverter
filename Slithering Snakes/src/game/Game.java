package game;

import game.menus.MenuManager;
import game.properties.ReadPropertyFile;
import javafx.application.Application;
import javafx.stage.Stage;

public class Game extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Slithering Snakes");

        MenuManager menuManager =
                new MenuManager(
                        primaryStage,
                        ReadPropertyFile.getIntValue("width"),
                        ReadPropertyFile.getIntValue("height"));

        MenuManager.setCurrentScene("mainMenu");

        MenuManager.update();
    }
}
