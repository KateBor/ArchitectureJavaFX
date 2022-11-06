package com.example.architecture.utils;

import com.example.architecture.model.Coordinate;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Buffer {

    @AllArgsConstructor
    static class Pair {
        double value; //номер заявки
        int index; //номер ячейки буфера
    }

    public double[] array;
    Queue<Pair> queue;
    Set<Double> set;
    double deltaY = 5;

    private static final Logger logger = LoggerFactory.getLogger(Buffer.class);

    int choosingIterator = -1;
    int insertingIterator = -1;
    public double startHeight;
    public Map<Integer, List<Coordinate>> coordinates;

    public Buffer(int bufferNum, double startHeight) {
        array = new double[bufferNum];
        queue = new LinkedList<>();
        set = new HashSet<>();
        this.startHeight = startHeight;
        coordinates = new HashMap<>();

        for(int i = 0; i < bufferNum; i++) {
            coordinates.put(i, new ArrayList<>());
            coordinates.get(i).add(new Coordinate(0, startHeight + i * 10));
        }
    }

    public boolean hasEmpty() {
        return set.size() < array.length;
    }

    public int setSource(double request, double currentTime) {
        insertingIterator++;
        for (int i = 0; i < array.length; i++) {
            if (insertingIterator >= array.length) {
                insertingIterator = 0;
            }

            if (array[insertingIterator] == 0) {
                array[insertingIterator] = request;
                set.add(request);
                queue.add(new Pair(request, insertingIterator));
                addSetCoordinates(insertingIterator, currentTime);
                return insertingIterator;
            }
            insertingIterator++;
        }
        logger.warn("FullBufferException: Can't set source");
        return -1;
    }

    public int flush(double currentTime) { //отказ (самый старый в буфере)
        if (set.isEmpty()) {
            logger.warn("EmptyBufferException: Can't flush anything");
        }
        while (!queue.isEmpty() && !set.contains(queue.peek().value)) {
            queue.poll(); //удаляем уже невалидные данные
        }
        Pair pair = queue.poll();
        assert pair != null;
        array[pair.index] = 0;
        set.remove(pair.value);
        addFlushCoordinates(pair.index, currentTime);
        return pair.index;
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public Integer getNextIndex() {
        choosingIterator++;
        for (int i = 0; i < array.length; i++) {
            if (choosingIterator >= array.length) {
                choosingIterator = 0;
            }
            if (array[choosingIterator] != 0) {
                return choosingIterator;
            }
            choosingIterator++;
        }
        return null;
    }

    public Double chooseRequest(int index, double currentTime) { //по кольцу
        Double d = array[index];
        set.remove(d);
        array[index] = 0;
        addFlushCoordinates(index, currentTime);
        return d;
    }

    private void addSetCoordinates(int index, double currentTime) {
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10));
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10 + deltaY));
    }

    private void addFlushCoordinates(int index, double currentTime) {
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10 + deltaY));
        coordinates.get(index).add(new Coordinate(currentTime, startHeight + index * 10));
    }


    public Double getOldestRequest() {
        if (set.isEmpty()) {
            logger.warn("EmptyBufferException: Can't flush anything");
        }
        while (!queue.isEmpty() && !set.contains(queue.peek().value)) {
            queue.poll(); //удаляем уже невалидные данные
        }
        assert queue.peek() != null;
        return queue.peek().value;
    }

    public int size() {
        return array.length;
    }
}
