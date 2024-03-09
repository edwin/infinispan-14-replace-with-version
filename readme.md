# Infinispan 14 and replaceWithVersion

## About
Java project to simulate `replaceWithVersion` on Infinispan 14. 

## Cache Configuration
```json
{
  "distributed-cache": {
    "mode": "SYNC",
    "remote-timeout": "30000",
    "statistics": true,
    "encoding": {
      "media-type": "text/plain"
    },
    "locking": {
      "concurrency-level": "1000",
      "isolation": "READ_COMMITTED",
      "acquire-timeout": "300000",
      "striping": false
    },
    "transaction": {
      "mode": "NON_XA",
      "auto-commit": true,
      "stop-timeout": "300000",
      "locking": "PESSIMISTIC",
      "reaper-interval": "300000",
      "complete-timeout": "300000",
      "notifications": true
    },
    "memory": {
      "storage": "OFF_HEAP",
      "max-size": "30000000000"
    },
    "state-transfer": {
      "timeout": "300000"
    }
  }
}
```

## Infinispan Logs
```
2024-03-09 12:25:38,423 INFO  (main) [BOOT] JVM OpenJDK 64-Bit Server VM Red Hat, Inc. 17.0.6+10-LTS
2024-03-09 12:25:38,437 INFO  (main) [BOOT] JVM arguments = []
2024-03-09 12:25:38,438 INFO  (main) [BOOT] PID = 26288
2024-03-09 12:25:38,473 INFO  (main) [org.infinispan.SERVER] ISPN080000: Infinispan Server 14.0.17.Final starting
2024-03-09 12:25:38,473 INFO  (main) [org.infinispan.SERVER] ISPN080017: Server configuration: infinispan.xml
2024-03-09 12:25:38,474 INFO  (main) [org.infinispan.SERVER] ISPN080032: Logging configuration: D:\software\infinispan-server-14.0.17.Final\server\conf\log4j2.xml
2024-03-09 12:25:40,255 INFO  (main) [org.infinispan.SERVER] ISPN080027: Loaded extension 'query-dsl-filter-converter-factory'
2024-03-09 12:25:40,255 INFO  (main) [org.infinispan.SERVER] ISPN080027: Loaded extension 'continuous-query-filter-converter-factory'
2024-03-09 12:25:40,258 INFO  (main) [org.infinispan.SERVER] ISPN080027: Loaded extension 'iteration-filter-converter-factory'
2024-03-09 12:25:40,350 INFO  (main) [org.infinispan.SERVER] ISPN080027: Loaded extension 'com.mysql.cj.jdbc.Driver'
2024-03-09 12:25:40,350 INFO  (main) [org.infinispan.SERVER] ISPN080027: Loaded extension 'oracle.jdbc.OracleDriver'
2024-03-09 12:25:40,351 WARN  (main) [org.infinispan.SERVER] ISPN080059: No script engines are available
2024-03-09 12:25:41,156 WARN  (main) [org.infinispan.PERSISTENCE] ISPN000554: jboss-marshalling is deprecated and planned for removal
2024-03-09 12:25:41,194 INFO  (main) [org.infinispan.CONTAINER] ISPN000556: Starting user marshaller 'org.infinispan.commons.marshall.ImmutableProtoStreamMarshaller'
2024-03-09 12:25:41,544 INFO  (main) [org.infinispan.CONTAINER] ISPN000389: Loaded global state, version=14.0.17.Final timestamp=2024-03-08T15:25:00.078514500Z
2024-03-09 12:26:09,937 INFO  (main) [org.infinispan.CLUSTER] ISPN000078: Starting JGroups channel `cluster` with stack `tcp`
2024-03-09 12:26:12,724 INFO  (main) [org.jgroups.JChannel] local_addr: 4a851fa2-1a3e-4c91-859c-ab7ac38b9fea, name: DESKTOP-1P6ABDF-51911
2024-03-09 12:26:12,832 INFO  (main) [org.jgroups.protocols.FD_SOCK2] server listening on *.57800
2024-03-09 12:26:14,839 INFO  (main) [org.jgroups.protocols.pbcast.GMS] DESKTOP-1P6ABDF-51911: no members discovered after 2002 ms: creating cluster as coordinator
2024-03-09 12:26:14,848 INFO  (main) [org.infinispan.CLUSTER] ISPN000094: Received new cluster view for channel cluster: [DESKTOP-1P6ABDF-51911|0] (1) [DESKTOP-1P6ABDF-51911]
2024-03-09 12:26:14,904 INFO  (main) [org.infinispan.CLUSTER] ISPN000079: Channel `cluster` local address is `DESKTOP-1P6ABDF-51911`, physical addresses are `[192.168.8.120:7800]`
2024-03-09 12:26:15,499 INFO  (main) [org.infinispan.CONTAINER] ISPN000104: Using EmbeddedTransactionManager
2024-03-09 12:26:15,825 INFO  (main) [org.infinispan.SERVER] ISPN080018: Started connector Resp (internal)
2024-03-09 12:26:15,826 INFO  (ForkJoinPool.commonPool-worker-2) [org.infinispan.server.core.telemetry.TelemetryServiceFactory] ISPN000953: OpenTelemetry integration is disabled
2024-03-09 12:26:15,856 INFO  (ForkJoinPool.commonPool-worker-1) [org.infinispan.SERVER] ISPN080018: Started connector HotRod (internal)
2024-03-09 12:26:15,930 INFO  (ForkJoinPool.commonPool-worker-2) [org.infinispan.SERVER] ISPN080018: Started connector REST (internal)
2024-03-09 12:26:15,947 INFO  (main) [org.infinispan.SERVER] Using transport: NIO
2024-03-09 12:26:16,260 INFO  (main) [org.infinispan.SERVER] ISPN080004: Connector SinglePort (default) listening on 127.0.0.1:11222
2024-03-09 12:26:16,260 INFO  (main) [org.infinispan.SERVER] ISPN080034: Server 'DESKTOP-1P6ABDF-51911' listening on http://127.0.0.1:11222
2024-03-09 12:26:16,289 INFO  (main) [org.infinispan.SERVER] ISPN080001: Infinispan Server 14.0.17.Final started in 37814ms
```

## Testing Methodology
`5000` data updated concurrently by `100` different threads using `replaceWithversion`. Retry would happen if each threads unable to update the cache value.
```java

    private static final int NUM_EXECUTORS = 100;
    private static final int NUM_ENTRIES = 5000;

```

## Testing Result
| Attempt  | Number of Retries |
|----------| -- |
|  1 | 17435 |
|  2 | 12061 |
|  3 | 15423 |
|  4 | 9149 |
|  5 | 14871 |