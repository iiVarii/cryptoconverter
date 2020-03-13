package game.menus.customnodes.labels;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class StyledLabel extends Label {
    private static final Font ITALIC_BOLD = Font.font("", FontWeight.BOLD, FontPosture.ITALIC, 18);
    private static final Font MAIN_BOLD = Font.font("", FontWeight.BOLD, 18);

    public StyledLabel() {
        super();
    }

    public StyledLabel(String s) {
        super(s);
    }

    public StyledLabel(String s, Node node) {
        super(s, node);
    }

    public static StyledLabel italicBoldLawnGreen(String string) {
        StyledLabel output = new StyledLabel(string);
        output.setFont(ITALIC_BOLD);
        output.setTextFill(Color.LAWNGREEN);
        output.setEffect(new GaussianBlur(2));
        return output;
    }

    public static StyledLabel italicBoldLimeGreen(String string) {
        StyledLabel output = new StyledLabel(string);
        output.setFont(ITALIC_BOLD);
        output.setTextFill(Color.LIME);
        output.setEffect(new GaussianBlur(2));
        return output;
    }

    public static StyledLabel justifiedBoldChartreuse(String string) {
        StyledLabel output = new StyledLabel(string);
        output.setTextAlignment(TextAlignment.JUSTIFY);
        output.setFont(MAIN_BOLD);
        output.setTextFill(Color.CHARTREUSE);
        output.setEffect(new GaussianBlur(2));
        return output;
    }
}
