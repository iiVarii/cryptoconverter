package game.controller;

import game.controller.food.Food;
import game.controller.snake.Snake;
import game.controller.tile.Tile;
import game.controller.visual.ScoreLabel;
import game.controller.visual.Visual;
import game.menus.music.MusicManager;
import game.menus.scoreboard.ScoreBoard;
import game.properties.ReadPropertyFile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

public class Controller {

    private static final List<Character> allowedInputList =
            Arrays.asList(
                    '\u0041', '\u0042', '\u0043', '\u0044', '\u0045', '\u0046', '\u0047', '\u0048', '\u0049',
                    '\u004a', '\u004b', '\u004c', '\u004d', '\u004e', '\u004f', '\u0050', '\u0051', '\u0052',
                    '\u0053', '\u0160', '\u005a', '\u017d', '\u0054', '\u0055', '\u0056', '\u0057', '\u00d5',
                    '\u00c4', '\u00d6', '\u00dc', '\u0058', '\u0059', '\u0031', '\u0032', '\u0033', '\u0034',
                    '\u0035', '\u0036', '\u0037', '\u0038', '\u0039', '\u0030', ' ');
    private static final double PROGRESS_STEP = 0.01;
    private static List<List<Tile>> myTiles = new ArrayList<>();
    private static List<Snake> mySnakes = new ArrayList<>();
    private static List<Food> myFoods = new ArrayList<>();
    private static Timeline clock;
    private static double clockRate = 1;
    private static double clockSpeed;
    private static boolean clockIsRunning = false;
    private static int playerCount = 1;

    public static void setup() {
        playerCount = ReadPropertyFile.getIntValue("playercount");
        createField(8, 8);
        createPlayer(3, 4, playerCount);
        createFood(3, 2);
        clockSpeed = Math.pow(ReadPropertyFile.getIntValue("difficulty"), 1.25);
        createClock();
    }

    public static void bindKeys(Scene scene) {
        scene.setOnKeyPressed(
                keyEvent -> {
                    Snake player = getSnakes().get(0);
                    if (!Visual.isEscapeMenuShown() && !Visual.isDeathMenuShown()) {
                        if (keyEvent.getCode() == KeyCode.valueOf(ReadPropertyFile.getStringValue("upkey"))) {
                            player.setTileProgress(min(player.getTileProgress() + PROGRESS_STEP, 1));
                        } else if (keyEvent.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("downkey"))) {
                            player.setTileProgress(max(player.getTileProgress() - PROGRESS_STEP, 0));
                        } else if (keyEvent.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("leftkey"))) {
                            player.setDirection(
                                    Snake.directionTurnLeftCycle.get(
                                            (Snake.directionTurnLeftCycle.indexOf(player.getDirection()) + 3)
                                                    % Snake.directionTurnLeftCycle.size()));
                        } else if (keyEvent.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("rightkey"))) {
                            player.setDirection(
                                    Snake.directionTurnLeftCycle.get(
                                            (Snake.directionTurnLeftCycle.indexOf(player.getDirection()) + 5)
                                                    % Snake.directionTurnLeftCycle.size()));
                        } else if ((keyEvent.getCode() == KeyCode.valueOf("P")
                                || keyEvent.getCode()
                                == KeyCode.valueOf(ReadPropertyFile.getStringValue("pausegame")))
                                && !Visual.isEscapeMenuShown()
                                && !Visual.isDeathMenuShown()) {
                            toggleClock();
                        }
                        if (playerCount == 2) {
                            Snake player2 = getSnakes().get(1);
                            if (keyEvent.getCode() == KeyCode.LEFT) {
                                player2.setDirection(
                                        Snake.directionTurnLeftCycle.get(
                                                (Snake.directionTurnLeftCycle.indexOf(player2.getDirection()) + 3)
                                                        % Snake.directionTurnLeftCycle.size()));
                            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                                player2.setDirection(
                                        Snake.directionTurnLeftCycle.get(
                                                (Snake.directionTurnLeftCycle.indexOf(player2.getDirection()) + 5)
                                                        % Snake.directionTurnLeftCycle.size()));
                            }
                        }
                        Visual.resetIndex();
                        Visual.displaySnakes(Controller.getSnakes());
                        Visual.displayFoods(Controller.getFoods());
                        Visual.displayIndex();

                    } else {
                        if (Visual.getDeathMenuScoreContainer().isVisible()) {
                            if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                                String tempScoreText = Visual.getScoreText().getText();
                                if (!tempScoreText.equals("")) {
                                    Visual.getScoreText()
                                            .setText(tempScoreText.substring(0, tempScoreText.length() - 1));
                                }
                            } else if (allowedInputList.contains(
                                    keyEvent.getCode().getChar().toUpperCase().charAt(0))) {
                                Visual.getScoreText()
                                        .setText(Visual.getScoreText().getText() + keyEvent.getCode().getChar());
                            } else if (keyEvent.getCode() == KeyCode.ENTER
                                    || keyEvent.getCode() == KeyCode.ESCAPE) {
                                if (keyEvent.getCode() == KeyCode.ENTER
                                        && !Visual.getScoreText().getText().equals("")) {
                                    ScoreBoard.writeScore(Visual.getScoreText().getText(), ScoreLabel.getScore());
                                }
                                Visual.getDeathMenuScoreContainer().setVisible(false);
                                Visual.getDeathMenuBox().setVisible(true);
                                Visual.getScoreText().setText("");
                                Visual.getScoreLabel().setScore(0);
                            }
                        } else if (Visual.getDeathMenuBox().isVisible()) {
                            if (keyEvent.getCode().equals(KeyCode.UP)
                                    || keyEvent.getCode()
                                    == KeyCode.valueOf(ReadPropertyFile.getStringValue("upkey"))) {
                                if (Visual.getDeathMenuIndex() > 0) {
                                    Visual.deactivateDeathItems();
                                    Visual.setDeathMenuIndex(Visual.getDeathMenuIndex() - 1);
                                    Visual.getDeathMenuItem(Visual.getDeathMenuIndex()).setActive(true);
                                } else {
                                    Visual.deactivateDeathItems();
                                    Visual.getDeathMenuItem(Visual.getDeathMenuBox().getChildren().size() - 1)
                                            .setActive(true);
                                    Visual.setDeathMenuIndex(Visual.getDeathMenuBox().getChildren().size() - 1);
                                }
                            } else if (keyEvent.getCode()
                                    == KeyCode.valueOf(ReadPropertyFile.getStringValue("downkey"))
                                    || keyEvent.getCode() == KeyCode.DOWN) {
                                if (Visual.getDeathMenuIndex()
                                        < Visual.getDeathMenuBox().getChildren().size() - 1) {
                                    Visual.deactivateDeathItems();
                                    Visual.setDeathMenuIndex(Visual.getDeathMenuIndex() + 1);
                                    Visual.getDeathMenuItem(Visual.getDeathMenuIndex()).setActive(true);
                                } else {
                                    Visual.deactivateDeathItems();
                                    Visual.getDeathMenuItem(0).setActive(true);
                                    Visual.setDeathMenuIndex(0);
                                }
                            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                                Visual.getDeathMenuItem(Visual.getDeathMenuIndex()).activate();
                            }
                        }
                    }
                });
    }

    public static List<Food> getFoods() {
        return myFoods;
    }

    private static void createClock() {
        clockIsRunning = true;
        clock =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(PROGRESS_STEP / clockSpeed),
                                event -> {
                                    for (Snake snake : mySnakes) {
                                        snake.setTileProgress(min(snake.getTileProgress() + PROGRESS_STEP, 1));
                                        if (snake.getTileProgress() == 1) {
                                            Pair<Double, Double> indexChange =
                                                    Snake.directionToChange.get(snake.getDirection());
                                            Tile headTile = snake.getHeadTile();
                                            Pair<Double, Double> headIndex = null;
                                            for (int i = 0; i < myTiles.size(); i++) {
                                                if (myTiles.get(i).contains(headTile))
                                                    headIndex =
                                                            new Pair((double) i, (double) myTiles.get(i).indexOf(headTile));
                                            }

                                            if (snake.getDead()) {
                                                endGame();
                                                break;
                                            }

                                            if (headIndex == null)
                                                throw new IllegalStateException("Snake head not found");
                                            int newX = (int) (headIndex.getKey() + indexChange.getKey());
                                            int newY = (int) (headIndex.getValue() + indexChange.getValue());

                                            if (!snake.getDead() && isFreeLocation(newX, newY, snake)) {
                                                if (checkFoodIntersection(getFoods(), snake)) {
                                                    snake.growToTile(myTiles.get(newX).get(newY));
                                                    checkFoodIntersection(getFoods(), snake);
                                                } else {
                                                    snake.moveToTile(myTiles.get(newX).get(newY));
                                                }
                                            } else if (!snake.getDead()) {
                                                checkFoodIntersection(getFoods(), snake);
                                                killSnake(snake);
                                            }
                                        }
                                    }
                                    Visual.resetIndex();
                                    Visual.displaySnakes(Controller.getSnakes());
                                    Visual.displayFoods(Controller.getFoods());
                                    Visual.displayIndex();

                                    if (mySnakes.get(0).getOccupiedTiles().size() * 0.2 - clockRate * 1.2 > 0) {
                                        clockRate += 0.25;
                                        clock.setRate(clockRate);
                                    }
                                }));
        clock.setCycleCount(Timeline.INDEFINITE);
    }

    private static void killSnake(Snake snake) {
        Tile headTile = snake.getHeadTile();
        Pair<Double, Double> multipliers = Snake.directionToChange.get(snake.getDirection());
        Tile fakeTile =
                new Tile(
                        headTile.getX() + multipliers.getKey() * Tile.DEFAULT_SIZE,
                        headTile.getY() + multipliers.getValue() * Tile.DEFAULT_SIZE);
        snake.moveToTile(fakeTile);
        snake.kill();
    }

    private static void endGame() {
        Visual.toggleDeathMenu(true);
        clockIsRunning = false;
        clock.stop();
    }

    private static boolean isFreeLocation(int newX, int newY, Snake snake) {
        if (newX < 0 || newX >= myTiles.size() || newY < 0 || newY >= myTiles.get(0).size())
            return false;
        return !getOccupiedTiles(snake).contains(myTiles.get(newX).get(newY));
    }

    private static boolean checkFoodIntersection(List<Food> foods, Snake snake) {
        for (Food food : foods) {
            if (food.getTile().getX() == snake.getHeadTile().getX()
                    && food.getTile().getY() == snake.getHeadTile().getY()
                    && snake.getTileProgress() > 0.5
                    && snake.getTileProgress() <= 1) {
                MusicManager.playEffect("eatSound");
                food.setTile(getNewFoodTile());
                Visual.getScoreLabel().addScore();
                return true;
            }
        }
        return false;
    }

    private static Set<Tile> getOccupiedTiles(Snake snake) {
        if (snake == null) return getOccupiedTiles();
        Set<Tile> occupiedTiles = new HashSet<>();
        occupiedTiles.addAll(getAllOccupiedTiles());
        occupiedTiles.removeAll(snake.getTiles());
        occupiedTiles.addAll(snake.getOccupiedTiles());
        return occupiedTiles;
    }

    private static Set<Tile> getOccupiedTiles() {
        Set<Tile> occupiedTiles = new HashSet<>();
        for (Snake snake : mySnakes) {
            occupiedTiles.addAll(snake.getOccupiedTiles());
        }
        return occupiedTiles;
    }

    private static Set<Tile> getAllOccupiedTiles() {
        Set<Tile> occupiedTiles = new HashSet<>();
        for (Snake snake : mySnakes) {
            occupiedTiles.addAll(snake.getTiles());
        }
        return occupiedTiles;
    }

    private static Tile getNewFoodTile() {
        int randomX = ThreadLocalRandom.current().nextInt(0, myTiles.size());
        int randomY = ThreadLocalRandom.current().nextInt(0, myTiles.get(0).size());

        Set<Tile> occupiedTiles = getAllOccupiedTiles();
        if (occupiedTiles.size() == myTiles.size() * myTiles.get(0).size()) {
            for (Snake snake : mySnakes) {
                snake.kill();
            }
            endGame();
        }

        if (occupiedTiles.contains(myTiles.get(randomX).get(randomY))) {
            return getNewFoodTile();
        }
        return myTiles.get(randomX).get(randomY);
    }

    public static void startClock() {
        new Timer()
                .schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                clock.play();
                            }
                        },
                        1000);
    }

    private static void createField(int x, int y) {
        myTiles = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            List<Tile> row = new ArrayList<>();
            for (int j = 0; j < y; j++) {
                row.add(new Tile());
            }
            myTiles.add(row);
        }
    }

    private static Snake makeSnake(int x, int y) {
        LinkedList<Tile> tiles = new LinkedList<>();
        tiles.add(myTiles.get(x).get(y - 2));
        tiles.add(myTiles.get(x).get(y - 1));
        tiles.add(myTiles.get(x).get(y));
        tiles.add(myTiles.get(x).get(y + 1));
        tiles.add(myTiles.get(x).get(y + 2));

        Snake player = new Snake(tiles, Snake.Direction.DOWN);
        player.setTileProgress(0);
        return player;
    }

    private static void createPlayer(int x, int y, int count) {
        mySnakes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Snake player = makeSnake(x + i, y);
            mySnakes.add(player);
        }
    }

    private static void createFood(int x, int y) {
        myFoods = new ArrayList<>();
        myFoods.add(new Food(myTiles.get(x).get(y)));
    }

    public static List<List<Tile>> getTiles() {
        return myTiles;
    }

    public static List<Snake> getSnakes() {
        return mySnakes;
    }

    public static void toggleClock() {
        if (clockIsRunning) {
            clock.pause();
        } else {
            clock.play();
        }
        clockIsRunning = !clockIsRunning;
    }

    public static void toggleClock(boolean bool) {
        if (bool) {
            clock.play();
        } else {
            clock.stop();
        }
        clockIsRunning = bool;
    }
}
