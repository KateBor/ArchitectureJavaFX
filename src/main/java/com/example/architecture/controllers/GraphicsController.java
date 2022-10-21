package com.example.architecture.controllers;

import com.example.architecture.ArchitectureFX;
import com.example.architecture.messages.GraphicsResponse;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class GraphicsController {
    @FXML
    public LineChart<Number, Number> lineChart;
    @FXML
    public ScrollPane scrollPane;

    private Stage dialogStage;
    private ArchitectureFX architectureFX;
    final int WINDOW_SIZE = 10;
    private GraphicsResponse response;

    public void setArchitectureFX(ArchitectureFX architectureFX) {
        this.architectureFX = architectureFX;
        response = architectureFX.getGraphicsResponse();
    }

    @FXML
    public void showGraphics() {

        scrollPane.setMinSize(response.getCurrentTime() * 100,600);
        lineChart.setMinSize(scrollPane.getMinWidth(),scrollPane.getMinHeight()-20);

        //отказы
        XYChart.Series<Number, Number> reject = new XYChart.Series<>();
        for (var pair : response.rejection) {
            reject.getData().add(new XYChart.Data<>(pair.getX(), pair.getY()));
        }
        lineChart.getData().add(reject);

        //источники
        for(int i = 0; i < response.coordinatesSource.size(); i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("И" + i);
            for (var pair : response.coordinatesSource.get(i)) {
                series.getData().add(new XYChart.Data<>(pair.getX(), pair.getY()));
            }
            lineChart.getData().add(series);
        }

        //приборы
        for(int i = 0; i < response.coordinatesDevice.size(); i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("П" + i);
            for (var pair : response.coordinatesDevice.get(i)) {
                series.getData().add(new XYChart.Data<>(pair.getX(), pair.getY()));
            }
            lineChart.getData().add(series);
        }

        //буфер
        for(int i = 0; i < response.coordinatesBuffer.size(); i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Б" + i);
            for (var pair : response.coordinatesBuffer.get(i)) {
                series.getData().add(new XYChart.Data<>(pair.getX(), pair.getY()));
            }
            lineChart.getData().add(series);
        }
    }
}
