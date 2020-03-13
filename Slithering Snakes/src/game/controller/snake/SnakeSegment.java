package game.controller.snake;

import game.controller.tile.Tile;
import game.sprites.SpriteManager;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.util.Pair;

import java.util.*;

import static game.controller.snake.Snake.Direction.*;
import static java.lang.Math.*;
import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.cos;

public class SnakeSegment {
    private static final double DEFAULT_THICKNESS = 32;
    private static final double DEFAULT_WIDTH_RATIO = 0.5;
    private LinkedList<Tile> myTiles;
    private double myWidthRatio;
    private double myThickness;
    private Paint defaultPaint;
    private Paint darkPaint = SpriteManager.getPattern("snakeSkinSpriteDark");
    private Paint darkestPaint = SpriteManager.getPattern("snakeSkinSpriteDarkest");
    private double myProgress = 1;

    public SnakeSegment(LinkedList<Tile> tiles, Paint paint, double widthRatio, double thickness) {
        myTiles = tiles;
        myWidthRatio = widthRatio;
        myThickness = thickness;
        defaultPaint = paint;
    }

    public SnakeSegment(LinkedList<Tile> tiles, Paint paint) {
        this(tiles, paint, DEFAULT_WIDTH_RATIO, DEFAULT_THICKNESS);
    }

    private Snake.Direction getDirection(Tile start, Tile end) {
        Pair<Double, Double> difference =
                new Pair<>(Math.signum(end.getX() - start.getX()), Math.signum(end.getY() - start.getY()));
        return Snake.changeToDirection.getOrDefault(difference, Snake.Direction.RIGHT);
    }

    public Snake.Direction getDirection() {
        return getDirection(myTiles.get(1), myTiles.get(0));
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

    private List<Pair<Double, Double>> getPoints(Tile tile, Snake.Direction direction) {
        List<Pair<Double, Double>> points = new ArrayList<>();

        double x0 = tile.getX();
        double y0 = tile.getY();
        double width = tile.getWidth();
        double height = tile.getHeight();

        double dX0 = 0;
        double dX1 = 0;
        double dY0 = 0;
        double dY1 = 0;
        switch (direction) {
            case UP:
                dY0 = height;
                dY1 = height;
            case DOWN:
                dX0 = (0.5 - myWidthRatio / 2) * width;
                dX1 = (0.5 + myWidthRatio / 2) * width;
                break;
            case RIGHT:
                dX0 = width;
                dX1 = width;
            case LEFT:
                dY0 = (0.5 - myWidthRatio / 2) * height;
                dY1 = (0.5 + myWidthRatio / 2) * height;
                break;
        }
        points.add(toIsometric(x0 + dX0, y0 + dY0, myThickness));
        points.add(toIsometric(x0 + dX1, y0 + dY1, myThickness));
        points.add(toIsometric(x0 + dX1, y0 + dY1, 0));
        points.add(toIsometric(x0 + dX0, y0 + dY0, 0));
        return points;
    }

    private List<Pair<Double, Double>> scaleLinearly(
            List<Pair<Double, Double>> staticPoints,
            List<Pair<Double, Double>> dynamicPoints,
            double scale) {
        List<Pair<Double, Double>> points = new ArrayList<>();
        for (int i = 0; i < min(staticPoints.size(), dynamicPoints.size()); i++) {
            double startX = staticPoints.get(i).getKey();
            double endX = dynamicPoints.get(i).getKey();
            double startY = staticPoints.get(i).getValue();
            double endY = dynamicPoints.get(i).getValue();
            points.add(new Pair<>(startX + (endX - startX) * scale, startY + (endY - startY) * scale));
        }
        return points;
    }

    private Pair<Double, Double> pointsToFunction(Pair<Double, Double> p0, Pair<Double, Double> p1) {
        double a = (p0.getValue() - p1.getValue()) / (p0.getKey() - p1.getKey());
        double b = p0.getValue() - a * p0.getKey();
        return new Pair<>(a, b);
    }

    private Pair<Double, Double> functionsToPoints(Pair<Double, Double> f0, Pair<Double, Double> f1) {
        double x = (f0.getValue() - f1.getValue()) / (f1.getKey() - f0.getKey());
        double y = f0.getKey() * x + f0.getValue();
        return new Pair<>(x, y);
    }

    private Pair<Double, Double> ellipseTransformation(
            Pair<Double, Double> point, Pair<Double, Double> center, double multX, double multY) {
        double dX = point.getKey() - center.getKey();
        double dY = point.getValue() - center.getValue();

        return new Pair<>(multX * dX + center.getKey(), multY * dY + center.getValue());
    }

    private List<Pair<Double, Double>> scaleElliptically(
            List<Pair<Double, Double>> staticPoints,
            List<Pair<Double, Double>> dynamicPoints,
            double scale,
            int parity) {
        List<Pair<Double, Double>> points = new ArrayList<>();
        Pair<Double, Double> c0 =
                functionsToPoints(
                        pointsToFunction(staticPoints.get(0), staticPoints.get(1)),
                        pointsToFunction(dynamicPoints.get(0), dynamicPoints.get(1)));

        Pair<Double, Double> c1 =
                functionsToPoints(
                        pointsToFunction(staticPoints.get(2), staticPoints.get(3)),
                        pointsToFunction(dynamicPoints.get(2), dynamicPoints.get(3)));

        double staticAngle =
                atan2(
                        staticPoints.get(0).getValue() - c0.getValue(),
                        staticPoints.get(0).getKey() - c0.getKey());

        double dynamicAngle =
                atan2(
                        dynamicPoints.get(0).getValue() - c0.getValue(),
                        dynamicPoints.get(0).getKey() - c0.getKey());

        double delta = ((dynamicAngle - staticAngle)); // % PI);
        if (delta > PI) delta -= 2 * PI;
        if (delta < -PI) delta += 2 * PI;
        double angleScale = scale; // (1 * scale + 1.5);
        double newDynamicAngle = (staticAngle + delta * angleScale) % (2 * PI);

        double multX = cos(newDynamicAngle) / cos(dynamicAngle); // * 3.33 - 2.33;
        double multY = sin(newDynamicAngle) / sin(dynamicAngle); // * 0.5 + 0.5;
        if (parity == 1) {
            multX = multX * 3.33 - 2.33;
        } else if (parity == 0) {
            multY = multY * 0.5 + 0.5;
        }

        Pair<Double, Double> p0 = ellipseTransformation(dynamicPoints.get(0), c0, multX, multY);
        Pair<Double, Double> p1 = ellipseTransformation(dynamicPoints.get(1), c0, multX, multY);
        Pair<Double, Double> p2 = ellipseTransformation(dynamicPoints.get(2), c1, multX, multY);
        Pair<Double, Double> p3 = ellipseTransformation(dynamicPoints.get(3), c1, multX, multY);

        points.add(p0);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(c0);
        points.add(c1);

        return points;
    }

    private Pair<Double, Group> getTileVisual(
            Tile tile, Pair<Snake.Direction, Snake.Direction> heading, double tileProgress) {
        Group allSides = new Group();

        List<Pair<Double, Double>> startSidePoints =
                getPoints(tile, Snake.switchDirection.get(heading.getKey()));
        List<Pair<Double, Double>> endSidePoints = getPoints(tile, heading.getValue());
        List<Pair<Double, Double>> frontSidePoints = new ArrayList<>();

        Path topSide, frontSide, endSide;
        boolean sweepFlag = true; // true or false
        double switchMultiplier = 1; // 1 or -1
        int switchParity = 1; // 0 or 1
        switch (heading.toString()) {
            case "UP=UP":
            case "DOWN=DOWN":
            case "RIGHT=RIGHT":
            case "LEFT=LEFT":
                endSidePoints = scaleLinearly(startSidePoints, endSidePoints, tileProgress);
                topSide = new Path();
                topSide.setFill(defaultPaint);
                topSide.setStroke(defaultPaint);
                topSide
                        .getElements()
                        .addAll(
                                new MoveTo(startSidePoints.get(0).getKey(), startSidePoints.get(0).getValue()),
                                new LineTo(startSidePoints.get(1).getKey(), startSidePoints.get(1).getValue()),
                                new LineTo(endSidePoints.get(1).getKey(), endSidePoints.get(1).getValue()),
                                new LineTo(endSidePoints.get(0).getKey(), endSidePoints.get(0).getValue()),
                                new ClosePath());

                frontSide = new Path();
                frontSide.setFill(darkPaint);
                frontSide.setStroke(darkPaint);
                frontSide
                        .getElements()
                        .addAll(
                                new MoveTo(startSidePoints.get(1).getKey(), startSidePoints.get(1).getValue()),
                                new LineTo(startSidePoints.get(2).getKey(), startSidePoints.get(2).getValue()),
                                new LineTo(endSidePoints.get(2).getKey(), endSidePoints.get(2).getValue()),
                                new LineTo(endSidePoints.get(1).getKey(), endSidePoints.get(1).getValue()),
                                new ClosePath());

                endSide = new Path();
                endSide.setFill(darkestPaint);
                endSide.setStroke(darkestPaint);

                if (endSidePoints.get(2).getValue() < startSidePoints.get(2).getValue()) {
                    List<Pair<Double, Double>> temp = endSidePoints;
                    endSidePoints = startSidePoints;
                    startSidePoints = temp;
                }
                endSide
                        .getElements()
                        .addAll(
                                new MoveTo(endSidePoints.get(0).getKey(), endSidePoints.get(0).getValue()),
                                new LineTo(endSidePoints.get(1).getKey(), endSidePoints.get(1).getValue()),
                                new LineTo(endSidePoints.get(2).getKey(), endSidePoints.get(2).getValue()),
                                new LineTo(endSidePoints.get(3).getKey(), endSidePoints.get(3).getValue()),
                                new ClosePath());

                allSides.getChildren().addAll(topSide, frontSide, endSide);
                break;
            case "RIGHT=DOWN":
                switchMultiplier = -1;
            case "LEFT=UP":
                sweepFlag = false;
            case "UP=LEFT":
                if (sweepFlag) switchMultiplier = -1;
            case "DOWN=RIGHT":
                switchParity = 0;
            case "DOWN=LEFT":
                if (switchParity == 1) switchMultiplier = -1;
            case "UP=RIGHT":
                if (switchParity == 1) sweepFlag = false;
            case "LEFT=DOWN":
                if (switchParity == 1 && sweepFlag) switchMultiplier = -1;
            case "RIGHT=UP":
                double xRadius0 =
                        (1 - switchMultiplier * myWidthRatio) * tile.getWidth() / cos(atan(0.5)) / 2;
                double yRadius0 =
                        (1 - switchMultiplier * myWidthRatio) * tile.getHeight() / cos(atan(0.5)) / 4;
                double xRadius1 =
                        (1 + switchMultiplier * myWidthRatio) * tile.getWidth() / cos(atan(0.5)) / 2;
                double yRadius1 =
                        (1 + switchMultiplier * myWidthRatio) * tile.getHeight() / cos(atan(0.5)) / 4;

                double xRadiusFront = xRadius0;
                double yRadiusFront = yRadius0;

                if (switchParity == 1 && tileProgress > 0.5) {
                    frontSidePoints = scaleElliptically(startSidePoints, endSidePoints, 0.5, switchParity);
                } else {
                    frontSidePoints =
                            scaleElliptically(startSidePoints, endSidePoints, tileProgress, switchParity);
                }

                if (switchParity == 1) {
                    frontSidePoints.set(1, frontSidePoints.get(0));
                    frontSidePoints.set(2, frontSidePoints.get(3));
                }
                if (switchMultiplier == 1 && switchParity == 1 && tileProgress > 0.5) {
                    List<Pair<Double, Double>> temp0 =
                            scaleElliptically(startSidePoints, endSidePoints, 0.5, switchParity);
                    List<Pair<Double, Double>> temp1 =
                            scaleElliptically(startSidePoints, endSidePoints, tileProgress, switchParity);
                    frontSidePoints.set(0, temp0.get(1));
                    frontSidePoints.set(1, temp1.get(1));
                    frontSidePoints.set(2, temp1.get(2));
                    frontSidePoints.set(3, temp0.get(2));

                    xRadiusFront = xRadius1;
                    yRadiusFront = yRadius1;

                } else {
                    frontSidePoints.set(0, startSidePoints.get(1));
                    frontSidePoints.set(3, startSidePoints.get(2));
                }
                endSidePoints =
                        scaleElliptically(startSidePoints, endSidePoints, tileProgress, switchParity);

                topSide = new Path();
                topSide.setFill(defaultPaint);
                topSide.setStroke(defaultPaint);
                topSide
                        .getElements()
                        .addAll(
                                new MoveTo(startSidePoints.get(1).getKey(), startSidePoints.get(1).getValue()),
                                new ArcTo(
                                        xRadius0,
                                        yRadius0,
                                        0,
                                        endSidePoints.get(1 - switchParity).getKey(),
                                        endSidePoints.get(1 - switchParity).getValue(),
                                        false,
                                        sweepFlag),
                                new LineTo(
                                        endSidePoints.get(switchParity).getKey(),
                                        endSidePoints.get(switchParity).getValue()),
                                new ArcTo(
                                        xRadius1,
                                        yRadius1,
                                        0,
                                        startSidePoints.get(0).getKey(),
                                        startSidePoints.get(0).getValue(),
                                        false,
                                        !sweepFlag),
                                new ClosePath());

                frontSide = new Path();
                frontSide.setFill(darkestPaint);
                frontSide.setStroke(darkestPaint);
                frontSide
                        .getElements()
                        .addAll(
                                new MoveTo(frontSidePoints.get(0).getKey(), frontSidePoints.get(0).getValue()),
                                new ArcTo(
                                        xRadiusFront,
                                        yRadiusFront,
                                        0,
                                        frontSidePoints.get(1).getKey(),
                                        frontSidePoints.get(1).getValue(),
                                        false,
                                        sweepFlag),
                                new LineTo(frontSidePoints.get(2).getKey(), frontSidePoints.get(2).getValue()),
                                new ArcTo(
                                        xRadiusFront,
                                        yRadiusFront,
                                        0,
                                        frontSidePoints.get(3).getKey(),
                                        frontSidePoints.get(3).getValue(),
                                        false,
                                        !sweepFlag),
                                new ClosePath());

                endSide = new Path();
                endSide.setFill(darkPaint);
                endSide.setStroke(darkPaint);
                endSide
                        .getElements()
                        .addAll(
                                new MoveTo(endSidePoints.get(0).getKey(), endSidePoints.get(0).getValue()),
                                new LineTo(endSidePoints.get(1).getKey(), endSidePoints.get(1).getValue()),
                                new LineTo(endSidePoints.get(2).getKey(), endSidePoints.get(2).getValue()),
                                new LineTo(endSidePoints.get(3).getKey(), endSidePoints.get(3).getValue()),
                                new ClosePath());

                allSides.getChildren().addAll(frontSide, topSide, endSide);
                break;
        }

        double index =
                indexOfPoints(
                        Arrays.asList(
                                toSpacial(startSidePoints.get(2)),
                                toSpacial(startSidePoints.get(3)),
                                toSpacial(endSidePoints.get(2)),
                                toSpacial(endSidePoints.get(3))));

        return new Pair<>(index, allSides);
    }

    private Pair<Snake.Direction, Snake.Direction> flipHeading(
            Pair<Snake.Direction, Snake.Direction> heading) {
        Map<Snake.Direction, Snake.Direction> other = new HashMap<>();
        other.put(UP, DOWN);
        other.put(DOWN, UP);
        other.put(LEFT, RIGHT);
        other.put(RIGHT, LEFT);

        return new Pair<>(other.get(heading.getValue()), other.get(heading.getKey()));
    }

    public List<Pair<Double, Group>> getIndexedVisual() {
        Snake.Direction nextDirection = getDirection(myTiles.get(1), myTiles.get(0));
        Snake.Direction currentDirection = getDirection(myTiles.get(2), myTiles.get(1));
        Snake.Direction previousDirection = getDirection(myTiles.get(3), myTiles.get(2));
        Pair<Snake.Direction, Snake.Direction> nextTileHeading =
                new Pair<>(currentDirection, nextDirection);
        Pair<Snake.Direction, Snake.Direction> previousTileHeading =
                new Pair<>(previousDirection, currentDirection);

        return Arrays.asList(
                getTileVisual(myTiles.get(1), nextTileHeading, myProgress),
                getTileVisual(myTiles.get(2), flipHeading(previousTileHeading), 1 - myProgress));
    }

    public void setProgress(double value) {
        myProgress = value;
    }

    public Tile moveToTile(Tile tile) {
        myTiles.addFirst(tile);
        myTiles.removeLast();
        return myTiles.get(1);
    }

    public Set<Tile> getOccupiedTiles() {
        Set<Tile> output = new HashSet<>();
        output.add(myTiles.get(1));
        return output;
    }

    public List<Tile> getTiles() {
        return myTiles;
    }

    @Override
    public String toString() {
        StringJoiner output = new StringJoiner("\n");
        output.add("SnakeSegment:");
        for (Tile tile : myTiles) {
            output.add("\t" + tile.toString());
        }
        return output.toString();
    }
}
