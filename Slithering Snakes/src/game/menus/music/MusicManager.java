package game.menus.music;

import game.properties.ReadPropertyFile;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private static final String menuMusicPath = "/music/menuBackground.mp3";
    private static final String gameMusicPath = "/music/gameBackground.mp3";
    private static final String clickSoundPath = "/music/soundeffects/clickSound.wav";
    private static final String eatSoundPath = "/music/soundeffects/eatSound.wav";
    private static Map<String, MediaPlayer> musicMap = new HashMap<>();
    private static Map<String, AudioClip> audioMap = new HashMap<>();

    public MusicManager() {
        createMusic(menuMusicPath, "menuMusic");
        createMusic(gameMusicPath, "gameMusic");

        createSoundEffect(clickSoundPath, "clickSound");
        createSoundEffect(eatSoundPath, "eatSound");
    }

    private static void createMusic(String path, String storedName) {
        Media musicMedia = new Media(MusicManager.class.getResource(path).toString());
        MediaPlayer musicPlayer = new MediaPlayer(musicMedia);

        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.setVolume(ReadPropertyFile.getDoubleValue("volume"));
        musicPlayer.setMute(ReadPropertyFile.getBoolValue("ismuted"));

        musicMap.put(storedName, musicPlayer);
    }

    private static void createSoundEffect(String path, String storedName) {
        AudioClip audioClip = new AudioClip(MusicManager.class.getResource(path).toString());
        audioClip.setVolume(ReadPropertyFile.getDoubleValue("volume"));
        audioMap.put(storedName, audioClip);
    }

    public static void playEffect(String effectName) {
        if (!ReadPropertyFile.getBoolValue("ismuted")) {
            getAudio(effectName).play();
        }
    }

    public static void toggleMute() {
        boolean bool = ReadPropertyFile.getBoolValue("ismuted");
        musicMap.forEach((key, value) -> value.setMute(!bool));
        ReadPropertyFile.setBoolValue("ismuted", !bool);
    }

    public static Map<String, MediaPlayer> getMusicMap() {
        return musicMap;
    }

    public static Map<String, AudioClip> getAudioMap() {
        return audioMap;
    }

    public static void switchMusic(String sceneName) {
        switch (sceneName) {
            case "game":
                musicMap.get("menuMusic").stop();
                musicMap.get("gameMusic").play();
                break;
            case "mainMenu":
                musicMap.get("gameMusic").stop();
                musicMap.get("menuMusic").play();
        }
    }

    public static MediaPlayer getMusic(String musicName) {
        return musicMap.get(musicName);
    }

    public static AudioClip getAudio(String audioName) {
        return audioMap.get(audioName);
    }
}
