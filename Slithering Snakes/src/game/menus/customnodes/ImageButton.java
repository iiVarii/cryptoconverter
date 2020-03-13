package game.menus.customnodes;

import game.menus.music.MusicManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageButton extends Button {

    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 6 4 4 6;";

    public ImageButton(String imagePath) {
        setGraphic(new ImageView(new Image(getClass().getResourceAsStream(imagePath))));
        setNormal();

        setOnMousePressed(mouseEvent -> setPressed());
        setOnMouseReleased(mouseEvent -> setNormal());

        addEventHandler(ActionEvent.ACTION, event -> MusicManager.playEffect("clickSound"));
    }

    public void setNormal() {
        setStyle(STYLE_NORMAL);
    }

    public void setPressed() {
        setStyle(STYLE_PRESSED);
    }
}
