package com.mark.resource;

public class Marker implements Comparable<Marker> {
    public  long time;     // in milliseconds
    public  boolean select;

    public transient int work;      // work variable

    public Marker(long time) {
        this.time = time;
    }

    // this version creates a temporary working instance for deserialization from the hash table
    // input should have the right count of values in the right sequence with the right delimiter.
    public Marker(String stored) {
        this.select = stored.substring(0, 1).equals("1");
        this.time = Long.parseLong(stored.substring(2));
    }

    @Override
    public int compareTo(Marker o) {
        return time == o.time ? 0 : time < o.time ? - 1 : 1;
    }

    public String toStore() {
        return String.format("%d:%d", select ? 1 : 0, time);
    }
}
