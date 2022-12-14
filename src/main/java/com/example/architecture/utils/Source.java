package com.example.architecture.utils;

import com.example.architecture.model.Coordinate;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Source {
    public int[] countRequests;
    public double[] workTime;
    public double[] waitTime;
    public double[] startTime;
    public Map<Integer, List<Double>> timeRequestsBySource;
    public Map<Integer, List<Coordinate>> coordinates;
    double startHeight;
    double deltaY = 5;
    double lm;


    public Source(int sourceNum, double startHeight, double lambda) {
        countRequests = new int[sourceNum];
        workTime = new double[sourceNum];
        waitTime = new double[sourceNum];
        startTime = new double[sourceNum];
        coordinates = new HashMap<>();
        lm = lambda;
        this.startHeight = startHeight;

        timeRequestsBySource = new HashMap<>();
        for (int i = 0; i < sourceNum; i++) {
            timeRequestsBySource.put(i, new ArrayList<>());
            coordinates.put(i, new ArrayList<>());
            coordinates.get(i).add(new Coordinate(0, startHeight + i * 10));
        }
    }

    public double createRequest(int sourceNumber, double currentTime) {
        double requestNumber = ++countRequests[sourceNumber];
        while (requestNumber >= 1) {
            requestNumber /= 10;
        }
        addCoordinates(sourceNumber, currentTime);
        startTime[sourceNumber] = currentTime;
        return (double) sourceNumber + requestNumber;
    }

    public double deltaTimePoisson() { // должен генерировать в среднем раз в 2.5 минуты
        return -1 / lm * Math.log(Math.random());// r -> [0; 1]
    }

    public int size() {
        return countRequests.length;
    }

    public void sumWorkTime(int toolNum, double time) {
        workTime[toolNum] += time;
    }

    public int getNumSource(double d) {
        return (int) d;
    }


    public void sumWaitTime(int toolNum, double time) {
        waitTime[toolNum] += time;
        timeRequestsBySource.get(toolNum).add(time);
    }

    public void addCoordinates(int source, double currentTime) {
        coordinates.get(source).add(new Coordinate(currentTime, startHeight + source * 10));
        coordinates.get(source).add(new Coordinate(currentTime, startHeight + source * 10 + deltaY));
        coordinates.get(source).add(new Coordinate(currentTime, startHeight + source * 10));
    }
}
