package com.example.architecture.controllers;

import com.example.architecture.ArchitectureFX;
import com.example.architecture.messages.ExperimentParamsRequest;
import com.example.architecture.messages.ExperimentResponse;
import com.example.architecture.services.Simulation;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Data;

@Data
public class ParamsController {
    @FXML
    private TextField sourceNum;
    @FXML
    private TextField deviceNum;
    @FXML
    private TextField bufferNum;
    @FXML
    private TextField eventsNum;

    @FXML
    public TextField alpha;
    @FXML
    public TextField beta;
    @FXML
    public TextField lambda;

    private ArchitectureFX architectureFX;

    @FXML
    public void setParams() {

        ExperimentParamsRequest req = new ExperimentParamsRequest(
                Integer.parseInt(sourceNum.getText()),
                Integer.parseInt(deviceNum.getText()),
                Integer.parseInt(bufferNum.getText()),
                Integer.parseInt(eventsNum.getText()),
                Double.parseDouble(alpha.getText()),
                Double.parseDouble(beta.getText()),
                Double.parseDouble(lambda.getText())
        );


        Simulation simulation = new Simulation();

        double p0 = 2, p1 = 1;

        while (countAccuracy(p0, p1) >= 0.1) {
            simulation.startWork(req);
            p0 = simulation.getP0();
            req.setEventsNum(simulation.calculateN0());
            simulation.startWork(req);
            req.setEventsNum(simulation.calculateN0());
            p1 = simulation.getP0();
        }

        System.out.println("N = " + req.getEventsNum() + "; p = " + p0 + "; p1 = " + p1);
        //вывести вводимые параметры
        ExperimentResponse response = simulation.countResults();
        System.out.println(response.toString());


        architectureFX.setExperimentResponse(response);
        architectureFX.setEventResponse(simulation.getEventResponse());
        architectureFX.setGraphicsResponse(simulation.getInfoForGraphics());
        architectureFX.showResultWindow();
    }

    private static double countAccuracy(double p0, double p1) {
        return Math.abs(p0 - p1) / p0;
    }
}
