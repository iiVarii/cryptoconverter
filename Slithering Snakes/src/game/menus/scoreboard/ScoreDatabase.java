package game.menus.scoreboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.menus.customnodes.labels.StyledLabel;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ScoreDatabase {
    private static final int spaceCount = 5;
    private static final String jsonFilePathInternal = "/scoreData.json";
    private static final String jsonFilePathExternal = "scoreData.json";
    private static Map<String, Double> scoreData;

    public ScoreDatabase() {
        loadData();
    }

    public static void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            scoreData =
                    mapper.readValue(
                            new File(jsonFilePathExternal), new TypeReference<Map<String, Double>>() {
                            });
        } catch (Exception e) {
            scoreData = new HashMap<>();
            try {
                scoreData =
                        mapper.readValue(
                                ScoreDatabase.class.getResourceAsStream(jsonFilePathInternal),
                                new TypeReference<Map<String, Double>>() {
                                });
            } catch (IOException ignored) {
            }
        }
    }

    private void writeData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(jsonFilePathExternal), scoreData);
        } catch (Exception ignored) {
        }
    }

    public void writeScore(String name, Double value) {
        scoreData.put(name.trim(), value);
        writeData();
        loadData();
    }

    public Map<String, Double> getTopTen() {
        scoreData =
                scoreData.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .limit(10)
                        .collect(
                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        return scoreData;
    }

    public GridPane getGrid() {
        GridPane scoreGrid = new GridPane();
        int counter = 0;

        for (Map.Entry<String, Double> entry : getTopTen().entrySet()) {
            Font scoreFont = Font.font("", FontWeight.BOLD, FontPosture.ITALIC, 25);

            StyledLabel scoreText = StyledLabel.italicBoldLawnGreen(entry.getKey());
            StyledLabel scoreValue =
                    StyledLabel.italicBoldLawnGreen(String.valueOf((int) entry.getValue().doubleValue()));
            scoreText.setTextFill(Color.BLUEVIOLET);

            scoreText.setFont(scoreFont);
            scoreValue.setFont(scoreFont);

            scoreGrid.add(scoreText, 0, counter);
            scoreGrid.add(scoreValue, 1, counter);
            counter++;
        }
        if (scoreData.isEmpty()) scoreGrid.add(new Label("              "), 0, 0);
        return scoreGrid;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int maxSize =
                getTopTen().entrySet().stream()
                        .mapToInt(entry -> ((entry.getKey() + entry.getValue()).length()))
                        .max()
                        .orElse(0)
                        + spaceCount;

        for (Map.Entry<String, Double> entry : getTopTen().entrySet()) {
            int spaceLength = maxSize - ((entry.getKey() + entry.getValue()).length());
            String toAppend =
                    entry.getKey() + " ".repeat(spaceLength) + ((int) entry.getValue().doubleValue());
            stringBuilder.append(toAppend);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
