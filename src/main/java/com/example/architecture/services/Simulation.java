package com.example.architecture.services;

import com.example.architecture.messages.EventResponse;
import com.example.architecture.messages.ExperimentParamsRequest;
import com.example.architecture.messages.ExperimentResponse;
import com.example.architecture.messages.GraphicsResponse;
import com.example.architecture.model.Coordinate;
import com.example.architecture.model.Event;
import com.example.architecture.utils.Buffer;
import com.example.architecture.utils.Device;
import com.example.architecture.utils.Source;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Simulation {

    @AllArgsConstructor
    static class TimeAction implements Comparable<TimeAction> {
        Double time;
        Integer action;
        int toolNum;

        @Override
        public int compareTo(TimeAction o) {
            if (this.time.compareTo(o.time) == 0) {
                return -this.action.compareTo(o.action);
            }
            return this.time.compareTo(o.time);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);
    Queue<TimeAction> timeLine;

    double currentTime;
    int count, countRejection;

    List<Coordinate> rejection;

    Double p0;
    Integer N0;
    Buffer buffer;
    Device device;
    Source source;

    EventResponse eventResponse;

    String newReq = "Новая заявка. ";
    String bufReq = "Заявка из буфера. ";

    String reject = "Отказ заявки. ";
    String flushDev = "Заявка обработана. Освобождаем прибор. ";
    String whereDev = "Отправляем на прибор. ";
    String whereBuf = "Отправляем в буфер. ";


    public void startWork(ExperimentParamsRequest request) {
        buffer = new Buffer(request.getBufferNum(), 25);

        device = new Device(request.getDeviceNum(), request.getSourceNum(),
                    buffer.startHeight + request.getBufferNum() * 10 + 20,
                            request.getAlpha(), request.getBeta());
        source = new Source(request.getSourceNum(), device.startHeight + request.getDeviceNum() * 10 + 20,
                            request.getLambda());
        timeLine = new PriorityQueue<>();
        N0 = request.getEventsNum();
        eventResponse = new EventResponse();
        rejection = new ArrayList<>();
        rejection.add(new Coordinate(0, 5));
        count = 0;
        currentTime = 0;
        countRejection = 0;

        //запуск источников
        for (; count < request.getSourceNum(); count++) {
            timeLine.add(new TimeAction(currentTime, 0, count));
            currentTime += 2;
        }

        while (!timeLine.isEmpty()) {
            logger.info("итерация: " + count);
            TimeAction timeAction = timeLine.poll();
            assert timeAction != null;
            currentTime = timeAction.time;
            logger.info("event: " + timeAction.action);
            switch (timeAction.action) {
                case 0 ->  //создать новую заявку и отправить куда-нибудь (возможно выбить из буфера)
                        addNewRequest(timeAction);
                case 1 -> // освободить один прибор(если есть занятый) и отправить на него заявку из буфера (если есть)
                        completeRequest(timeAction);
            }
        }
        System.out.println("count: " + count);
        System.out.println("reject count: " + countRejection);
        p0 = calculateP0();
    }



    private void addNewRequest(TimeAction timeAction) {
        logger.info("создаем новую заявку");
        double req = source.createRequest(timeAction.toolNum, currentTime);
        count++;
        double time;
        if (count <= N0) {
            time = source.deltaTimePoisson();
            timeLine.add(new TimeAction(currentTime + time, 0, timeAction.toolNum));
        }

        if (device.hasEmpty()) {
            logger.info("добавляем заявку сразу на прибор");
            int num = device.setSource(req, currentTime);
            time = device.calculateDeltaTimeEvenly();

            source.sumWaitTime(timeAction.toolNum, 0);
            source.sumWorkTime(timeAction.toolNum, time);

            device.sumWorkTime(num, timeAction.toolNum, time);
            eventResponse.add(new Event("И" + timeAction.toolNum, "П" + num, null, req, currentTime,
                                    newReq + whereDev, count, countRejection));
            timeLine.add(new TimeAction(currentTime + time, 1, num));
        } else {
            if (!buffer.hasEmpty()) {
                countRejection++;
                addRejectCoordinates();
                logger.info("очищаем место в буфере (выбираем самую старую заявку)"); //суммировать буферное время
                double oldestReq = buffer.getOldestRequest();
                int num = buffer.flush(currentTime);
                eventResponse.add(new Event(null, null, "Б" + num, oldestReq, currentTime, reject,
                                            count, countRejection));
            }
            logger.info("добавляем заявку в буфер");
            int num = buffer.setSource(req, currentTime);
            eventResponse.add(new Event("И" + timeAction.toolNum, null, "Б" + num, req, currentTime,
                                newReq + whereBuf, count, countRejection));

        }
    }

    private void addRejectCoordinates() {
        rejection.add(new Coordinate(currentTime, 5));
        rejection.add(new Coordinate(currentTime, 10));
        rejection.add(new Coordinate(currentTime, 5));
    }

    private void completeRequest(TimeAction timeAction) { //номер прибора
        if (!device.isEmpty()) {
            logger.info("освобождаем прибор");
            double oldReq = device.flush(timeAction.toolNum, currentTime);
            eventResponse.add(new Event(null, "П" + timeAction.toolNum, null, oldReq, currentTime,
                                    flushDev, count, countRejection));
        }

        if (!buffer.isEmpty()) {
            Integer bufNum = buffer.getNextIndex();
            if (bufNum == null) {
                logger.warn("result - " + buffer.isEmpty() + "\n array = " + Arrays.toString(buffer.array));
                return;
            }
            Double d = buffer.chooseRequest(bufNum, currentTime);
            if (d != null) {
                logger.info("добавляем на прибор заявку из буфера");
                int num = device.setSource(d, currentTime);
                int index = source.getNumSource(d);
                double time = device.calculateDeltaTimeEvenly();

                source.sumWorkTime(index, time);
                source.sumWaitTime(index, currentTime - source.startTime[index]);
                device.sumWorkTime(timeAction.toolNum, index, time);

                eventResponse.add(new Event(null, "П" + num, "Б" + bufNum, d, currentTime,
                                        bufReq + whereDev, count, countRejection));
                timeLine.add(new TimeAction(currentTime + time, 1, timeAction.toolNum));
            }
        }
    }


    double ta = 1.643;
    double si = 0.1;

    private double calculateP0() {
        return (double) countRejection / N0;
    }

    public int calculateN0() {
        return (int) ((Math.pow(ta, 2) * (1 - p0)) / (p0 * Math.pow(si, 2)));
    }


    public ExperimentResponse countResults() {
        if (source == null) {
            return null;
        }
        //1. кол-во заявок от каждого источника                     (посчитано)
        //2. вероятность отказа Р_отк                               (посчитано)
        //3. ср время пребывания в системе Т_преб = Т_БП + Т_обсл   (посчитано)
        //4. ср время ожидания Т_БП                                 (посчитано)
        //5. сп время обслуживания заявки Т_обсл                    (посчитано)
        //6. дисперсия Д_БП                                         (посчитано)
        //7. дисперсия Д_обсл                                       (посчитано)

        double[][] resultSource = new double[source.size()][7];
        for (int i = 0; i < resultSource.length; i++) {
            resultSource[i][0] = source.countRequests[i];
            resultSource[i][1] = (source.countRequests[i] - source.timeRequestsBySource.get(i).size()) / resultSource[i][0];
            if (resultSource[i][1] == 1) {
                resultSource[i][3] = 0;
                resultSource[i][4] = 0;
                resultSource[i][2] = 0;
                resultSource[i][5] = 0;
                resultSource[i][6] = 0;
                continue;
            }

            resultSource[i][3] = source.waitTime[i] / source.timeRequestsBySource.get(i).size(); //время ожидания может быть 0
            resultSource[i][4] = source.workTime[i] / source.timeRequestsBySource.get(i).size();
            resultSource[i][2] = resultSource[i][3] + resultSource[i][4];

            //считаем дисперсию
            double s_BP = resultSource[i][3];
            double s_obsl = resultSource[i][4];

            double d_BP = 0;
            double d_obsl = 0;


            for (double j : source.timeRequestsBySource.get(i)) {
                d_BP += Math.pow((j - s_BP), 2);
            }
            resultSource[i][5] = d_BP / resultSource[i][0];

            for (double j : device.timeRequestsByDevice.get(i)) {
                d_obsl += Math.pow((j - s_obsl), 2);
            }
            resultSource[i][6] = d_obsl / device.timeRequestsByDevice.get(i).size();
        }

        // коэф использования устройств К_исп
        // (время работы каждого прибора / время реализации)        (посчитано)
        double[] resultDevice = new double[device.size()];
        for (int i = 0; i < resultDevice.length; i++) {
            resultDevice[i] = device.workTime[i] / currentTime;
        }
        return new ExperimentResponse(resultSource, resultDevice, N0, p0, buffer.size(), device.getAlpha(), device.getBeta(), source.getLm());
    }


    public GraphicsResponse getInfoForGraphics() {
        return new GraphicsResponse(currentTime, source.coordinates, device.coordinates, buffer.coordinates, rejection);
    }
}
