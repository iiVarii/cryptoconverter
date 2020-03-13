package game.menus.customnodes.texts;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class HeaderText extends Text {
    private static final Font MAIN_BOLD = Font.font("", FontWeight.BOLD, 26);

    public HeaderText(String s) {
        super(s);
        setTextAlignment(TextAlignment.JUSTIFY);
        setFont(MAIN_BOLD);
        setFill(Color.BLUEVIOLET);
        GaussianBlur visualEffect = new GaussianBlur(2);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(4);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        visualEffect.setInput(dropShadow);
        setEffect(visualEffect);
    }
}
