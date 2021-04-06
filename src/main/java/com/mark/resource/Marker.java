package com.mark.resource;

public class Marker implements Comparable<Marker> {
    public  long time;     // in milliseconds
    public  boolean select;

    public Marker(long time) {
        this.time = time;
    }

    @Override
    public int compareTo(Marker o) {
        return time == o.time ? 0 : time < o.time ? - 1 : 1;
    }
}
