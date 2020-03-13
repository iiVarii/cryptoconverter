package game.controller.visual;

import game.properties.ReadPropertyFile;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ScoreLabel extends Label {
    private static final Font MAIN_BOLD = Font.font("", FontWeight.BOLD, 26);
    private static int score;

    public ScoreLabel() {
        super("Score: 0");
        setTextAlignment(TextAlignment.JUSTIFY);
        setFont(MAIN_BOLD);
        setTextFill(Color.WHITESMOKE);
        GaussianBlur visualEffect = new GaussianBlur(2);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(4);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        visualEffect.setInput(dropShadow);
        setEffect(visualEffect);
        setPadding(new Insets(50));

        score = 0;
    }

    public static int getScore() {
        return score;
    }

    public void setScore(int scoreValue) {
        score = scoreValue;
        setText("Score: " + scoreValue);
    }

    public void addScore() {
        score += ReadPropertyFile.getIntValue("difficulty") * 100;
        setText("Score: " + score);
    }
}
