package com.example.me.myproject;

import android.util.Log;

import com.google.common.collect.Lists;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Me on 2018/6/12.
 */

public class GraphParser {
    WeightedPseudograph<Node, Edge> finalGraph;
    WeightedPseudograph<Node, Edge> graph = new WeightedPseudograph<Node, Edge>(Edge.class);;
    SimpleWeightedGraph<Node, Edge> tempGraph = new SimpleWeightedGraph<Node, Edge>(Edge.class);
    MyGraph finalMyGraph;
    MyGraph myGraph = new MyGraph();
    MyGraph tempMyGraph = new MyGraph();

    public GraphParser(WeightedPseudograph<Node, Edge> graph, MyGraph myGraph){
        this.graph = graph;
        this.myGraph = myGraph;
    }

    public void parse(String data){
        Pattern nodePattern = Pattern.compile("<node id=\"(\\d*)\" lat=\"(-?\\d*.?\\d*)\" lon=\"(-?\\d*.?\\d*)\"\\/?>");
        Pattern wayPattern = Pattern.compile("<way id=\"(\\d*)\">(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?(?:\\s*<nd ref=\"(\\d*)\"\\/>\\s*)?");
        Matcher nodeMatcher = nodePattern.matcher(data);
        Matcher wayMatcher = wayPattern.matcher(data);

        while (nodeMatcher.find()) {
            Node node = new Node();
            node.setNodeId(Long.parseLong(nodeMatcher.group(1)));
            node.setLat(Double.parseDouble(nodeMatcher.group(2)));
            node.setLon(Double.parseDouble(nodeMatcher.group(3)));
            tempGraph.addVertex(node);
            tempMyGraph.nodeArray.put(Long.parseLong(nodeMatcher.group(1)), node);

        }

        while (wayMatcher.find()){
            for(int i = 2; i < wayMatcher.groupCount(); i++){
                if(wayMatcher.group(i+1)==null)
                    break;
                Edge edge = new Edge();
                Node sourceVertex = tempMyGraph.nodeArray.get(Long.parseLong(wayMatcher.group(i)));
                Node targetVertex = tempMyGraph.nodeArray.get(Long.parseLong(wayMatcher.group(i+1)));
                edge.setLabel(wayMatcher.group(1)+","+ (i-2));
                edge.setCenter(new GeoPoint((sourceVertex.getLat()+targetVertex.getLat())/2, (sourceVertex.getLon()+targetVertex.getLon())/2));
                tempGraph.addEdge(sourceVertex, targetVertex, edge);
                tempGraph.setEdgeWeight(edge, LocationUtils.getDistance(sourceVertex.getLat(), sourceVertex.getLon(), targetVertex.getLat(), targetVertex.getLon()));
                edge.setLength(LocationUtils.getDistance(sourceVertex.getLat(), sourceVertex.getLon(), targetVertex.getLat(), targetVertex.getLon()));


                if(edge.getSource()!=null&&edge.getTarget()!=null) {
                    tempMyGraph.edgeList.add(edge);
                    sourceVertex.connections.add(edge);
                    targetVertex.connections.add(edge);
                }


            }
        }


        GraphUtils.deleteIndependentNE(new GeoPoint( 55.9444562, -3.1877047), tempGraph, tempMyGraph);

        Edge edge = new Edge();
        Edge tempEdge = new Edge();
        Node tempSource = new Node();
        for(int i=0; i<tempMyGraph.edgeList.size(); i++){
            if(tempMyGraph.edgeList.get(i).getLabel().split(",")[1].equals("0")){
                if(tempEdge.getTarget()!=null){
                    myGraph.edgeList.add(edge);
                    graph.addVertex(tempEdge.getTarget());
                    graph.addEdge(tempSource, tempEdge.getTarget(), edge);
                    graph.setEdgeWeight(edge, edge.getLength());
                    edge.setCenter(new GeoPoint((edge.getSource().getLat()+edge.getTarget().getLat())/2, (edge.getSource().getLon()+edge.getTarget().getLon())/2));
                    tempEdge = new Edge();
                }
                tempEdge = tempMyGraph.edgeList.get(i);
                edge = new Edge();
                edge.subEdges = new ArrayList<Edge>();
                edge.setLabel(tempEdge.getLabel());
                edge.subEdges.add(tempEdge);
                edge.setLength(tempEdge.getLength());
                graph.addVertex(tempEdge.getSource());
                tempSource = tempEdge.getSource();
            }
            else {
                if(tempMyGraph.edgeList.get(i).getSource().connections.size()>2) {
                    if (tempEdge.getTarget() != null) {
                        myGraph.edgeList.add(edge);
                        graph.addVertex(tempEdge.getTarget());
                        graph.addEdge(tempSource, tempEdge.getTarget(), edge);
                        graph.setEdgeWeight(edge, edge.getLength());
                        edge.setCenter(new GeoPoint((edge.getSource().getLat() + edge.getTarget().getLat()) / 2, (edge.getSource().getLon() + edge.getTarget().getLon()) / 2));
                        tempEdge = new Edge();
                    }
                    tempEdge = tempMyGraph.edgeList.get(i);
                    edge = new Edge();
                    edge.subEdges = new ArrayList<Edge>();
                    edge.setLabel(tempEdge.getLabel());
                    edge.subEdges.add(tempEdge);
                    edge.setLength(tempEdge.getLength());
                    graph.addVertex(tempEdge.getSource());
                    tempSource = tempEdge.getSource();
                }
                else{
                    tempEdge = tempMyGraph.edgeList.get(i);
                    edge.subEdges.add(tempEdge);
                    edge.setLength(edge.getLength() + tempEdge.getLength());
                }


            }
        }

        Node n;
        for(Edge edge1:graph.edgeSet()){
            n=null;
            n=myGraph.nodeArray.get(edge1.getSource().getNodeId());
            edge1.getSource().largeEdgeConnections.add(edge1);
            if(n==null){
                myGraph.nodeArray.append(edge1.getSource().getNodeId(), edge1.getSource());
            }
            n=myGraph.nodeArray.get(edge1.getTarget().getNodeId());
            edge1.getTarget().largeEdgeConnections.add(edge1);
            if(n==null){
                myGraph.nodeArray.append(edge1.getTarget().getNodeId(), edge1.getTarget());
            }
        }


        List<Node> nodes = new ArrayList<Node>();
        for(int i=0; i<myGraph.nodeArray.size(); i++){
            if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.size()==2){
                nodes.add(myGraph.nodeArray.valueAt(i));
                Node node = myGraph.nodeArray.valueAt(i);
                Edge newEdge = new Edge();
                Node n1;
                Node n2;
                newEdge.subEdges = new ArrayList<Edge>();


                if(i==28){
                    Log.d("","");
                }
                if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getTarget()==myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getSource()) {


                    newEdge.subEdges.addAll(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).subEdges);

                    newEdge.subEdges.addAll(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).subEdges);

                }
                else if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getSource()==myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getSource()){
                    newEdge.subEdges.addAll(Lists.reverse(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).subEdges));
                    newEdge.subEdges.addAll(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).subEdges);
                }
                else if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getSource()==myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getTarget()){

                    newEdge.subEdges.addAll(Lists.reverse(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).subEdges));
                    newEdge.subEdges.addAll(Lists.reverse(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).subEdges));
                }
                else {
                    newEdge.subEdges.addAll(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).subEdges);
                    newEdge.subEdges.addAll(Lists.reverse(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).subEdges));
                }
                newEdge.setLength(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getLength()+myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getLength());
                newEdge.setLabel(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getLabel());
                graph.removeEdge(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0));
                graph.removeEdge(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1));
                myGraph.edgeList.remove(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0));
                myGraph.edgeList.remove(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1));
                if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getSource()==myGraph.nodeArray.valueAt(i)){
                    n1 = myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getTarget();
                }
                else {
                    n1 = myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0).getSource();
                }

                if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getSource()==myGraph.nodeArray.valueAt(i)){
                    n2 = myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getTarget();
                }
                else {
                    n2 = myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1).getSource();
                }

                n1.largeEdgeConnections.remove(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(0));
                n1.largeEdgeConnections.add(newEdge);
                n2.largeEdgeConnections.remove(myGraph.nodeArray.valueAt(i).largeEdgeConnections.get(1));
                n2.largeEdgeConnections.add(newEdge);

                graph.addEdge(n1, n2, newEdge);
                graph.setEdgeWeight(newEdge, newEdge.getLength());
                newEdge.setCenter(new GeoPoint((n1.getLat()+n2.getLat())/2, (n1.getLon()+n2.getLon())/2));
                myGraph.edgeList.add(newEdge);

                Node tempNode = new Node();
                if(newEdge.subEdges.get(0).getSource() == newEdge.subEdges.get(1).getTarget()) {
                    tempNode = newEdge.subEdges.get(0).getSource();
                }
                else if(newEdge.subEdges.get(0).getSource() == newEdge.subEdges.get(1).getSource()){
                    tempNode = newEdge.subEdges.get(0).getSource();
                }
                else if(newEdge.subEdges.get(0).getTarget() == newEdge.subEdges.get(1).getSource()){
                    tempNode = newEdge.subEdges.get(0).getTarget();
                }
                else if(newEdge.subEdges.get(0).getTarget() == newEdge.subEdges.get(1).getTarget()){
                    tempNode = newEdge.subEdges.get(0).getTarget();
                }
                else{
                    tempNode = new Node();
                    Log.d("path to nodes error 1","1");
                }

                for(int j=1; j<newEdge.subEdges.size(); j++){
                    if(newEdge.subEdges.get(j).getSource()==tempNode){
                        tempNode = newEdge.subEdges.get(j).getTarget();
                    }
                    else if(newEdge.subEdges.get(j).getTarget()==tempNode){
                        tempNode = newEdge.subEdges.get(j).getSource();
                    }
                    else {
                        Log.d("","rrrr");
                    }
                }
            }

        }


        for(int i=0; i<nodes.size(); i++){
            myGraph.nodeArray.delete(nodes.get(i).getNodeId());
            graph.removeVertex(nodes.get(i));
        }


    }

}
