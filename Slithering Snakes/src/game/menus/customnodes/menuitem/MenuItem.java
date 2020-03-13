package game.menus.customnodes.menuitem;

import game.controller.visual.Visual;
import game.menus.MainMenu;
import game.menus.Options;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MenuItem extends HBox {
    private static final Font FONT = Font.font("", FontWeight.BOLD, 24);
    private menuShapeAddition c1 = new menuShapeAddition(), c2 = new menuShapeAddition();
    private Text text;
    private Runnable script;

    public MenuItem(String name) {
        super(15);
        setAlignment(Pos.CENTER);

        text = new Text(name);
        text.setFont(FONT);

        GaussianBlur visualEffect = new GaussianBlur(2);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(2);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        visualEffect.setInput(dropShadow);
        text.setEffect(visualEffect);

        getChildren().addAll(c1, text, c2);
        setActive(false);
        setListeners();
    }

    public MenuItem(Node node) {
        super(15);
        setAlignment(Pos.CENTER);
        getChildren().addAll(c1, node, c2);
        setActive(false);
        setListeners();
    }

    private static Circle createCircleItem() {
        Circle outputCircle = new Circle(10, Color.TRANSPARENT);
        outputCircle.setStrokeType(StrokeType.OUTSIDE);
        outputCircle.setStroke(Color.web("#594696", 0.5));
        outputCircle.setStrokeWidth(1);
        outputCircle.setEffect(new BoxBlur(5, 5, 2));
        FillTransition fillTransition =
                new FillTransition(
                        Duration.millis(1000), outputCircle, Color.web("#594696"), Color.web("#83FDFF"));
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.play();
        return outputCircle;
    }

    public void setActive(boolean b) {
        c1.setVisible(b);
        c2.setVisible(b);
        if (text != null) {
            text.setFill(b ? Color.web("#83FDFF") : Color.BLUEVIOLET);
        }
    }

    public void setOnActivate(Runnable r) {
        script = r;
    }

    public void activate() {
        if (script != null) script.run();
        requestFocus();
    }

    private void setListeners() {
        setOnMouseEntered(
                mouseEvent -> {
                    MainMenu.deactivateMenuItems();
                    Options.deactivateMenuItems();
                    Visual.deactivateEscapeItems();
                    Visual.deactivateDeathItems();
                    setActive(true);
                });
        setOnMouseExited(
                mouseEvent -> {
                    MainMenu.deactivateMenuItems();
                    Options.deactivateMenuItems();
                    Visual.deactivateEscapeItems();
                    Visual.deactivateDeathItems();
                    setActive(false);
                });
    }

    private static class menuShapeAddition extends Parent {
        menuShapeAddition() {
            getChildren().addAll(createCircleItem(), createCircleItem());
            setEffect(new GaussianBlur(2));
        }
    }
}
