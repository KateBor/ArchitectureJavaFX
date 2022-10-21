package com.example.architecture.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {
    String source;
    String device;
    String buffer;
    double reqNum;
    double time;
    String event;
    int countReq;
    int countReject;
}
