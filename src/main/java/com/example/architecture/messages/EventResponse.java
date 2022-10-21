package com.example.architecture.messages;

import com.example.architecture.model.Event;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventResponse {
    List<Event> eventList;

    public EventResponse() {
        eventList = new ArrayList<>();
    }

    public void add(Event event) {
        eventList.add(event);
    }
}
