package game.controller.snake;

import game.controller.tile.Tile;
import game.controller.visual.IndexManager;
import game.sprites.SpriteManager;
import javafx.util.Pair;

import java.util.*;

public class Snake {
    public static final Map<Direction, Pair<Double, Double>> directionToChange =
            Map.of(
                    Direction.RIGHT, new Pair(1d, 0d),
                    Direction.LEFT, new Pair(-1d, 0d),
                    Direction.UP, new Pair(0d, 1d),
                    Direction.DOWN, new Pair(0d, -1d));
    public static final List<Direction> directionTurnLeftCycle =
            Arrays.asList(Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT);
    static final Map<Pair<Double, Double>, Direction> changeToDirection =
            Map.of(
                    new Pair(1d, 0d), Direction.RIGHT,
                    new Pair(-1d, 0d), Direction.LEFT,
                    new Pair(0d, 1d), Direction.UP,
                    new Pair(0d, -1d), Direction.DOWN);
    private static final double DEFAULT_THICKNESS = 48;
    private static final double DEFAULT_WIDTH = 48;
    static Map<Direction, Direction> switchDirection =
            Map.of(
                    Direction.UP, Direction.DOWN,
                    Direction.DOWN, Direction.UP,
                    Direction.LEFT, Direction.RIGHT,
                    Direction.RIGHT, Direction.LEFT);
    private LinkedList<Tile> myTiles;
    private List<SnakeSegment> mySegments = new LinkedList<>();
    private Direction myLastDirection;
    private Direction myDirection;
    private double myTileProgress = 0;
    private double myThickness;
    private double myWidth;
    private boolean dead = false;

    public Snake(LinkedList<Tile> tiles, Direction direction, double thickness, double width) {
        if (tiles.size() < 4) throw new RuntimeException("Not enough tiles given to Snake constructor");

        LinkedList<Tile> current_tiles = new LinkedList<>(tiles.subList(0, 3));
        int index = 2;
        do {
            index++;
            current_tiles.add(tiles.get(index));
            mySegments.add(
                    new SnakeSegment(
                            (LinkedList<Tile>) current_tiles.clone(),
                            SpriteManager.getPattern("snakeSkinSprite")));
            current_tiles.removeFirst();
        } while (index < tiles.size() - 1);
        myTiles = tiles;
        myDirection = direction;
        myThickness = thickness;
        myWidth = width;
    }

    public Snake(LinkedList<Tile> tiles, Direction direction) {
        this(tiles, direction, DEFAULT_THICKNESS, DEFAULT_WIDTH);
    }

    public void addSegmentsToIndex() {
        for (SnakeSegment segment : mySegments) {
            IndexManager.addAllShapes(segment.getIndexedVisual());
        }
    }

    public void moveToTile(Tile tile) {
        setTileProgress(0);

        for (SnakeSegment segment : mySegments) {
            tile = segment.moveToTile(tile);
        }
    }

    public void growToTile(Tile tile) {
        SnakeSegment newSegment =
                new SnakeSegment(
                        (LinkedList<Tile>)
                                ((LinkedList<Tile>) mySegments.get(mySegments.size() - 1).getTiles()).clone(),
                        SpriteManager.getPattern("snakeSkinSprite"));
        moveToTile(tile);
        mySegments.add(newSegment);
    }

    public double getTileProgress() {
        return myTileProgress;
    }

    public void setTileProgress(double value) {
        myTileProgress = value;
        for (SnakeSegment segment : mySegments) {
            segment.setProgress(myTileProgress);
        }
    }

    public Tile getHeadTile() {
        return mySegments.get(0).getTiles().get(0);
    }

    public Tile getVisualHeadTile() {
        return mySegments.get(0).getTiles().get(1);
    }

    public List<Tile> getTiles() {
        List<Tile> output = new ArrayList<>();
        for (SnakeSegment segment : mySegments) {
            output.addAll(segment.getTiles());
        }
        return output;
    }

    public Set<Tile> getOccupiedTiles() {
        Set<Tile> output = new HashSet<>();
        for (int i = 0; i < mySegments.size() - 1; i++) {
            SnakeSegment segment = mySegments.get(i);
            output.addAll(segment.getOccupiedTiles());
        }
        return output;
    }

    public void kill() {
        dead = true;
    }

    public boolean getDead() {
        return dead;
    }

    public Direction getDirection() {
        return myDirection;
    }

    public void setDirection(Direction direction) {
        if (!switchDirection.get(mySegments.get(0).getDirection()).equals(direction))
            myDirection = direction;
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
