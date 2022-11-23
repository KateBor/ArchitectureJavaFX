package com.example.architecture;

import com.example.architecture.messages.ExperimentParamsRequest;
import com.example.architecture.messages.ExperimentResponse;
import com.example.architecture.services.Simulation;

import java.io.FileWriter;
import java.io.IOException;

public class ArchitectureApp {

    public static void main(String[] args) throws IOException {

        int[] devArr = new int[]{
                152, 267, 224
        };
        int bufMin = 10, bufMax = 200;
        int devMin = 4, devMax = 270;
        int buf = bufMin;
        double alpha = 2, beta = 2, lambda = 0.0025;
        FileWriter writer0 = new FileWriter("src/small.txt", true);
        int src = 50000;
        int N0 = src * 100;
        for (int i = 0; i < 3; i++) {
            int countAns = 0;
            int dev = devArr[i];
            for (; buf < bufMax && countAns < 3; buf++) {
                Simulation simulation = new Simulation();
                simulation.startWork(new ExperimentParamsRequest(src, dev, buf, N0, alpha, beta, lambda));
                double p0 = simulation.getP0();
                ExperimentResponse resp = simulation.countResults();
                if (p0 <= 0.1) {
                    //записывать каждый раз
                    countAns++;
                    String ans = src + "; " + dev + "; " + buf + "; " + p0 + "; " +
                            resp.getTpreb() + "; " + resp.getKisp() + ";\n";
                    writer0.write(ans);
                    writer0.flush();
                    System.out.println("+");
                } else if (p0 > 0.15) {
                    break;
                }
            }
            buf = bufMin;
        }
    }

    private static double countAccuracy(double p0, double p1) {
        return Math.abs(p0 - p1) / p0;
    }
}
