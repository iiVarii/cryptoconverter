package game.menus.background;

import game.menus.MenuManager;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.util.HashMap;
import java.util.Map;

public class BackgroundManager {
    private static final String menuBackgroundImagePath = "/backgrounds/menuBackground.jpg";
    private static final String gameBackgroundPicturePath = "/backgrounds/gameBackground.png";

    private static final String menuBackgroundVideoPath = "/backgrounds/menuBackground.mp4";

    private static Map<String, MediaPlayer> backgroundPlayerMap = new HashMap<>();
    private static Map<String, MediaView> backgroundViewMap = new HashMap<>();

    private static Map<String, Background> backgroundMap = new HashMap<>();

    public BackgroundManager(int width, int height) {
        createBackgroundFromVideo(menuBackgroundVideoPath, "mainMenu");
        createMediaView(getMediaPlayer("mainMenu"), "mainMenu");

        createBackgroundImage(width, height, menuBackgroundImagePath, "menu");
        createBackgroundImage(width, height, gameBackgroundPicturePath, "game");
    }

    public static void createBackgroundImage(int width, int height, String path, String name) {
        String backgroundURIString = BackgroundManager.class.getResource(path).toString();
        Image backgroundImage = new Image(backgroundURIString);
        BackgroundSize bSize = new BackgroundSize(width, height, false, false, false, true);
        Background background =
                new Background(
                        new BackgroundImage(
                                backgroundImage,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                bSize));
        backgroundMap.put(name, background);
    }

    public static void createBackgroundFromVideo(String path, String name) {
        String videoURIString = BackgroundManager.class.getResource(path).toString();
        Media backgroundVideo = new Media(videoURIString);

        MediaPlayer mediaPlayer = new MediaPlayer(backgroundVideo);
        mediaPlayer.setMute(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        backgroundPlayerMap.put(name, mediaPlayer);
    }

    public static void createMediaView(MediaPlayer mediaPlayer, String name) {
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.fitWidthProperty().bind(MenuManager.getMyStage().widthProperty());
        mediaView.fitHeightProperty().bind(MenuManager.getMyStage().heightProperty());
        mediaView.setPreserveRatio(false);
        backgroundViewMap.put(name, mediaView);
    }

    public static MediaPlayer getMediaPlayer(String name) {
        return backgroundPlayerMap.get(name);
    }

    public static MediaView getMediaView(String name) {
        return backgroundViewMap.get(name);
    }

    public static Background getBackground(String name) {
        return backgroundMap.get(name);
    }

    public static void switchBackgrounds(String newScene) {
        switch (newScene) {
            case "game":
                getMediaPlayer("mainMenu").stop();
                break;
            case "mainMenu":
                getMediaPlayer("mainMenu").play();
        }
    }
}
