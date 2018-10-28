package com.example.me.myproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends Activity {

    MapView map = null;
    String response;
    public static final String mPath = "osmdata.txt";
    private FileReader fileReader;

    double limitation = 2000;
    GeoPoint startGeo = new GeoPoint( 55.9444562, -3.1877047);
    GeoPoint endGeo = new GeoPoint(  55.9444562, -3.1877047);

    TextView routeLengthText;
    TextView iterationText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(com.example.me.myproject.R.layout.activity_main);

        map = (MapView) findViewById(com.example.me.myproject.R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        final IMapController mapController = map.getController();
        mapController.setZoom(16.0);
        mapController.setCenter(new GeoPoint(55.9444562, -3.1877047));


        routeLengthText = (TextView) findViewById(com.example.me.myproject.R.id.RouteLength);
        iterationText = (TextView) findViewById(com.example.me.myproject.R.id.Iteration);

        readDataFromOverpass();

//        readFromFile();

        Thread newThread = new Thread() {
            public void run() {

                WeightedPseudograph<Node, Edge> graph = new WeightedPseudograph<Node, Edge>(Edge.class);
                MyGraph myGraph = new MyGraph();
                GraphParser gp = new GraphParser(graph, myGraph);
                gp.parse(response);

                Node startNode = myGraph.findNearestNode(startGeo.getLatitude(), startGeo.getLongitude());
                Node endNode = myGraph.findNearestNode(endGeo.getLatitude(), endGeo.getLongitude());
                setMarker(startGeo);
                setMarker(endGeo);

//                boolean f = graph.containsVertex(n);
//                org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node,Edge> dijkstraShortestPath= new org.jgrapht.alg.shortestpath.DijkstraShortestPath<Node,Edge>(graph);
//                ShortestPathAlgorithm.SingleSourcePaths<Node,Edge> singleSourcePaths = dijkstraShortestPath.getPaths(n);



                GraphUtils.deleteIndependentNE(startGeo, graph, myGraph);
                GraphUtils.edgeFilter(graph, myGraph, myGraph.findNearestNode(startGeo.getLatitude(), startGeo.getLongitude()),
                        myGraph.findNearestNode(endGeo.getLatitude(), endGeo.getLongitude()), limitation);


                DijkstraShortestPath<Node,Edge> dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(graph);
                Map<Long, ShortestPathAlgorithm.SingleSourcePaths> dijkstraMap = new HashMap();

                for(int i=0; i<myGraph.nodeArray.size(); i++){
                    dijkstraMap.putIfAbsent(myGraph.nodeArray.keyAt(i), dijkstraShortestPath.getPaths(myGraph.nodeArray.valueAt(i)));
                }
//                Node n1 = new Node();
//                Node n2 = new Node();
//                for(int i=0; i<myGraph.nodeArray.size(); i++){
//                    if(myGraph.nodeArray.valueAt(i).getNodeId()==480761355L)
//                        n1 = myGraph.nodeArray.valueAt(i);
//                    if(myGraph.nodeArray.valueAt(i).getNodeId()==773527665L)
//                        n2 = myGraph.nodeArray.valueAt(i);
//                }
//                FloydWarshallShortestPaths<Node, Edge> floydWarshallShortestPaths = new FloydWarshallShortestPaths(graph);
//                floydWarshallShortestPaths.getPath(startNode, n1);



//                dijkstraMap.get(startNode.getNodeId());

                RouteGenerator rg = new RouteGenerator(graph, myGraph, startGeo, endGeo, limitation, dijkstraMap);



//                DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(graph);
//                double d = dijkstraShortestPath.getPath(n1,n2).getWeight();
//                double d2 = GraphUtils.getPathLength(dijkstraShortestPath.getPath(n1,n2).getEdgeList());
//                GraphUtils.edgeFilter(graph, myGraph, n1, n2, 73.74);


//                Edge e = new Edge();
//                List<Edge> es = new ArrayList<Edge>();
//                for(int i=0; i<myGraph.edgeList.size(); i++) {
//                    if (myGraph.edgeList.get(i).getLabel().equals("158080156,9")) {
//                        e = myGraph.edgeList.get(i);
//                    }
//                }
//                DijkstraShortestPath<Node, Edge> dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(graph);
//                double d1 = dijkstraShortestPath.getPath( myGraph.findNearestNode(startGeo.getLatitude(), startGeo.getLongitude()), e.getSource()).getWeight();
//                double d2 = dijkstraShortestPath.getPath( myGraph.findNearestNode(endGeo.getLatitude(), endGeo.getLongitude()), e.getTarget()).getWeight();

//
//                for(Edge edge: myGraph.edgeList){
//                    List<Edge> edgeList = new ArrayList<Edge>();
//                    edgeList.addAll(edge.subEdges);
//                    Polyline roadOverlay = new Polyline();
//                    roadOverlay.setPoints(GraphUtils.nodesToGeos(GraphUtils.edgesToNodes(edgeList, edge.getSource(), endNode)));
////                    map.getOverlays().clear();
//                    map.getOverlays().add(roadOverlay);
//                    map.invalidate();
//                }
//
//                int count=0;
//                List<Node> nodeList= new ArrayList<Node>();
//                for(int i=0; i<myGraph.nodeArray.size(); i++){
//                    if(myGraph.nodeArray.valueAt(i).largeEdgeConnections.size()==2){
//                        count++;
//                        nodeList.add(myGraph.nodeArray.valueAt(i));
//                    }
//
//                }

//                long sysDate = System.currentTimeMillis();
//                FloydWarshallShortestPaths<Node, Edge> floydWarshallShortestPaths = new FloydWarshallShortestPaths(graph);
//                floydWarshallShortestPaths.getPath(startNode, n1);
//                long time1 = (System.currentTimeMillis()-sysDate)/1000;



                Log.d("","start");
                int iter = 1;
                String visitedEdges = new String();
                String visitedEdges2 = new String();
                String visitedEdges3 = new String();
                String visitedEdges4 = new String();
                String visitedEdges5 = new String();
                String everyIterTime = new String();
                String everyIterTime2 = new String();
                String everyIterTime3 = new String();
                String everyIterTime4 = new String();
                String everyIterTime5 = new String();
                List<List<Edge>> pathInAllIter = new ArrayList<List<Edge>>();
                int edgeSize = myGraph.edgeList.size();
                int noVisitedEdge=0;
                int totalVisitedTimes = 0;
                double repeatWeight = 20;
                double crossWeight = 20;
                while (iter <= 1000) {


                    long sysDate = System.currentTimeMillis();
                    List<Edge> edgeList = rg.myGRASP(4);
                    double time = (System.currentTimeMillis()-sysDate)/1000.0;
                    pathInAllIter.add(edgeList);
                    if(rg.totalLength>limitation)
                        Log.d("","");
                    Log.d("return edge size",""+rg.returnEdgeList.size());
                    totalVisitedTimes=0;
                    for(int i=0; i<rg.returnEdgeList.size(); i++) {
                        rg.returnEdgeList.get(i).setVisitTimes(rg.returnEdgeList.get(i).getVisitTimes() + 1);
                        totalVisitedTimes += rg.returnEdgeList.get(i).getVisitTimes();
                    }
                    Polyline roadOverlay = new Polyline();
                    roadOverlay.setPoints(GraphUtils.nodesToGeos(GraphUtils.edgesToNodes(edgeList, startNode, endNode)));
                    roadOverlay.setWidth(4f);
//                    map.getOverlays().clear();
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                    routeLengthText.setText("Route Length:"+rg.totalLength);
                    iterationText.setText("Iteration:" + iter);
                    noVisitedEdge=0;

                    for(Edge edge:myGraph.edgeList){
                        if(edge.getVisitTimes()==0){
                            noVisitedEdge++;
                        }
                    }

                    visitedEdges = visitedEdges + "," + ((double)myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize;
                    everyIterTime = everyIterTime + "," + time;
                    Log.d("coverage" +
                            "",""+((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize));
                    if((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize==1.0)
                        break;
                    iter++;

                }
//                for(Edge edge:myGraph.edgeList){
//                    edge.setVisitTimes(0);
//                }
//                    map.getOverlays().clear();
//                iter=0;
//                while (iter <= 1000) {
//
//
//                    long sysDate = System.currentTimeMillis();
//                    List<Edge> edgeList = rg.myGRASP(2);
//                    long time = (System.currentTimeMillis()-sysDate)/1000;
//                    pathInAllIter.add(edgeList);
//                    if(rg.totalLength>limitation)
//                        Log.d("","");
//                    Log.d("return edge size",""+rg.returnEdgeList.size());
//                    totalVisitedTimes=0;
//                    for(int i=0; i<rg.returnEdgeList.size(); i++) {
//                        rg.returnEdgeList.get(i).setVisitTimes(rg.returnEdgeList.get(i).getVisitTimes() + 1);
//                        totalVisitedTimes += rg.returnEdgeList.get(i).getVisitTimes();
//                    }
//                    Polyline roadOverlay = new Polyline();
//                    roadOverlay.setPoints(GraphUtils.nodesToGeos(GraphUtils.edgesToNodes(edgeList, startNode, endNode)));
//                    roadOverlay.setWidth(4f);
////                    map.getOverlays().clear();
//                    map.getOverlays().add(roadOverlay);
//                    map.invalidate();
//                    routeLengthText.setText("Route Length:"+rg.totalLength);
//                    iterationText.setText("Iteration:" + iter);
//                    noVisitedEdge=0;
//
//                    for(Edge edge:myGraph.edgeList){
//                        if(edge.getVisitTimes()==0){
//                            noVisitedEdge++;
//                        }
//                    }
//
//                    visitedEdges2 = visitedEdges2 + "," + ((double)myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize;
//                    everyIterTime2 = everyIterTime2 + "," + time;
//                    Log.d("coverage" +
//                            "",""+((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize));
//                    if((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize==1.0)
//                        break;
//                    iter++;
//
//                }
//                for(Edge edge:myGraph.edgeList){
//                    edge.setVisitTimes(0);
//                }
//                map.getOverlays().clear();
//                iter=0;
//                while (iter <= 1000) {
//
//
//                    long sysDate = System.currentTimeMillis();
//                    List<Edge> edgeList = rg.myGRASP(3);
//                    long time = (System.currentTimeMillis()-sysDate)/1000;
//                    pathInAllIter.add(edgeList);
//                    if(rg.totalLength>limitation)
//                        Log.d("","");
//                    Log.d("return edge size",""+rg.returnEdgeList.size());
//                    totalVisitedTimes=0;
//                    for(int i=0; i<rg.returnEdgeList.size(); i++) {
//                        rg.returnEdgeList.get(i).setVisitTimes(rg.returnEdgeList.get(i).getVisitTimes() + 1);
//                        totalVisitedTimes += rg.returnEdgeList.get(i).getVisitTimes();
//                    }
//                    Polyline roadOverlay = new Polyline();
//                    roadOverlay.setPoints(GraphUtils.nodesToGeos(GraphUtils.edgesToNodes(edgeList, startNode, endNode)));
//                    roadOverlay.setWidth(4f);
////                    map.getOverlays().clear();
//                    map.getOverlays().add(roadOverlay);
//                    map.invalidate();
//                    routeLengthText.setText("Route Length:"+rg.totalLength);
//                    iterationText.setText("Iteration:" + iter);
//                    noVisitedEdge=0;
//
//                    for(Edge edge:myGraph.edgeList){
//                        if(edge.getVisitTimes()==0){
//                            noVisitedEdge++;
//                        }
//                    }
//
//                    visitedEdges3 = visitedEdges3 + "," + ((double)myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize;
//                    everyIterTime3 = everyIterTime3 + "," + time;
//                    Log.d("coverage" +
//                            "",""+((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize));
//                    if((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize==1.0)
//                        break;
//                    iter++;
//
//                }
//                for(Edge edge:myGraph.edgeList){
//                    edge.setVisitTimes(0);
//                }
//                map.getOverlays().clear();
//                iter=0;
//                while (iter <= 1000) {
//
//
//                    long sysDate = System.currentTimeMillis();
//                    List<Edge> edgeList = rg.myGRASP(4);
//                    long time = (System.currentTimeMillis()-sysDate)/1000;
//                    pathInAllIter.add(edgeList);
//                    if(rg.totalLength>limitation)
//                        Log.d("","");
//                    Log.d("return edge size",""+rg.returnEdgeList.size());
//                    totalVisitedTimes=0;
//                    for(int i=0; i<rg.returnEdgeList.size(); i++) {
//                        rg.returnEdgeList.get(i).setVisitTimes(rg.returnEdgeList.get(i).getVisitTimes() + 1);
//                        totalVisitedTimes += rg.returnEdgeList.get(i).getVisitTimes();
//                    }
//                    Polyline roadOverlay = new Polyline();
//                    roadOverlay.setPoints(GraphUtils.nodesToGeos(GraphUtils.edgesToNodes(edgeList, startNode, endNode)));
//                    roadOverlay.setWidth(4f);
////                    map.getOverlays().clear();
//                    map.getOverlays().add(roadOverlay);
//                    map.invalidate();
//                    routeLengthText.setText("Route Length:"+rg.totalLength);
//                    iterationText.setText("Iteration:" + iter);
//                    noVisitedEdge=0;
//
//                    for(Edge edge:myGraph.edgeList){
//                        if(edge.getVisitTimes()==0){
//                            noVisitedEdge++;
//                        }
//                    }
//
//                    visitedEdges4 = visitedEdges4 + "," + ((double)myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize;
//                    everyIterTime4 = everyIterTime4 + "," + time;
//                    Log.d("coverage" +
//                            "",""+((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize));
//                    if((double)(myGraph.edgeList.size() - noVisitedEdge)/(double)edgeSize==1.0)
//                        break;
//                    iter++;
//
//                }
                map = map;
            }
        };
        newThread.start();

    }

    public void setMarker(GeoPoint geoPoint){
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        map.invalidate();
    }

    public void readFromFile(){
        fileReader = new FileReader(this);
        List<String> mLines;
        mLines = fileReader.readLine(mPath);
        StringBuilder sb = new StringBuilder();
        for(String s : mLines)
            sb.append(s);
        response = sb.toString();
    }

    public void readDataFromOverpass(){
        Thread newThread = new Thread() {
            public void run() {
                String data = "<osm-script>\n" +
                        "  <union into=\"_\">\n" +
                        "    <query into=\"_\" type=\"way\">\n" +
                        "      <has-kv k=\"highway\" modv=\"\" regv=\"\"/>\n" +
//                        "      <has-kv k=\"area\" modv=\"not\" v=\"yes\"/>\n" +
//                        "      <has-kv k=\"highway\" modv=\"not\" regv=\"(path|steps)\"/>\n" +
                        "      <around radius=\""+ limitation/2 +"\" lat=\""+(startGeo.getLatitude()+endGeo.getLatitude())/2+"\" lon=\""+(endGeo.getLongitude()+startGeo.getLongitude())/2+"\"/>\n" +
                        "    </query>\n" +
                        "    <recurse from=\"_\" into=\"_\" type=\"down\"/>\n" +
                        "  </union>\n" +
                        "  <print e=\"\" from=\"_\" geometry=\"skeleton\" ids=\"yes\" limit=\"\" mode=\"body\" n=\"\" order=\"id\" s=\"\" w=\"\"/>\n" +
                        "</osm-script>";

                String url = "http://overpass-api.de/api/interpreter";
                String requestMethod = "POST";
                response = HTTPconn.requestPostData(data, url);
            }

            ;
        };
        newThread.start();

        try {
            newThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

}
