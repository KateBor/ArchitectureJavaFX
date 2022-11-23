package com.example.architecture.utils;

import com.example.architecture.model.Coordinate;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Device {
    public Map<Integer, List<Double>> timeRequestsByDevice;
    public double startHeight;
    public Map<Integer, List<Coordinate>> coordinates;
    double[] array;
    public double[] workTime;
    private static final Logger logger = LoggerFactory.getLogger(Device.class);
    double deltaY = 5;
    double alpha;
    double beta;


    public Device(int deviceNum, int sourceNum, double startHeight, double alpha, double beta) {
        array = new double[deviceNum];
        workTime = new double[deviceNum];
        this.alpha = alpha;
        this.beta = beta;
        this.startHeight = startHeight;
        coordinates = new HashMap<>();
        timeRequestsByDevice = new HashMap<>();

        for (int i = 0; i < deviceNum; i++) {
            coordinates.put(i, new ArrayList<>());
            coordinates.get(i).add(new Coordinate(0, startHeight + i * 10));
        }


        for (int i = 0; i < sourceNum; i++) {
            timeRequestsByDevice.put(i, new ArrayList<>());
        }
    }

    public boolean hasEmpty() {
        return findFirstEmptyIndex() != null;
    }

    public boolean isEmpty() {
        for (double d : array) {
            if (d != 0) {
                return false;
            }
        }
        return true;
    }

    public Integer findFirstEmptyIndex() {
        Integer index = null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int setSource(double request, double currentTime) {
        Integer emptyIndex;                                                 //целая часть - номер источника,
        if ((emptyIndex = findFirstEmptyIndex()) != null) {                 //дробная - номер заявки (пакета)
            array[emptyIndex] = request;
            addSetCoordinates(emptyIndex, currentTime);
        } else {
            logger.warn("NoEmptyDeviceException: Can't set source");
            return -1;
        }
        return emptyIndex;
    }

    private void addSetCoordinates(int index, double currentTime) {
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10));
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10 + deltaY));
    }


    private void addFlushCoordinates(int index, double currentTime) {
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10 + deltaY));
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10));
    }


    public double flush(int deviceNumber, double currentTime) {
        double req = array[deviceNumber];
        array[deviceNumber] = 0;
        addFlushCoordinates(deviceNumber, currentTime);
        return req;
    }


    public double calculateDeltaTimeEvenly() {
        return Math.random()*(beta - alpha) + alpha;
    }

    public int size() {
        return array.length;
    }

    public void sumWorkTime(int devNum, int srcNum, double time) {
        workTime[devNum] += time;
        timeRequestsByDevice.get(srcNum).add(time);
    }
}
