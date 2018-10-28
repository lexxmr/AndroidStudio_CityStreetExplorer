package com.example.me.myproject;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Me on 2018/6/12.
 */

public class Edge extends DefaultWeightedEdge implements Comparable<Edge>{
    private String label;
    private int visitTimes;
    private GeoPoint center;
    private double length;
    private Node sNode;
    private Node tNode;
    public List<Edge> subEdges;

    public void setsNode(Node sNode){
        this.sNode = sNode;
    }

    public Node getsNode() {
        return sNode;
    }

    public void settNode(Node tNode) {
        this.tNode = tNode;
    }

    public Node gettNode() {
        return tNode;
    }

    public void setLength(double length){ this.length = length; }

    public double getLength(){ return length; }

    public void setCenter(GeoPoint center){ this.center = center; }

    public GeoPoint getCenter(){ return center; }

    public void setVisitTimes(int visitTimes) {
        this.visitTimes = visitTimes;
    }

    public int getVisitTimes() {
        return visitTimes;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public double getWeight(){
        return super.getWeight();
    }

    public Node getSource(){
        return (Node)super.getSource();
    }

    public  Node getTarget(){
        return (Node)super.getTarget();
    }

    public int hashCode()
    {
        return label.hashCode();
    }

    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Edge)) {
            return false;
        }

        Edge edge = (Edge) obj;
        return label.equals(edge.label);
    }

    @Override
    public int compareTo(Edge e){
        return Integer.valueOf(this.getVisitTimes()).compareTo(Integer.valueOf(e.getVisitTimes()));
    }
}
