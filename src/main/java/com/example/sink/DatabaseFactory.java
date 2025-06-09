package com.example.sink;

import com.example.enums.DB;
import java.util.Map;


public class DatabaseFactory {
    private static final Map<DB, Database> databaseImplMap = Map.of(
            DB.IN_MEMORY, new InMemorySink(),
            DB.REDIS, new RedisSink()
    );

    public static Database getDatabaseImpl(DB config) {
        return databaseImplMap.getOrDefault(config, databaseImplMap.get(DB.IN_MEMORY));
    }
}
