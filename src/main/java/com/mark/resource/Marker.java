package com.mark.resource;

public class Marker implements Comparable<Marker> {
    public  float position;     // in seconds
    public  long time;          // in milliseconds
    public  boolean select;

    public Marker(float position) {
        this.position = position;
        this.time = (long)position*1000;
    }

    @Override
    public int compareTo(Marker o) {
        return time == o.time ? 0 : time < o.time ? - 1 : 1;
    }
}
