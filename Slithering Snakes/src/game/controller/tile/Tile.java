package game.controller.tile;

import game.sprites.SpriteManager;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.cos;

public class Tile {
    public static final int DEFAULT_SIZE = 64;
    static final int DEFAULT_THICKNESS = DEFAULT_SIZE / 2;
    private Paint topPaint = SpriteManager.getPattern("tileTopSprite");
    private Paint rightPaint = SpriteManager.getPattern("tileRightSprite");
    private Paint leftPaint = SpriteManager.getPattern("tileLeftSprite");
    private double myWidth;
    private double myHeight;
    private double myThickness;
    private double myX;
    private double myY;

    public Tile(double x, double y, int width, int height, double thickness) {
        myX = x;
        myY = y;
        myWidth = width;
        myHeight = height;
        myThickness = thickness;
    }

    public Tile(double x, double y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_THICKNESS);
    }

    public Tile() {
        this(0, 0);
    }

    public Group getVisual() {
        Group allSides = new Group();

        Polygon topSide = new Polygon();
        topSide
                .getPoints()
                .addAll(
                        cos(atan(0.5)) * (myX - myY), (myY + myX) * 0.5,
                        cos(atan(0.5)) * (myX - myY + myWidth), (myY + myX + myHeight) * 0.5,
                        cos(atan(0.5)) * (myX - myY), (myY + myX) * 0.5 + myHeight,
                        cos(atan(0.5)) * (myX - myY - myWidth), (myY + myX + myHeight) * 0.5);
        topSide.setFill(topPaint);

        Polygon leftSide = new Polygon();
        leftSide
                .getPoints()
                .addAll(
                        cos(atan(0.5)) * (myX - myY - myWidth), (myY + myX + myHeight) * 0.5,
                        cos(atan(0.5)) * (myX - myY), (myY + myX) * 0.5 + myHeight,
                        cos(atan(0.5)) * (myX - myY), (myY + myX) * 0.5 + myHeight + myThickness,
                        cos(atan(0.5)) * (myX - myY - myWidth), (myY + myX + myHeight) * 0.5 + myThickness);
        leftSide.setFill(leftPaint);

        Polygon rightSide = new Polygon();
        rightSide
                .getPoints()
                .addAll(
                        cos(atan(0.5)) * (myX - myY + myWidth), (myY + myX + myHeight) * 0.5,
                        cos(atan(0.5)) * (myX - myY + myWidth), (myY + myX + myHeight) * 0.5 + myThickness,
                        cos(atan(0.5)) * (myX - myY), (myY + myX) * 0.5 + myHeight + myThickness,
                        cos(atan(0.5)) * (myX - myY), (myY + myX) * 0.5 + myHeight);
        rightSide.setFill(rightPaint);

        allSides.getChildren().addAll(topSide, leftSide, rightSide);
        return allSides;
    }

    public double getX() {
        return myX;
    }

    public void setX(double value) {
        myX = value;
    }

    public double getY() {
        return myY;
    }

    public void setY(double value) {
        myY = value;
    }

    public double getWidth() {
        return myWidth;
    }

    public double getHeight() {
        return myHeight;
    }

    @Override
    public String toString() {
        return "Tile(" + (myX + myWidth / 2) + "; " + (myY + myWidth / 2) + ")";
    }
}
