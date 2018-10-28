package com.example.me.myproject;

import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.MAX_VALUE;

/**
 * Created by Me on 2018/6/12.
 */

public class MyGraph {
    public LongSparseArray<Node> nodeArray = new LongSparseArray<Node>();
    public List<Edge> edgeList = new ArrayList<Edge>();
    public LongSparseArray<Node> originalNodeArray = new LongSparseArray<Node>();
    public List<Edge> originalEdgeList = new ArrayList<Edge>();

    public void resetVisited(){
        for(Edge edge:edgeList){
            edge.setVisitTimes(0);
        }
    }

    public Node findNearestNode(double lat, double lon){
        Long nodeId = new Long(1);
        Double minDistance = MAX_VALUE;
        for(int i=0; i<nodeArray.size(); i++){
            Node node = nodeArray.valueAt(i);
            double distance = LocationUtils.getDistance(node.getLat(), node.getLon(), lat, lon);
            if(distance < minDistance){
                minDistance = distance;
                nodeId = node.getNodeId();
            }
        }
        return nodeArray.get(nodeId);
    }
}
