package com.mark.utils;

import com.mark.Log;
import com.mark.Prefs;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

public class HashStore {
    //public static HashStore Instance = new HashStore();

    private final DB db;
    private final ConcurrentMap map;

    public static void main(String[] args) {
        //HashStore hashStore = HashStore.Instance;

        String hashStoreOK = HashStore.checkHashStoreDB();
        if (hashStoreOK != null) {
            Log.log("Hash Store is not set");
            return;
        }

        HashStore hashStore = new HashStore();

        //hashStore.put("h1", "v1");
        //hashStore.put("h2", "v2");

        Log.log("Hash store size: %d", hashStore.map.size());
        Log.log("map get 1: %s", hashStore.get("h1"));
        Log.log("map get 2: %s", hashStore.get("h2"));

        hashStore.close();
    }

    public static String checkHashStoreDB() {
        String dbPath = Prefs.getHashStoreDBPath();
        if (dbPath.isEmpty()) {
            return "Hash Store Database path is not set yet (Settings -> Hash Store).";
        }

        File dbFile = new File(Prefs.getHashStoreDBPath());
        if (!dbFile.exists()) {
            return String.format("Hash Store Database '%s' does not exist (Settings -> Hash Store).");
        }

        return null;        // OK
    }

    public HashStore() {
        this.db = DBMaker.fileDB(Prefs.getHashStoreDBPath()).fileMmapEnable().transactionEnable().make();
        this.map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
    }

    // just to get the singleton to come to live
    public void init() {
        Log.log("HashStore (%s)", db.isClosed() ? "closed" : "open");
    }

    public void close() {
        db.close();
    }

    public void commit() {
        db.commit();
    }

    public void put(String hash, String value) {
        this.put(hash, value, true);
    }

    public void put(String hash, String value, boolean commit) {
        map.put(hash, value);

        if (commit) {
            db.commit();
        }
    }

    public String get(String hash) {
        return (String)map.get(hash);
    }
}