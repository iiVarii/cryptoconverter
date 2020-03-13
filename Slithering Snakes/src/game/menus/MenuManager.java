package game.menus;

import game.controller.Controller;
import game.controller.visual.Visual;
import game.menus.background.BackgroundManager;
import game.menus.music.MusicManager;
import game.menus.scoreboard.ScoreBoard;
import game.properties.ReadPropertyFile;
import game.sprites.SpriteManager;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {
    private static Map<String, Scene> myScenes = new HashMap<>();
    private static String myCurrentScene;

    private static Stage myStage;

    private static MusicManager musicManager;
    private static BackgroundManager backgroundManager;
    private static SpriteManager spriteManager;

    // Don't delete this line! It calls ReadPropertyFile to initialize creation of file input and
    // output streams before other initialization.
    private static ReadPropertyFile properties = new ReadPropertyFile();

    public MenuManager(Stage stage, int width, int height) {
        myStage = stage;
        setResolution(width, height);
        ReadPropertyFile.setIntValue("width", width);
        ReadPropertyFile.setIntValue("height", height);

        musicManager = new MusicManager();
        backgroundManager = new BackgroundManager(width, height);
        spriteManager = new SpriteManager();

        addScene("mainMenu", MainMenu.createMainMenu());

        addScene("options", Options.createOptions());

        addScene("scoreBoard", ScoreBoard.createScoreBoard());

        addScene("credits", Credits.createCredits());

        addScene("game", Visual.getScene());

        setResolutionListeners(new ArrayList<>(myScenes.values()));
        setGlobalKeyBinds();
    }

    private static Scene getCurrentScene() {
        return myScenes.get(myCurrentScene);
    }

    public static void setCurrentScene(String sceneName) {
        switch (sceneName) {
            case "exit":
                System.exit(0);
                break;
            case "scoreBoard":
                refreshScene("scoreBoard", ScoreBoard.createScoreBoard());
                break;
            case "game":
                Visual.resetGame();
                Controller.startClock();
                break;
            case "mainMenu":
                Controller.toggleClock(false);
                break;
        }

        Visual.toggleEscapeMenu(false);
        Visual.hideDeathMenu();
        BackgroundManager.switchBackgrounds(sceneName);
        MusicManager.switchMusic(sceneName);
        myCurrentScene = sceneName;
        update();
    }

    private static void refreshScene(String sceneName, Scene scene) {
        myScenes.remove(sceneName);
        myScenes.put(sceneName, scene);
    }

    private static void setResolutionListeners(List<Scene> sceneList) {
        for (Scene scene : sceneList) {
            ChangeListener<Number> resizeListener =
                    (observable, oldValue, newValue) -> {
                        int newWidth = (int) scene.getWindow().getWidth();
                        int newHeight = (int) scene.getWindow().getHeight();

                        ReadPropertyFile.setIntValue("width", newWidth);
                        ReadPropertyFile.setIntValue("height", newHeight);

                        Visual.calculateCoordinates(newWidth, newHeight);
                        Visual.displayTiles(Controller.getTiles());
                        Visual.resetIndex();
                        Visual.displaySnakes(Controller.getSnakes());
                        Visual.displayFoods(Controller.getFoods());
                        Visual.displayIndex();
                    };

            scene.widthProperty().addListener(resizeListener);
            scene.heightProperty().addListener(resizeListener);
        }
    }

    private static void setGlobalKeyBinds() {
        myScenes.forEach(
                (key, value) ->
                        value.addEventHandler(
                                KeyEvent.KEY_PRESSED,
                                keyEvent -> {
                                    if ((keyEvent
                                            .getCode()
                                            .equals(KeyCode.valueOf(ReadPropertyFile.getStringValue("audiomute")))
                                            || keyEvent.getCode().equals(KeyCode.M))
                                            && !Visual.getDeathMenu().isVisible()) {
                                        MusicManager.toggleMute();
                                    } else if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                                        switch (myCurrentScene) {
                                            case "mainMenu":
                                                System.exit(0);
                                            case "scoreBoard":
                                                setCurrentScene("mainMenu");
                                                break;
                                            case "options": {
                                                if (!Options.getPopupBox().isVisible()) {
                                                    setCurrentScene("mainMenu");
                                                }
                                                break;
                                            }
                                            case "credits": {
                                                setCurrentScene("mainMenu");
                                                break;
                                            }
                                            case "game": {
                                                if (!Visual.isDeathMenuShown()) {
                                                    Visual.toggleEscapeMenu();
                                                    Controller.toggleClock();
                                                }
                                            }
                                        }
                                    }
                                }));
    }

    static void setResolution(int width, int height) {
        myStage.setWidth(width);
        myStage.setHeight(height);
        update();
    }

    public static void update() {
        myStage.setScene(getCurrentScene());
        myStage.show();
    }

    public static Stage getMyStage() {
        return myStage;
    }

    private void addScene(String sceneName, Scene scene) {
        myScenes.put(sceneName, scene);
    }
}
