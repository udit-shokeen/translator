package com.example.enums;

import java.util.HashMap;
import java.util.Map;


public enum DB {
    IN_MEMORY,
    REDIS;

    private static final Map<String, DB> dbMap = new HashMap<>();

    static {
        for (DB db : DB.values()) {
            dbMap.put(db.name().toLowerCase(), db);
        }
    }

    public static DB getDBFromString(String dbName) {
        return dbMap.getOrDefault(dbName.toLowerCase(), IN_MEMORY);
    }
}
