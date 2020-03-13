package game.menus.customnodes.labels;

import game.menus.customnodes.KeyPopup;
import game.properties.ReadPropertyFile;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class KeyLabel extends Label {
    boolean isActive = false;
    private String propertyPath;
    private KeyPopup keyPopup;

    public KeyLabel(String text, String propertyPath, KeyPopup keyPopup) {
        super(text);
        this.propertyPath = propertyPath;
        this.keyPopup = keyPopup;
        setStyles();
        setTextFill(Color.BLUEVIOLET);
        createFunctionality();
    }

    private void setStyles() {
        setFont(Font.font("", FontWeight.BOLD, FontPosture.ITALIC, 18));
        setEffect(new GaussianBlur(2));
        setTextFill(Color.LAWNGREEN);
    }

    public void setPropertyValue(String valueString) {
        ReadPropertyFile.setStringValue(propertyPath, valueString);
    }

    public void createFunctionality() {
        addEventFilter(
                MouseEvent.ANY,
                event -> {
                    event.consume();
                    if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
                        keyPopup.setVisible(true);
                        setActive(true);
                    }
                });
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
