package com.example.architecture;

import com.example.architecture.messages.ExperimentParamsRequest;
import com.example.architecture.services.Simulation;

public class ArchitectureApp {

    public static void main(String[] args) {
        Simulation simulation = new Simulation();

        int N0 = 5000, source = 4, device = 5, buff = 6;
        double p0 = 2, p1 = 1;

        while (countAccuracy(p0, p1) >= 0.1) {
            simulation.startWork(new ExperimentParamsRequest(source, device, buff, N0, 1, 1.1, 0.275));
            p0 = simulation.getP0();
            N0 = simulation.calculateN0();
            simulation.startWork(new ExperimentParamsRequest(source, device, buff, N0, 1, 1.1, 0.275));
            N0 = simulation.getN0();
            p1 = simulation.getP0();
         }

        System.out.println("N = " + N0 + "; p = " + p0 + "; p1 = " + p1);
        System.out.println(simulation.countResults().toString());
    }

    private static double countAccuracy(double p0, double p1) {
        return Math.abs(p0 - p1) / p0;
    }

}
