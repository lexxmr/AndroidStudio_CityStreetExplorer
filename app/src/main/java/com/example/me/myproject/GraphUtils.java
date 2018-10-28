package com.example.me.myproject;

import android.util.Log;

import com.google.common.collect.Lists;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Double.MAX_VALUE;

/**
 * Created by Me on 2018/6/12.
 */

public class GraphUtils {

//    public static List<Node> edgesToNodes(List<Edge> edges, Node startNode, Node endNode){
//        List<Node> nodes = new ArrayList<Node>();
//        Node tempNode;
//        Edge preEdge;
//        Node tNode;
//
//        if(edges == null){
//            return nodes;
//        }
//
//        if(edges.size() == 0){
//            return nodes;
//        }
//
//        if(edges.size() == 1){
//            nodes.add(startNode);
//            nodes.add(endNode);
//            return nodes;
//        }
//
//        if(edges.get(0).getSource() == edges.get(1).getTarget()) {
//            nodes.add(edges.get(0).getTarget());
//            tNode = edges.get(0).getTarget();
//            for(Edge e: Lists.reverse(edges.get(0).subEdges)){
//                if(e.getTarget()==tNode){
//                    nodes.add(e.getSource());
//                    tNode = e.getSource();
//                }
//                else {
//                    nodes.add(e.getTarget());
//                    tNode = e.getTarget();
//                }
//            }
//            nodes.add(edges.get(1).getSource());
//            tempNode = edges.get(1).getSource();
//        }
//        else if(edges.get(0).getSource() == edges.get(1).getSource()){
//            nodes.add(edges.get(0).getTarget());
//            tNode = edges.get(0).getTarget();
//            for(Edge e: Lists.reverse(edges.get(0).subEdges)){
//                if(e.getTarget()==tNode){
//                    nodes.add(e.getSource());
//                    tNode = e.getSource();
//                }
//                else {
//                    nodes.add(e.getTarget());
//                    tNode = e.getTarget();
//                }
//            }
//            nodes.add(edges.get(1).getTarget());
//            tempNode = edges.get(1).getTarget();
//        }
//        else if(edges.get(0).getTarget() == edges.get(1).getSource()){
//            nodes.add(edges.get(0).getSource());
//            tNode = edges.get(0).getSource();
//            for(Edge e: edges.get(0).subEdges){
//                if(e.getTarget()==tNode){
//                    nodes.add(e.getSource());
//                    tNode = e.getSource();
//                }
//                else {
//                    nodes.add(e.getTarget());
//                    tNode = e.getTarget();
//                }
//            }
//            nodes.add(edges.get(1).getTarget());
//            tempNode = edges.get(1).getTarget();
//        }
//        else if(edges.get(0).getTarget() == edges.get(1).getTarget()){
//            nodes.add(edges.get(0).getSource());
//            tNode = edges.get(0).getSource();
//            for(Edge e: edges.get(0).subEdges){
//                if(e.getTarget()==tNode){
//                    nodes.add(e.getSource());
//                    tNode = e.getSource();
//                }
//                else {
//                    nodes.add(e.getTarget());
//                    tNode = e.getTarget();
//                }
//            }
//            nodes.add(edges.get(1).getSource());
//            tempNode = edges.get(1).getSource();
//        }
//        else{
//            tempNode = new Node();
//            Log.d("path to nodes error 1","1");
//        }
//        preEdge = edges.get(1);
//
//        int i = 0;
//        for (Edge edge: edges) {
//            if(i>=2){
//                if(edge.getTarget() == tempNode){
//                    tNode = edge.getTarget();
//                    for(Edge e: Lists.reverse(edge.subEdges)){
//                        if(e.getTarget()==tNode){
//                            nodes.add(e.getSource());
//                            tNode = e.getSource();
//                        }
//                        else {
//                            nodes.add(e.getTarget());
//                            tNode = e.getTarget();
//                        }
//                    }
//                    tempNode = edge.getSource();
//                    preEdge = edge;
//                }
//                else if(edge.getSource() == tempNode){
//                    tNode = edge.getSource();
//                    for(Edge e: edge.subEdges){
//                        if(e.getTarget()==tNode){
//                            nodes.add(e.getSource());
//                            tNode = e.getSource();
//                        }
//                        else {
//                            nodes.add(e.getTarget());
//                            tNode = e.getTarget();
//                        }
//                    }
//                    tempNode = edge.getTarget();
//                    preEdge = edge;
//                }
//                else {
//                    Log.d("path to nodes error 2","" + edge.getLabel() + "," + preEdge.getLabel());
//                }
//            }
//            i++;
//        }
//        return nodes;
//    }

    public static List<Node> edgesToNodes(List<Edge> edges, Node startNode, Node endNode){
        List<Edge> edgeList = new ArrayList<Edge>();
        List<Node> nodes = new ArrayList<Node>();
        Node tempNode;
        Edge preEdge;

        if(edges == null){
            return nodes;
        }

        if(edges.size() == 0){
            return nodes;
        }

        if(edges.size() == 1){
            tempNode=startNode;
            nodes.add(tempNode);
            for(Edge edge:edges.get(0).subEdges){
                if(edge.getTarget()==tempNode){
                    tempNode=edge.getSource();
                }else {
                    tempNode=edge.getTarget();
                }
                nodes.add(tempNode);
            }
            return nodes;
        }

        if(edges.get(0).getSource() == edges.get(1).getTarget()) {
            edgeList.addAll(Lists.reverse(edges.get(0).subEdges));
            edgeList.addAll(Lists.reverse(edges.get(1).subEdges));
            tempNode = edges.get(1).getSource();
        }
        else if(edges.get(0).getSource() == edges.get(1).getSource()){
            edgeList.addAll(Lists.reverse(edges.get(0).subEdges));
            edgeList.addAll(edges.get(1).subEdges);
            tempNode = edges.get(1).getTarget();
        }
        else if(edges.get(0).getTarget() == edges.get(1).getSource()){
            edgeList.addAll(edges.get(0).subEdges);
            edgeList.addAll(edges.get(1).subEdges);
            tempNode = edges.get(1).getTarget();
        }
        else if(edges.get(0).getTarget() == edges.get(1).getTarget()){
            edgeList.addAll(edges.get(0).subEdges);
            edgeList.addAll(Lists.reverse(edges.get(1).subEdges));
            tempNode = edges.get(1).getSource();
        }
        else{
            tempNode = new Node();
            Log.d("path to nodes error 1","1");
        }
        int i = 0;
        for (Edge edge: edges) {
            if(i>=2){
                if(edge.getTarget() == tempNode){
                    edgeList.addAll(Lists.reverse(edge.subEdges));
                    tempNode = edge.getSource();
                }
                else if(edge.getSource() == tempNode){
                    edgeList.addAll(edge.subEdges);
                    tempNode = edge.getTarget();
                }
                else {
                    Log.d("path to nodes error 2","" + edge.getLabel());
                }
            }
            i++;
        }

        tempNode = startNode;
        nodes.add(startNode);
        for(Edge edge: edgeList){
            if(edge.getSource()==tempNode){
                nodes.add(edge.getTarget());
                tempNode = edge.getTarget();
            }
            else if(edge.getTarget()==tempNode){
                nodes.add(edge.getSource());
                tempNode = edge.getSource();
            }
            else {
                Log.d("","subedge error");
            }
        }

        return  nodes;
    }

    public static List<GeoPoint> nodesToGeos(List<Node> nodes){
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        for(Node n : nodes){
            geoPoints.add(new GeoPoint(n.getLat(), n.getLon()));
        }
        return geoPoints;
    }

    public static double getShortestDistanceFromNodes(List<Node> nodes){
        return LocationUtils.getDistance(nodesToGeos(nodes));
    }

    public static List<Node> getShortestPathNodes(SimpleWeightedGraph<Node,Edge> graph, Node startNode, Node endNode){
        List<Edge> edges = DijkstraShortestPath.findPathBetween(graph, startNode, endNode);
        return edgesToNodes(edges, startNode, endNode);
    }

    public static double getShortestDistance(SimpleWeightedGraph<Node,Edge> graph, Node startNode, Node endNode){
        return getPathLength(DijkstraShortestPath.findPathBetween(graph, startNode, endNode));
    }

    public static double getPathLength(List<Edge> edges){
        if(edges==null)
            return 0.0;
        double distance = 0.0;
        for(Edge edge:edges)
            distance += edge.getLength();
        return distance;
    }

    public static List<Edge> getLeastVisitedEdges(List<Edge> edges){
        Collections.sort(edges);
        int leastVisitedTimes = edges.get(0).getVisitTimes();
        List<Edge> leastVisitedEdges = new ArrayList<Edge>();
        for(int i=0; i<edges.size(); i++){
            if(edges.get(i).getVisitTimes() == leastVisitedTimes)
                leastVisitedEdges.add(edges.get(i));
            else
                break;
        }
        return leastVisitedEdges;
    }


    public static Node findNearestNode(List<Node> nodeList, double lat, double lon){
        int index=0;
        Double minDistance = MAX_VALUE;
        for(int i=0; i<nodeList.size(); i++){
            Node node = nodeList.get(i);
            double distance = LocationUtils.getDistance(node.getLat(), node.getLon(), lat, lon);
            if(distance < minDistance){
                minDistance = distance;
                index = i;
            }
        }
        return nodeList.get(index);
    }

    public static void deleteIndependentNE(GeoPoint referenceGeo, WeightedPseudograph graph, MyGraph myGraph){

        List<Node> independentNode = new ArrayList<Node>();
        org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node,Edge> dijkstraShortestPath= new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node,Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePaths = dijkstraShortestPath.getPaths(myGraph.findNearestNode(referenceGeo.getLatitude(), referenceGeo.getLongitude()));
        for(int i=0; i<myGraph.nodeArray.size(); i++) {
            try {
                GraphPath<Node, Edge> graphPath = singleSourcePaths.getPath(myGraph.nodeArray.valueAt(i));
                graphPath.getEdgeList();
            }
            catch (Exception d){
                independentNode.add(myGraph.nodeArray.valueAt(i));

            }
        }


        for(Node node: independentNode){
            myGraph.nodeArray.delete(node.getNodeId());
            myGraph.edgeList.removeAll(node.largeEdgeConnections);
        }

    }

    public static void deleteIndependentNE(GeoPoint referenceGeo, SimpleWeightedGraph graph, MyGraph myGraph){

        List<Node> independentNode = new ArrayList<Node>();
        org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node,Edge> dijkstraShortestPath= new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node,Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePaths = dijkstraShortestPath.getPaths(myGraph.findNearestNode(referenceGeo.getLatitude(), referenceGeo.getLongitude()));
        for(int i=0; i<myGraph.nodeArray.size(); i++) {
            try {
                GraphPath<Node, Edge> graphPath = singleSourcePaths.getPath(myGraph.nodeArray.valueAt(i));
                graphPath.getEdgeList();
            }
            catch (Exception d){
                independentNode.add(myGraph.nodeArray.valueAt(i));

            }
        }


        for(Node node: independentNode){
            myGraph.nodeArray.delete(node.getNodeId());
            myGraph.edgeList.removeAll(node.connections);
        }

    }

    public static void edgeFilter(WeightedPseudograph<Node, Edge> graph, MyGraph myGraph, Node startNode, Node endNode, double limitation){
        org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePathsFromStart = dijkstraShortestPath.getPaths(startNode);
        ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePathsFromEnd = dijkstraShortestPath.getPaths(endNode);
        List<Edge> edges = new ArrayList<Edge>();
        for(int i=0; i<myGraph.edgeList.size(); i++){

            if(GraphUtils.getPathLength(singleSourcePathsFromStart.getPath(myGraph.edgeList.get(i).getSource()).getEdgeList())+GraphUtils.getPathLength(singleSourcePathsFromEnd.getPath(myGraph.edgeList.get(i).getTarget()).getEdgeList())+myGraph.edgeList.get(i).getLength()>limitation
                    || GraphUtils.getPathLength(singleSourcePathsFromStart.getPath(myGraph.edgeList.get(i).getTarget()).getEdgeList())+GraphUtils.getPathLength(singleSourcePathsFromEnd.getPath(myGraph.edgeList.get(i).getSource()).getEdgeList())+myGraph.edgeList.get(i).getLength()>limitation){
                edges.add(myGraph.edgeList.get(i));
            }
        }
        myGraph.edgeList.removeAll(edges);
        for(int i=0; i<myGraph.nodeArray.size(); i++){
            if(myGraph.nodeArray.valueAt(i).largeEdgeConnections==null)
                myGraph.nodeArray.delete(myGraph.nodeArray.keyAt(i));
        }
    }

    public static void edgeFilter(WeightedPseudograph<Node, Edge> graph, List<Edge> edgeList, Node startNode, Node endNode, double limitation){
        org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePathsFromStart = dijkstraShortestPath.getPaths(startNode);
        ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePathsFromEnd = dijkstraShortestPath.getPaths(endNode);
        List<Edge> edges = new ArrayList<Edge>();
        Node sNode;
        Node tNode;
        for(int i=0; i<edgeList.size(); i++){
            if(GraphUtils.getPathLength(singleSourcePathsFromStart.getPath(edgeList.get(i).getSource()).getEdgeList())<GraphUtils.getPathLength(singleSourcePathsFromStart.getPath(edgeList.get(i).getTarget()).getEdgeList())){
                sNode = edgeList.get(i).getSource();
                tNode = edgeList.get(i).getTarget();
            }
            else {
                sNode = edgeList.get(i).getTarget();
                tNode = edgeList.get(i).getSource();
            }
            if(GraphUtils.getPathLength(singleSourcePathsFromStart.getPath(sNode).getEdgeList())+ GraphUtils.getPathLength(singleSourcePathsFromEnd.getPath(tNode).getEdgeList())+edgeList.get(i).getLength()>limitation){
                edges.add(edgeList.get(i));
            }
        }
        edgeList.removeAll(edges);
    }

    public static List<Edge> filterEdgesByDistance(List<Edge> edgeList, final GeoPoint center, double range){
        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return Double.valueOf(LocationUtils.getDistance(e1.getCenter(), center))
                        .compareTo(LocationUtils.getDistance(e2.getCenter(), center));
            }
        });

        List<Edge> edges = new ArrayList<Edge>();
        for(int i=0; i<edgeList.size(); i++){
            if(LocationUtils.getDistance(edgeList.get(i).getCenter(), center)<range)
                edges.add(edgeList.get(i));
            else
                break;
        }


        return edges;
    }



}

