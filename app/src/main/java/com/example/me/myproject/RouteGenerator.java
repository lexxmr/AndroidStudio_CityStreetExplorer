package com.example.me.myproject;

import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by Me on 2018/6/12.
 */

public class RouteGenerator {
    MyGraph myGraph;
    WeightedPseudograph<Node, Edge> graph;
    Node startNode;
    Node endNode;
    double limitation;
    public double totalLength;
    public List<Edge> returnEdgeList;
//    FloydWarshallShortestPaths<Node, Edge> floydWarshallShortestPaths;
    Map<Long,  ShortestPathAlgorithm.SingleSourcePaths> dijkstraMap;

    public RouteGenerator(WeightedPseudograph<Node, Edge> graph, MyGraph myGraph, GeoPoint startPoint, GeoPoint endPoint, double limitation, Map<Long,  ShortestPathAlgorithm.SingleSourcePaths> dijkstraMap) {
        this.graph = graph;
        this.myGraph = myGraph;
        this.startNode = myGraph.findNearestNode(startPoint.getLatitude(), startPoint.getLongitude());
        this.endNode = myGraph.findNearestNode(endPoint.getLatitude(), endPoint.getLongitude());
        this.limitation = limitation;
        this.dijkstraMap = dijkstraMap;
    }

    public RouteGenerator(WeightedPseudograph<Node, Edge> graph, MyGraph myGraph, Node startNode, Node endNode, double limitation, Map<Long,  ShortestPathAlgorithm.SingleSourcePaths> dijkstraMap) {
        this.graph = graph;
        this.myGraph = myGraph;
        this.startNode = startNode;
        this.endNode = endNode;
        this.limitation = limitation;
        this.dijkstraMap = dijkstraMap;
    }


    public List<Edge> pureRandomWalk() {
        Random generator = new Random();
        returnEdgeList = new ArrayList<Edge>();
        double currentLength = 0.0;
        Node tempNode;
        boolean flag = true;
        Node s = startNode;
        org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node, Edge> singleSourcePaths = dijkstraShortestPath.getPaths(endNode);

        while (flag) {
            int randomIndex = generator.nextInt(s.largeEdgeConnections.size());
            Edge randomEdge = s.largeEdgeConnections.get(randomIndex);
            if (randomEdge.getSource() == s)
                tempNode = randomEdge.getTarget();
            else
                tempNode = randomEdge.getSource();

            if (currentLength + randomEdge.getLength() + GraphUtils.getPathLength(singleSourcePaths.getPath(tempNode).getEdgeList()) > limitation)
                flag = false;
            else {
                returnEdgeList.add(randomEdge);
                currentLength += randomEdge.getLength();
                s = tempNode;
            }
        }

        returnEdgeList.addAll(singleSourcePaths.getPath(s).getEdgeList());
        totalLength = currentLength + GraphUtils.getPathLength(singleSourcePaths.getPath(s).getEdgeList());

        return returnEdgeList;
    }

    public List<Edge> randomWalk() {
        double currentLength = 0.0;
        Node tempNode;
        boolean flag = true;
        Node s = startNode;
        org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node, Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node, Edge> singleSourcePaths = dijkstraShortestPath.getPaths(endNode);
        returnEdgeList = new ArrayList<Edge>();

        List<Edge> tempList = new ArrayList<Edge>();

        List<Edge> leastVisitedEdges = GraphUtils.getLeastVisitedEdges(s.largeEdgeConnections);
        while (flag) {
//            //method: random select
//            Edge leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));

            //method: select least neighbour
            int[] unvisitedSize = new int [leastVisitedEdges.size()];
            for(int i=0; i< leastVisitedEdges.size(); i++){
                if (leastVisitedEdges.get(i).getSource() == s)
                    tempNode = leastVisitedEdges.get(i).getTarget();
                else
                    tempNode = leastVisitedEdges.get(i).getSource();
                int size=0;
                for(Edge e:tempNode.largeEdgeConnections){
                    if(e.getVisitTimes()==0){
                        size++;
                    }
                }
                unvisitedSize[i] = size;
            }
            tempList.clear();
            for(int i=0; i< leastVisitedEdges.size(); i++){
                if(unvisitedSize[i]==Ints.max(unvisitedSize)){
                    tempList.add(leastVisitedEdges.get(i));
                }
            }
            Edge leastVisitedEdge = tempList.get(new Random().nextInt(tempList.size()));
//            Edge leastVisitedEdge = leastVisitedEdges.get(Ints.indexOf(unvisitedSize, Ints.max(unvisitedSize)));

            if (leastVisitedEdge.getSource() == s)
                tempNode = leastVisitedEdge.getTarget();
            else
                tempNode = leastVisitedEdge.getSource();

            GraphPath<Node,Edge> graphPath = singleSourcePaths.getPath(tempNode);
            if (currentLength + leastVisitedEdge.getLength() + GraphUtils.getPathLength(graphPath.getEdgeList()) > limitation) {
                leastVisitedEdges.remove(leastVisitedEdge);
                if(leastVisitedEdges.size()==0) {
                    flag = false;
                }
            }
            else {
                currentLength += leastVisitedEdge.getLength();
                leastVisitedEdge.setVisitTimes(leastVisitedEdge.getVisitTimes()+1);
                returnEdgeList.add(leastVisitedEdge);
                s = tempNode;
                leastVisitedEdges = GraphUtils.getLeastVisitedEdges(s.largeEdgeConnections);
            }
        }

        for(Edge edge:Lists.reverse(singleSourcePaths.getPath(s).getEdgeList())){
            edge.setVisitTimes(edge.getVisitTimes()+1);
        }
        returnEdgeList.addAll(Lists.reverse(singleSourcePaths.getPath(s).getEdgeList()));
        totalLength = currentLength + GraphUtils.getPathLength(singleSourcePaths.getPath(s).getEdgeList());

        return returnEdgeList;
    }

    public List<Edge> leastVisitedEdgeFirst(double repeatWeight, double crossWeight) {
        boolean flag = true;
        List<Edge> tempEdgeList = new ArrayList<Edge>();
        List<Edge> determinedEdgeList = new ArrayList<Edge>();
        List<Edge> candidateEdgeList = new ArrayList<>(myGraph.edgeList);
        List<Edge> leastVisitedEdges;
        returnEdgeList = new ArrayList<Edge>();
        int iter = 0;
        while (flag) {
            leastVisitedEdges = GraphUtils.getLeastVisitedEdges(candidateEdgeList);
            Edge leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));
            tempEdgeList.add(leastVisitedEdge);
            iter++;

            if (canBeVisited(tempEdgeList, startNode, endNode, limitation, repeatWeight, crossWeight, true, true)) {
                determinedEdgeList = new ArrayList<>(tempEdgeList);
//                candidateEdgeList = filterEdgesByLimitation(determinedEdgeList, candidateEdgeList, startNode, endNode, limitation, true);
                if(candidateEdgeList.size() == 0)
                    flag = false;
                iter = 0;
            } else {
                if (determinedEdgeList.size() != 0) {
                    tempEdgeList.remove(leastVisitedEdge);
                    if (iter > 50)
                        flag = false;
                } else {
                    Log.d(TAG, "finding... " + leastVisitedEdge.getLabel());
                    tempEdgeList.clear();
                    determinedEdgeList.add(leastVisitedEdge);
                    return  edgesToPath(determinedEdgeList, startNode, endNode, repeatWeight, crossWeight, true, false);
                }
            }
        }

        Log.d("edge size", "" + determinedEdgeList.size());
        return edgesToPath(determinedEdgeList, startNode, endNode, repeatWeight, crossWeight, true, true);
    }

    private boolean canBeVisited(List<Edge> edgeList, final Node start, Node end, double limitation, double repeatWeight, double crossWeight, boolean sort, boolean weightFlag) {

        if(sort) {
            edgeList = sortEdges(edgeList, start);
        }

        List<Edge> tempEdgeList = new ArrayList<Edge>();
        if(weightFlag) {
            for (Edge edge : edgeList) {
                tempEdgeList.add(edge);
                graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
                for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
            }
        }

        Node tempNode;
        totalLength = 0;

        GraphPath<Node, Edge> graphPath;
        graphPath = new DijkstraShortestPath<Node, Edge>(graph).getPath(start, edgeList.get(0).getsNode());
        if(weightFlag) {
            for (Edge edge : graphPath.getEdgeList()) {
                tempEdgeList.add(edge);
                graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
                for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
            }
        }
        totalLength += GraphUtils.getPathLength(graphPath.getEdgeList()) + edgeList.get(0).getLength();
        tempNode = edgeList.get(0).gettNode();


        for (int i = 0; i < edgeList.size() - 1; i++) {

            graphPath = new DijkstraShortestPath<Node, Edge>(graph).getPath(tempNode, edgeList.get(i + 1).getsNode());
            if(weightFlag) {
                for (Edge edge : graphPath.getEdgeList()) {
                    tempEdgeList.add(edge);
                    graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                    for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                        tempEdgeList.add(nodeEdge);
                        graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                    }
                    for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                        tempEdgeList.add(nodeEdge);
                        graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                    }
                }
            }
            totalLength += GraphUtils.getPathLength(graphPath.getEdgeList()) + edgeList.get(i + 1).getLength();
            tempNode = edgeList.get(i+1).gettNode();
        }

        graphPath = new DijkstraShortestPath<Node, Edge>(graph).getPath(tempNode,  end);
        if(weightFlag) {
            for (Edge edge : graphPath.getEdgeList()) {
                tempEdgeList.add(edge);
                graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
                for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
            }
        }
        totalLength += GraphUtils.getPathLength(graphPath.getEdgeList());

        if(weightFlag) {
            for (int i = 0; i < tempEdgeList.size(); i++) {
                graph.setEdgeWeight(tempEdgeList.get(i), tempEdgeList.get(i).getLength());
            }
        }

        return totalLength < limitation;
    }

    private List<Edge> edgesToPath(List<Edge> edgeList, final Node start, Node end, double repeatWeight, double crossWeight, boolean sort, boolean weightFlag) {

        if(sort) {
            edgeList = sortEdges(edgeList, start);
        }


        List<Edge> tempEdgeList = new ArrayList<Edge>();

        if(weightFlag) {
            for (Edge edge : edgeList) {
                tempEdgeList.add(edge);
                graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
                for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
            }
        }

        Node tempNode;
        totalLength = 0;


        GraphPath<Node, Edge> graphPath;
        graphPath = new DijkstraShortestPath<Node, Edge>(graph).getPath(start, edgeList.get(0).getsNode());
        if(weightFlag) {
            for (Edge edge : graphPath.getEdgeList()) {
                tempEdgeList.add(edge);
                graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
                for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
            }
        }
        returnEdgeList.addAll(graphPath.getEdgeList());
        returnEdgeList.add(edgeList.get(0));
        totalLength += GraphUtils.getPathLength(graphPath.getEdgeList()) + edgeList.get(0).getLength();
        tempNode = edgeList.get(0).gettNode();


        for (int i = 0; i < edgeList.size() - 1; i++) {

            graphPath = new DijkstraShortestPath<Node, Edge>(graph).getPath(tempNode, edgeList.get(i + 1).getsNode());
            if(weightFlag) {
                for (Edge edge : graphPath.getEdgeList()) {
                    tempEdgeList.add(edge);
                    graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);
                    for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                        tempEdgeList.add(nodeEdge);
                        graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                    }
                    for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                        tempEdgeList.add(nodeEdge);
                        graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                    }
                }
            }
            returnEdgeList.addAll(graphPath.getEdgeList());
            returnEdgeList.add(edgeList.get(i + 1));
            totalLength += GraphUtils.getPathLength(graphPath.getEdgeList()) + edgeList.get(i + 1).getLength();
            tempNode = edgeList.get(i+1).gettNode();
        }

        graphPath = new DijkstraShortestPath<Node, Edge>(graph).getPath(tempNode,  end);
        if(weightFlag) {
            for (Edge edge : graphPath.getEdgeList()) {
                tempEdgeList.add(edge);
                graph.setEdgeWeight(edge, edge.getWeight() + repeatWeight);

                for (Edge nodeEdge : edge.getSource().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
                for (Edge nodeEdge : edge.getTarget().largeEdgeConnections) {
                    tempEdgeList.add(nodeEdge);
                    graph.setEdgeWeight(nodeEdge, nodeEdge.getWeight() + crossWeight);
                }
            }
        }
        returnEdgeList.addAll(graphPath.getEdgeList());
        totalLength += GraphUtils.getPathLength(graphPath.getEdgeList());


        if(weightFlag) {
            for (int i = 0; i < tempEdgeList.size(); i++) {
                graph.setEdgeWeight(tempEdgeList.get(i), tempEdgeList.get(i).getLength());
            }
        }

        return returnEdgeList;

    }


    public List<Edge> leastEdgeRandomWalkSplitBudget() {
        List<Edge> leastVisitedEdges = GraphUtils.getLeastVisitedEdges(myGraph.edgeList);
        DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node, Edge> singleSourcePathsFromStart = dijkstraShortestPath.getPaths(startNode);
        ShortestPathAlgorithm.SingleSourcePaths<Node, Edge> singleSourcePathsFromEnd = dijkstraShortestPath.getPaths(endNode);
        returnEdgeList = new ArrayList<Edge>();
        Edge leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));
        if(LocationUtils.getDistance(leastVisitedEdge.getSource(),startNode)<LocationUtils.getDistance(leastVisitedEdge.getSource(),endNode)){
            leastVisitedEdge.setsNode(leastVisitedEdge.getSource());
            leastVisitedEdge.settNode(leastVisitedEdge.getTarget());
        }else {
            leastVisitedEdge.settNode(leastVisitedEdge.getSource());
            leastVisitedEdge.setsNode(leastVisitedEdge.getTarget());
        }

        double d1 = GraphUtils.getPathLength(singleSourcePathsFromStart.getPath(leastVisitedEdge.getsNode()).getEdgeList());
        double d2 = GraphUtils.getPathLength(singleSourcePathsFromEnd.getPath(leastVisitedEdge.gettNode()).getEdgeList());
        double restBudget = limitation-d1-d2-leastVisitedEdge.getLength();
        double ratio = Math.random();

        RouteGenerator rgs = new RouteGenerator(graph, myGraph, startNode, leastVisitedEdge.getsNode(), d1 + restBudget*ratio, dijkstraMap);
        RouteGenerator rge = new RouteGenerator(graph, myGraph, leastVisitedEdge.gettNode(), endNode, d2 + restBudget*(1-ratio), dijkstraMap);
        rgs.randomWalk();
        rge.randomWalk();

        totalLength = rgs.totalLength + rge.totalLength + leastVisitedEdge.getLength();
        returnEdgeList.addAll(rgs.returnEdgeList);
        returnEdgeList.add(leastVisitedEdge);
        returnEdgeList.addAll(rge.returnEdgeList);
        return returnEdgeList;
    }

    public List<Edge> leastEdgeRegion(double range, double repeatWeight, double crossWeight, boolean weightFlag, boolean fromFar) {
        boolean flag = true;
        List<Edge> tempEdgeList = new ArrayList<Edge>();
        List<Edge> determinedEdgeList = new ArrayList<Edge>();
        List<Edge> edgeList = new ArrayList<>(myGraph.edgeList);
        returnEdgeList = new ArrayList<Edge>();
        int iter = 0;
        int iterLimitation = 10;
        List<Edge> leastVisitedEdges;
        Edge leastVisitedEdge;
        if(fromFar) {
            leastVisitedEdges = sortLeastEdgesByDistance(edgeList,new GeoPoint(startNode.getLat(), startNode.getLon()),new GeoPoint(endNode.getLat(), endNode.getLon()));
            leastVisitedEdge = leastVisitedEdges.get(leastVisitedEdges.size()-1);
        }
        else {
            leastVisitedEdges = GraphUtils.getLeastVisitedEdges(edgeList);
            leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));
        }
        tempEdgeList.add(leastVisitedEdge);
        while (flag) {

            iter++;

            if (canBeVisited(tempEdgeList, startNode, endNode, limitation, repeatWeight, crossWeight, true, weightFlag)) {
                determinedEdgeList = new ArrayList<Edge>(tempEdgeList);
//                int centerIndex = new Random().nextInt(determinedEdgeList.size()+2);
//                if(centerIndex < determinedEdgeList.size())
//                    edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, determinedEdgeList.get(centerIndex).getCenter(), smoothedRange);
//                else if(centerIndex == determinedEdgeList.size())
//                    edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, new GeoPoint(startNode.getLat(), startNode.getLon()), smoothedRange);
//                else if(centerIndex == determinedEdgeList.size() + 1)
//                    edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, new GeoPoint(endNode.getLat(), endNode.getLon()), smoothedRange);
//                else
//                    Log.d("","edge filter center error");
                edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, leastVisitedEdge.getCenter(), range);
                iter = 0;
            } else {
                if (determinedEdgeList.size() != 0) {
                    tempEdgeList.remove(leastVisitedEdge);
//                    int centerIndex = new Random().nextInt(determinedEdgeList.size()+2);
//                    if(centerIndex < determinedEdgeList.size())
//                        edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, determinedEdgeList.get(centerIndex).getCenter(), smoothedRange);
//                    else if(centerIndex == determinedEdgeList.size())
//                        edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, new GeoPoint(startNode.getLat(), startNode.getLon()), smoothedRange);
//                    else if(centerIndex == determinedEdgeList.size() + 1)
//                        edgeList = GraphUtils.filterEdgesByDistance(myGraph.edgeList, new GeoPoint(endNode.getLat(), endNode.getLon()), smoothedRange);
//                    else
//                        Log.d("","edge filter center error");
                    if (iter > iterLimitation)
                        flag = false;
                } else {
                    Log.d(TAG, "finding... " + leastVisitedEdge.getLabel());
                    determinedEdgeList.add(leastVisitedEdge);
                    return edgesToPath(determinedEdgeList, startNode, endNode, repeatWeight, crossWeight, true, false);
                }
            }

            leastVisitedEdges = GraphUtils.getLeastVisitedEdges(edgeList);
            leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));
            tempEdgeList.add(leastVisitedEdge);
        }


        Log.d("edge size", "" + determinedEdgeList.size());
        return edgesToPath(determinedEdgeList, startNode, endNode, repeatWeight, crossWeight, true, weightFlag);
    }

    private void setEdge(Edge edge, Node start){
        if(LocationUtils.getDistance(start, edge.getSource())<LocationUtils.getDistance(start, edge.getTarget())){
            edge.setsNode(edge.getSource());
            edge.settNode(edge.getTarget());
        }
        else{
            edge.setsNode(edge.getTarget());
            edge.settNode(edge.getSource());
        }
    }


    public List<Edge> leastEdgeRandomWalk() {
        List<Edge> leastVisitedEdges = GraphUtils.getLeastVisitedEdges(myGraph.edgeList);
        DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Node, Edge> singleSourcePathsFromStart = dijkstraShortestPath.getPaths(startNode);
        boolean flag = true;
        returnEdgeList = new ArrayList<Edge>();
        leastVisitedEdges = sortLeastEdgesByDistance(leastVisitedEdges, new GeoPoint(startNode.getLat(), startNode.getLon()), new GeoPoint(endNode.getLat(), endNode.getLon()));
//        Edge leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));
        Edge leastVisitedEdge = leastVisitedEdges.get(0);
//        Edge leastVisitedEdge = leastVisitedEdges.get(leastVisitedEdges.size()-1);
        setEdge(leastVisitedEdge, startNode);

        GraphPath<Node, Edge> graphPathFromStart = singleSourcePathsFromStart.getPath(leastVisitedEdge.getsNode());
        returnEdgeList.addAll(graphPathFromStart.getEdgeList());
        returnEdgeList.add(leastVisitedEdge);
        totalLength = 0;
        totalLength += GraphUtils.getPathLength(returnEdgeList);
        RouteGenerator rg = new RouteGenerator(graph, myGraph, leastVisitedEdge.gettNode(), endNode, limitation - GraphUtils.getPathLength(graphPathFromStart.getEdgeList()) - leastVisitedEdge.getLength(), dijkstraMap);
        rg.randomWalk();
        returnEdgeList.addAll(rg.returnEdgeList);
        totalLength+=rg.totalLength;

        return returnEdgeList;
    }

//    public List<Edge> leastEdgeRandomWalkDoubleEnd(){
//
//    }

    public List<Edge> leastEdgeExtend(double repeatWeight, double crossWeight, boolean weightFlag, boolean fromFar) {
        boolean flag = true;
        List<Edge> tempEdgeList = new ArrayList<Edge>();
        List<Edge> determinedEdgeList = new ArrayList<Edge>();
        List<Edge> edgeList = new ArrayList<>(myGraph.edgeList);
        returnEdgeList = new ArrayList<Edge>();
        int iter = 0;
        int iterLimitation = 10;
        List<Edge> leastVisitedEdges;
        Edge leastVisitedEdge;
        if(fromFar) {
            leastVisitedEdges = sortLeastEdgesByDistance(edgeList, new GeoPoint(startNode.getLat(), startNode.getLon()), new GeoPoint(endNode.getLat(), endNode.getLon()));
            leastVisitedEdge = leastVisitedEdges.get(leastVisitedEdges.size() - 1);
        }
        else {
            leastVisitedEdges = GraphUtils.getLeastVisitedEdges(edgeList);
            leastVisitedEdge = leastVisitedEdges.get(new Random().nextInt(leastVisitedEdges.size()));
        }
        tempEdgeList.add(leastVisitedEdge);
        edgeList.remove(leastVisitedEdge);
        edgeList = sortLeastEdgesByDistance(edgeList, leastVisitedEdge.getCenter(), leastVisitedEdge.getCenter());
        while (flag) {

            iter++;
            if (canBeVisited(tempEdgeList, startNode, endNode, limitation, repeatWeight, crossWeight, true, weightFlag)) {
                determinedEdgeList = new ArrayList<Edge>(tempEdgeList);

                iter = 0;
            } else {
                if (determinedEdgeList.size() != 0) {
                    tempEdgeList.remove(leastVisitedEdge);
                    if (iter > iterLimitation)
                        flag = false;
                } else {
                    Log.d(TAG, "finding... " + leastVisitedEdge.getLabel());
                    determinedEdgeList.add(leastVisitedEdge);
                    return edgesToPath(determinedEdgeList, startNode, endNode, repeatWeight, crossWeight, true, false);
                }
            }
            if(edgeList.size()==0){
                edgeList = sortLeastEdgesByDistance(myGraph.edgeList, leastVisitedEdge.getCenter(), leastVisitedEdge.getCenter());
            }
            leastVisitedEdge = edgeList.get(0);
            edgeList.remove(leastVisitedEdge);
            tempEdgeList.add(leastVisitedEdge);
        }


        Log.d("edge size", "" + determinedEdgeList.size());
        return edgesToPath(determinedEdgeList, startNode, endNode, repeatWeight, crossWeight, true, weightFlag);
    }

    private List<Edge> sortLeastEdgesByDistance(List<Edge> edgeList, final GeoPoint startCenter, final GeoPoint endCenter){
        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return Double.valueOf(LocationUtils.getDistance(e1.getCenter(), startCenter)+LocationUtils.getDistance(e1.getCenter(), endCenter))
                        .compareTo(LocationUtils.getDistance(e2.getCenter(), startCenter)+LocationUtils.getDistance(e2.getCenter(), endCenter));
            }
        });




        return GraphUtils.getLeastVisitedEdges(edgeList);
    }

    private List<Edge> sortLeastEdgesByDistance(List<Edge> edgeList, final Node start, final Node end){
        for(int i=0; i<edgeList.size(); i++){
            double d1 = GraphUtils.getPathLength(dijkstraMap.get(edgeList.get(i).getSource().getNodeId()).getPath(start).getEdgeList());
            double d2 = GraphUtils.getPathLength(dijkstraMap.get(edgeList.get(i).getTarget().getNodeId()).getPath(start).getEdgeList());
            if(d1<d2){
                edgeList.get(i).setsNode(edgeList.get(i).getSource());
                edgeList.get(i).settNode(edgeList.get(i).getTarget());
            }
            else {
                edgeList.get(i).settNode(edgeList.get(i).getSource());
                edgeList.get(i).setsNode(edgeList.get(i).getTarget());
            }
        }
        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return Double.valueOf(GraphUtils.getPathLength(dijkstraMap.get(e1.getsNode().getNodeId()).getPath(start).getEdgeList())+
                        GraphUtils.getPathLength(dijkstraMap.get(e1.gettNode().getNodeId()).getPath(end).getEdgeList()))
                        .compareTo(GraphUtils.getPathLength(dijkstraMap.get(e2.getsNode().getNodeId()).getPath(start).getEdgeList())+
                                GraphUtils.getPathLength(dijkstraMap.get(e2.gettNode().getNodeId()).getPath(end).getEdgeList()));
            }
        });




        return GraphUtils.getLeastVisitedEdges(edgeList);
    }


    List<Edge> candidateList;
    public List<Edge> myGRASP(int method) {
        candidateList = new ArrayList<Edge>(GraphUtils.getLeastVisitedEdges(myGraph.edgeList));
       List<Edge> tempEdgeList = new ArrayList<Edge>();
       Edge selectedEdge = candidateList.get(new Random().nextInt(candidateList.size()));
//       candidateList = sortLeastEdgesByDistance(candidateList, new GeoPoint(startNode.getLat(), startNode.getLon()), new GeoPoint(endNode.getLat(), endNode.getLon()));
//       Edge selectedEdge = candidateList.get(candidateList.size() - 1);
//       candidateList = sortLeastEdgesByDistance(candidateList, selectedEdge.getCenter(), selectedEdge.getCenter());
//       Edge selectedEdge = candidateList.get(0);
       while (selectedEdge!=null) {
           Log.d("candidate size",""+candidateList.size());
           tempEdgeList.add(selectedEdge);
           candidateList.remove(selectedEdge);
           selectedEdge = filterAndSelectEdge(tempEdgeList, method);
       }
       return constructPath(tempEdgeList, startNode, endNode, limitation);
    }

    private List<Edge> tempList = new ArrayList<Edge>();
    private Edge filterAndSelectEdge(List<Edge> currentEdgeList, int method){

        if(candidateList.size()==0){
            return null;
        }

//        //method1: get all reachable edges
//        List<Edge> filteredEdgeList = new ArrayList<>();
//        double pathLength[] = new double [candidateList.size()];
//        for(int i=0; i<candidateList.size(); i++){
//            currentEdgeList.add(candidateList.get(i));
//            if(constructPath(currentEdgeList, startNode, endNode, limitation)!=null){
//                pathLength[i] = totalLength;
//                filteredEdgeList.add(candidateList.get(i));
//            }
//            else {
//                pathLength[i] = limitation;
//            }
//            currentEdgeList.remove(candidateList.get(i));
//        }
//
//        if(filteredEdgeList.size()==0)
//            return null;

        //method2: range and iter
        List<Edge> filteredEdgeList = new ArrayList<Edge>(myGraph.edgeList);
        filteredEdgeList.removeAll(currentEdgeList);
        double[] d = new double[currentEdgeList.size()];
        double[] allEdgesMinD = new double[filteredEdgeList.size()];
        for(int i=0; i<filteredEdgeList.size(); i++){
            for(int j=0; j<currentEdgeList.size(); j++){
//                double d1 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getSource().getNodeId()).getPath(filteredEdgeList.get(i).getSource()).getEdgeList());
//                double d2 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getSource().getNodeId()).getPath(filteredEdgeList.get(i).getTarget()).getEdgeList());
//                double d3 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getTarget().getNodeId()).getPath(filteredEdgeList.get(i).getSource()).getEdgeList());
//                double d4 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getTarget().getNodeId()).getPath(filteredEdgeList.get(i).getTarget()).getEdgeList());
//                d[j] = Math.min(Math.min(d1,d2),Math.min(d3,d4));
                d[j] = LocationUtils.getDistance(filteredEdgeList.get(i).getCenter(), currentEdgeList.get(j).getCenter());
            }
            allEdgesMinD[i] = Doubles.min(d);
        }
        sortLeastEdgesByDistance(filteredEdgeList, allEdgesMinD);
//        filteredEdgeList = GraphUtils.filterEdgesByDistance(filteredEdgeList, currentEdgeList.get(currentEdgeList.size()-1).getCenter(), 200);
        int iter=0;
        while (iter<20){
            currentEdgeList.add(filteredEdgeList.get(iter));
            if(constructPath(currentEdgeList, startNode, endNode, limitation)!=null) {
                currentEdgeList.remove(filteredEdgeList.get(iter));
                return filteredEdgeList.get(iter);
            }
            currentEdgeList.remove(filteredEdgeList.get(iter));
            iter++;
        }
        return null;

//        //method3: select nearest and remove
//        int iter=0;
//        Edge selectedEdge = null;
//        List<Edge> removeList = new ArrayList<Edge>();
//        while (iter<20&&iter<candidateList.size()){
//            currentEdgeList.add(candidateList.get(iter));
//            if(constructPath(currentEdgeList, startNode, endNode, limitation)!=null) {
//                currentEdgeList.remove(candidateList.get(iter));
//                selectedEdge = candidateList.get(iter);
//                break;
//            }
//            removeList.add(candidateList.get(iter));
//            currentEdgeList.remove(candidateList.get(iter));
//            iter++;
//        }
//        candidateList.removeAll(removeList);
//        return selectedEdge;

//        if(method==1) {
//            //method1: random select
//            candidateList = filteredEdgeList;
//            return filteredEdgeList.get(new Random().nextInt(filteredEdgeList.size()));
//        }
//        else if (method == 2) {
//            //method2: select shortest
//            tempList.clear();
//            for (int i = 0; i < candidateList.size(); i++) {
//                if (pathLength[i] == Doubles.min(pathLength)) {
//                    tempList.add(candidateList.get(i));
//                }
//            }
//            candidateList = filteredEdgeList;
//            return tempList.get(new Random().nextInt(tempList.size()));
//        }
//        else if(method==3) {
//
//            //method3: select nearest
//            double[] d = new double[currentEdgeList.size()];
//            double[] allEdgesMinDistance = new double[filteredEdgeList.size()];
//            for (int i = 0; i < filteredEdgeList.size(); i++) {
//                for (int j = 0; j < currentEdgeList.size(); j++) {
//                    double d1 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getsNode().getNodeId()).getPath(filteredEdgeList.get(i).getSource()).getEdgeList());
//                    double d2 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).gettNode().getNodeId()).getPath(filteredEdgeList.get(i).getSource()).getEdgeList());
//                    double d3 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getsNode().getNodeId()).getPath(filteredEdgeList.get(i).getTarget()).getEdgeList());
//                    double d4 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).gettNode().getNodeId()).getPath(filteredEdgeList.get(i).getTarget()).getEdgeList());
//                    d[j] = Math.min(Math.min(d1, d2), Math.min(d3, d4));
////                LocationUtils.getDistance(filteredEdgeList.get(i).getCenter(), currentEdgeList.get(j).getCenter());
//                }
//                allEdgesMinDistance[i] = Doubles.min(d);
//            }
//            candidateList = filteredEdgeList;
//            tempList.clear();
//            for (int i = 0; i < filteredEdgeList.size(); i++) {
//                if (allEdgesMinDistance[i] == Doubles.min(allEdgesMinDistance)) {
//                    tempList.add(filteredEdgeList.get(i));
//                }
//            }
//            return tempList.get(new Random().nextInt(tempList.size()));
//        }
//        else {
//            //method4: select most after filter
//            int[] size = new int[filteredEdgeList.size()];
//            List<Edge> tempFilteredEdgeList = new ArrayList<Edge>(filteredEdgeList);
//            for (int i = 0; i < filteredEdgeList.size(); i++) {
//                currentEdgeList.add(filteredEdgeList.get(i));
//                tempFilteredEdgeList.remove(filteredEdgeList.get(i));
//                size[i] = filterEdges(currentEdgeList, tempFilteredEdgeList).size();
//                currentEdgeList.remove(currentEdgeList.size() - 1);
//                tempFilteredEdgeList.add(filteredEdgeList.get(i));
//
//            }
//            candidateList = filteredEdgeList;
//
//            tempList.clear();
//            for (int i = 0; i < filteredEdgeList.size(); i++) {
//                if (size[i] == Ints.max(size)) {
//                    tempList.add(filteredEdgeList.get(i));
//                }
//            }
//            return tempList.get(new Random().nextInt(tempList.size()));
//        }

    }

    private List<Edge> sortLeastEdgesByDistance(List<Edge> edgeList, final double[] allEdgesMinD){
        final List<Edge> tempEdgeList = new ArrayList<Edge>(edgeList);
        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return Double.valueOf(allEdgesMinD[tempEdgeList.indexOf(e1)])
                        .compareTo(allEdgesMinD[tempEdgeList.indexOf(e2)]);
            }
        });




        return GraphUtils.getLeastVisitedEdges(edgeList);
    }

    List<Edge> filteredEdgeList = new ArrayList<Edge>();
    private List<Edge> filterEdges(List<Edge> currentEdgeList, List<Edge> candidateList){

        filteredEdgeList.clear();
//        double[] d = new double[currentEdgeList.size()];
//        List<Edge> newCandidateList = new ArrayList<Edge>();
//        for(int i=0; i<candidateList.size(); i++){
//            for(int j=0; j<currentEdgeList.size(); j++){
//                double d1 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getSource().getNodeId()).getPath(candidateList.get(i).getSource()).getEdgeList());
//                double d2 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getSource().getNodeId()).getPath(candidateList.get(i).getTarget()).getEdgeList());
//                double d3 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getTarget().getNodeId()).getPath(candidateList.get(i).getSource()).getEdgeList());
//                double d4 = GraphUtils.getPathLength(dijkstraMap.get(currentEdgeList.get(j).getTarget().getNodeId()).getPath(candidateList.get(i).getTarget()).getEdgeList());
//                d[j] = Math.min(Math.min(d1,d2),Math.min(d3,d4));
//            }
//            if( Doubles.min(d)<range){
//                newCandidateList.add(candidateList.get(i));
//            }
//        }

        List<Edge> newCandidateList = candidateList;
        for(int i=0; i<newCandidateList.size(); i++){
            currentEdgeList.add(newCandidateList.get(i));
            if(constructPath(currentEdgeList, startNode, endNode, limitation)!=null){
                filteredEdgeList.add(newCandidateList.get(i));
            }
            currentEdgeList.remove(newCandidateList.get(i));
        }


        return filteredEdgeList;
    }

    private List<Edge> pathEdgeList = new ArrayList<Edge>();
    private List<Edge> constructPath(List<Edge> edgeList, Node start, Node end, double limitation){
        edgeList = sortEdges(edgeList, start);
        pathEdgeList.clear();
        pathEdgeList.addAll(dijkstraMap.get(start.getNodeId()).getPath(edgeList.get(0).getsNode()).getEdgeList());
//        pathEdgeList.addAll(floydWarshallShortestPaths.getPath(start, edgeList.get(0).getsNode()).getEdgeList());
        pathEdgeList.add(edgeList.get(0));
        for(int i=1; i<edgeList.size(); i++){
            pathEdgeList.addAll(dijkstraMap.get(edgeList.get(i-1).gettNode().getNodeId()).getPath(edgeList.get(i).getsNode()).getEdgeList());
//            pathEdgeList.addAll(floydWarshallShortestPaths.getPath(edgeList.get(i-1).gettNode(), edgeList.get(0).getsNode()).getEdgeList());
            pathEdgeList.add(edgeList.get(i));
        }
        pathEdgeList.addAll(dijkstraMap.get(edgeList.get(edgeList.size()-1).gettNode().getNodeId()).getPath(end).getEdgeList());
//        pathEdgeList.addAll(floydWarshallShortestPaths.getPath(edgeList.get(edgeList.size()-1).gettNode(), end).getEdgeList());
        returnEdgeList = pathEdgeList;
        totalLength = GraphUtils.getPathLength(pathEdgeList);
        if(totalLength>limitation){
            return null;
        }
        else {
            return pathEdgeList;
        }
    }

    private  List<Edge> tempEdgeList;
    List<Edge> newEdgeList = new ArrayList<Edge>();
    private List<Edge>  sortEdges(List<Edge> edgeList, Node start){
        tempEdgeList = new ArrayList<Edge>(edgeList);
        Node tempNode = start;
        newEdgeList.clear();
        while (tempEdgeList.size()>0) {
            for (Edge edge : tempEdgeList) {
                double d1 = GraphUtils.getPathLength(dijkstraMap.get(tempNode.getNodeId()).getPath(edge.getSource()).getEdgeList());
                double d2 = GraphUtils.getPathLength(dijkstraMap.get(tempNode.getNodeId()).getPath(edge.getTarget()).getEdgeList());
//                double d1 = GraphUtils.getPathLength(floydWarshallShortestPaths.getPath(tempNode, edge.getSource()).getEdgeList());
//                double d2 = GraphUtils.getPathLength(floydWarshallShortestPaths.getPath(tempNode, edge.getTarget()).getEdgeList());
                if (d1 < d2) {
                    edge.setsNode(edge.getSource());
                    edge.settNode(edge.getTarget());
                } else {
                    edge.setsNode(edge.getTarget());
                    edge.settNode(edge.getSource());
                }
            }
            sortByDistance(tempEdgeList, tempNode);
            newEdgeList.add(tempEdgeList.get(0));
            tempNode = tempEdgeList.get(0).gettNode();
            tempEdgeList.remove(tempEdgeList.get(0));
        }
        return newEdgeList;
    }

    private void sortByDistance(List<Edge>edgeList, final Node node){
        Collections.sort(edgeList, new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return Double.valueOf(GraphUtils.getPathLength(dijkstraMap.get(node.getNodeId()).getPath(e1.getsNode()).getEdgeList()))
                    .compareTo(GraphUtils.getPathLength(dijkstraMap.get(node.getNodeId()).getPath(e2.getsNode()).getEdgeList()));
//                return Double.valueOf(GraphUtils.getPathLength(floydWarshallShortestPaths.getPath(node, e1.getsNode()).getEdgeList()))
//                        .compareTo(GraphUtils.getPathLength(floydWarshallShortestPaths.getPath(node, e2.getsNode()).getEdgeList()));
            }
        });
    }

    private int checkHowManyLeastEdges(List<Edge>edgeList){
        int size=0;
        for(int i=0; i<edgeList.size(); i++){
            if(edgeList.get(i).getVisitTimes()==0){
                size++;
            }
        }
        return size;
    }
}

