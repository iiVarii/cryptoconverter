package game.controller.visual;

import javafx.scene.Group;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IndexManager {
    private static List<Pair<Double, Group>> shapes = new ArrayList<>();

    public static void addShape(Pair<Double, Group> shape) {
        shapes.add(shape);
    }

    public static void addAllShapes(List<Pair<Double, Group>> shapes) {
        for (Pair<Double, Group> shape : shapes) {
            addShape(shape);
        }
    }

    public static List<Group> getShapes() {
        shapes.sort(Comparator.comparingDouble(Pair::getKey));
        return shapes.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    public static void clear() {
        shapes = new ArrayList<>();
    }
}
