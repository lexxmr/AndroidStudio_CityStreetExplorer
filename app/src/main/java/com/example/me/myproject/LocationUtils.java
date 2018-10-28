package com.example.me.myproject;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Me on 2018/6/8.
 */

public class LocationUtils {
    private static double EARTH_RADIUS = 6371009;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow( Math.sin(b / 2), 2);
        s = Math.min(1.0, s);
        return 2*Math.asin(Math.sqrt(s))*EARTH_RADIUS;
    }

    public static double getDistance(List<GeoPoint> geoPoints){
        if (geoPoints.size() == 0)
            return 0.0;
        GeoPoint prePoint = geoPoints.get(0);
        double distance = 0.0;
        for (GeoPoint point: geoPoints) {
            distance += getDistance(prePoint.getLatitude(), prePoint.getLongitude(), point.getLatitude(), point.getLongitude());
            prePoint = point;
        }
        return distance;
    }

    public static double getDistance(GeoPoint geoPoint1, GeoPoint geoPoint2){
        double lat1 = geoPoint1.getLatitude();
        double lat2 = geoPoint2.getLatitude();
        double lng1 = geoPoint1.getLongitude();
        double lng2 = geoPoint2.getLongitude();
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow( Math.sin(b / 2), 2);
        s = Math.min(1.0, s);
        return 2*Math.asin(Math.sqrt(s))*EARTH_RADIUS;
    }

    public static double getDistance(Node node1, Node node2){
        double radLat1 = rad(node1.getLat());
        double radLat2 = rad(node2.getLat());
        double a = radLat1 - radLat2;
        double b = rad(node1.getLon()) - rad(node2.getLon());
        double s = Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow( Math.sin(b / 2), 2);
        s = Math.min(1.0, s);
        return 2*Math.asin(Math.sqrt(s))*EARTH_RADIUS;
    }

//    public static double getDistance(GeoPoint geoPoint1, GeoPoint geoPoint2){
//        double lat1 = geoPoint1.getLatitude();
//        double lat2 = geoPoint2.getLatitude();
//        double lng1 = geoPoint1.getLongitude();
//        double lng2 = geoPoint2.getLongitude();
//        double radLat1 = rad(lat1);
//        double radLat2 = rad(lat2);
//        double a = radLat1 - radLat2;
//        double b = rad(lng1) - rad(lng2);
//        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//                + Math.cos(radLat1) * Math.cos(radLat2)
//                * Math.pow(Math.sin(b / 2), 2)));
//        s = s * EARTH_RADIUS;
//        s = Math.round(s * 10000d) / 10000d;
//        s = s*1000;
//        return s;
//    }
//
//    public static double getDistance(Node node1, Node node2){
//        double radLat1 = rad(node1.getLat());
//        double radLat2 = rad(node2.getLat());
//        double a = radLat1 - radLat2;
//        double b = rad(node1.getLon()) - rad(node2.getLon());
//        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//                + Math.cos(radLat1) * Math.cos(radLat2)
//                * Math.pow(Math.sin(b / 2), 2)));
//        s = s * EARTH_RADIUS;
//        s = Math.round(s * 10000d) / 10000d;
//        s = s*1000;
//        return s;
//    }
}