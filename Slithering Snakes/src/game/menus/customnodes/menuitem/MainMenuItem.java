package game.menus.customnodes.menuitem;

import game.menus.MenuManager;
import game.menus.music.MusicManager;
import javafx.scene.Node;

public class MainMenuItem extends MenuItem {
    public MainMenuItem(String name, String returnScene, String soundEffectName) {
        super(name);
        addListeners(returnScene, soundEffectName);
    }

    public MainMenuItem(Node node, String returnScene, String soundEffectName) {
        super(node);
        addListeners(returnScene, soundEffectName);
    }

    private void addListeners(String returnScene, String soundEffectName) {
        setOnActivate(
                () -> {
                    MusicManager.getAudio(soundEffectName).play();
                    MenuManager.setCurrentScene(returnScene);
                });
        setOnMouseClicked(
                mouseEvent -> {
                    MusicManager.getAudio(soundEffectName).play();
                    MenuManager.setCurrentScene(returnScene);
                });
    }
}
