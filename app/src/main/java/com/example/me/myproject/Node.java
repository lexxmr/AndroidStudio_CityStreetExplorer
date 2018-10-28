package com.example.me.myproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Me on 2018/6/12.
 */

public class Node {
    private Long nodeId;
    private double lat;
    private double lon;
    public List<Edge> connections = new ArrayList<Edge>();
    public List<Edge> largeEdgeConnections = new ArrayList<Edge>();

    public void setNodeId(Long nodeId){
        this.nodeId = nodeId;
    }

    public void setLat(Double lat){
        this.lat = lat;
    }

    public void setLon(Double lon){
        this.lon = lon;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

}
