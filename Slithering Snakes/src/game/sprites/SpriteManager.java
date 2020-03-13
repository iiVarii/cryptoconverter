package game.sprites;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private static final Map<String, String> spriteNames =
            Map.of(
                    "/sprites/tileTop.png", "tileTopSprite",
                    "/sprites/tileLeft.png", "tileLeftSprite",
                    "/sprites/tileRight.png", "tileRightSprite",
                    "/sprites/snakeSkin.png", "snakeSkinSprite",
                    "/sprites/snakeSkinDark.png", "snakeSkinSpriteDark",
                    "/sprites/snakeSkinDarkest.png", "snakeSkinSpriteDarkest",
                    "/sprites/foodTop.png", "foodTopSprite",
                    "/sprites/foodLeft.png", "foodLeftSprite",
                    "/sprites/foodRight.png", "foodRightSprite");
    private static HashMap<String, ImagePattern> spriteMap;

    public SpriteManager() {
        spriteMap = new HashMap<>();
        spriteNames.forEach(SpriteManager::createSprite);
    }

    private static void createSprite(String spritePath, String spriteName) {
        Image patternImg = new Image(SpriteManager.class.getResourceAsStream(spritePath));
        ImagePattern pattern = new ImagePattern(patternImg);
        spriteMap.put(spriteName, pattern);
    }

    public static ImagePattern getPattern(String spriteName) {
        return spriteMap.get(spriteName);
    }
}
