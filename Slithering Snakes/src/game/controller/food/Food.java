package game.controller.food;

import game.controller.tile.Tile;
import game.controller.visual.IndexManager;
import game.sprites.SpriteManager;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.cos;

public class Food {
    private static final Paint DEFAULT_TOP_PAINT = SpriteManager.getPattern("foodTopSprite");
    private static final Paint DEFAULT_LEFT_PAINT = SpriteManager.getPattern("foodLeftSprite");
    private static final Paint DEFAULT_RIGHT_PAINT = SpriteManager.getPattern("foodRightSprite");
    private static double DEFAULT_WIDTH_RATIO = 0.25;
    private Tile myTile;

    private Food(Tile tile, double widthRatio) {
        myTile = tile;
        DEFAULT_WIDTH_RATIO = widthRatio;
    }

    public Food(Tile tile) {
        this(tile, DEFAULT_WIDTH_RATIO);
    }

    private Pair<Double, Double> toIsometric(double x, double y, double z) {
        return new Pair<>(cos(atan(0.5)) * (x - y), 0.5 * (x + y) - z);
    }

    private Pair<Double, Double> toSpacial(Pair<Double, Double> point) {
        return new Pair<>(
                point.getKey() / 2 / cos(atan(0.5)) + point.getValue(),
                point.getValue() - point.getKey() / 2 / cos(atan(0.5)));
    }

    private Double indexOfPoints(List<Pair<Double, Double>> points) {
        return points.stream().mapToDouble(point -> point.getKey() + point.getValue()).sum()
                / points.size();
    }

    private Pair<Double, Group> getVisual() {
        Pair<Double, Double> upperBackPoint =
                toIsometric(
                        myTile.getX() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getWidth() * DEFAULT_WIDTH_RATIO);
        Pair<Double, Double> upperLeftPoint =
                toIsometric(
                        myTile.getX() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getWidth() * DEFAULT_WIDTH_RATIO);
        Pair<Double, Double> upperRightPoint =
                toIsometric(
                        myTile.getX() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getWidth() * DEFAULT_WIDTH_RATIO);
        Pair<Double, Double> upperFrontPoint =
                toIsometric(
                        myTile.getX() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getWidth() * DEFAULT_WIDTH_RATIO);
        Pair<Double, Double> lowerLeftPoint =
                toIsometric(
                        myTile.getX() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        0);
        Pair<Double, Double> lowerRightPoint =
                toIsometric(
                        myTile.getX() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        0);
        Pair<Double, Double> lowerFrontPoint =
                toIsometric(
                        myTile.getX() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 + DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        0);
        Pair<Double, Double> lowerBackPoint =
                toIsometric(
                        myTile.getX() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        myTile.getY() + (1 - DEFAULT_WIDTH_RATIO) * myTile.getWidth() / 2,
                        0);

        Path topSide = new Path();
        topSide.setFill(DEFAULT_TOP_PAINT);
        topSide.setStroke(DEFAULT_TOP_PAINT);
        topSide
                .getElements()
                .addAll(
                        new MoveTo(upperBackPoint.getKey(), upperBackPoint.getValue()),
                        new LineTo(upperLeftPoint.getKey(), upperLeftPoint.getValue()),
                        new LineTo(upperFrontPoint.getKey(), upperFrontPoint.getValue()),
                        new LineTo(upperRightPoint.getKey(), upperRightPoint.getValue()),
                        new ClosePath());

        Path frontSide = new Path();
        frontSide.setFill(DEFAULT_RIGHT_PAINT);
        frontSide.setStroke(DEFAULT_RIGHT_PAINT);
        frontSide
                .getElements()
                .addAll(
                        new MoveTo(upperLeftPoint.getKey(), upperLeftPoint.getValue()),
                        new LineTo(lowerLeftPoint.getKey(), lowerLeftPoint.getValue()),
                        new LineTo(lowerFrontPoint.getKey(), lowerFrontPoint.getValue()),
                        new LineTo(upperFrontPoint.getKey(), upperFrontPoint.getValue()),
                        new ClosePath());

        Path endSide = new Path();
        endSide.setFill(DEFAULT_LEFT_PAINT);
        endSide.setStroke(DEFAULT_LEFT_PAINT);
        endSide
                .getElements()
                .addAll(
                        new MoveTo(upperFrontPoint.getKey(), upperFrontPoint.getValue()),
                        new LineTo(upperRightPoint.getKey(), upperRightPoint.getValue()),
                        new LineTo(lowerRightPoint.getKey(), lowerRightPoint.getValue()),
                        new LineTo(lowerFrontPoint.getKey(), lowerFrontPoint.getValue()),
                        new ClosePath());

        Group allSides = new Group();
        allSides.getChildren().addAll(frontSide, topSide, endSide);

        double index =
                indexOfPoints(
                        Arrays.asList(
                                toSpacial(lowerBackPoint),
                                toSpacial(lowerFrontPoint),
                                toSpacial(lowerLeftPoint),
                                toSpacial(lowerRightPoint)));

        return new Pair<>(index, allSides);
    }

    public void addFoodToIndex() {
        IndexManager.addShape(getVisual());
    }

    public Tile getTile() {
        return myTile;
    }

    public void setTile(Tile tile) {
        myTile = tile;
    }
}
