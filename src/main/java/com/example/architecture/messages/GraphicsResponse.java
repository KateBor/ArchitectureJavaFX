package com.example.architecture.messages;

import com.example.architecture.model.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class GraphicsResponse {
    double currentTime;
    public Map<Integer, List<Coordinate>> coordinatesSource;
    public Map<Integer, List<Coordinate>> coordinatesDevice;
    public Map<Integer, List<Coordinate>> coordinatesBuffer;
    public List<Coordinate> rejection;
}
