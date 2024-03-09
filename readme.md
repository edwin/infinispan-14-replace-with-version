# Infinispan 6.3.1 and replaceWithVersion

## About
Java project to simulate `replaceWithVersion` on Red Hat Datagrid 6.5.1 (Infinispan 6.3.1). 

## Cache Configuration
```xml
<replicated-cache name="balance" mode="SYNC" remote-timeout="300000" start="EAGER" statistics="true">
    <locking isolation="READ_COMMITTED" striping="false" acquire-timeout="100000" concurrency-level="1500"/>
    <transaction mode="NONE" locking="PESSIMISTIC"/>
</replicated-cache>
```

## Infinispan Logs
```
===============================================================================

  JBoss Bootstrap Environment

  JBOSS_HOME: D:\software\jboss-datagrid-6.5.1-server-0

  JAVA: D:\Java\jdk1.8.0_221\bin\java

  JAVA_OPTS: -XX:+TieredCompilation -XX:+UseCompressedOops -Dprogram.name=standalone.bat -Xms64M -Xmx512M  -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman

===============================================================================

11:54:13,816 INFO  [org.jboss.modules] (main) JBoss Modules version 1.3.6.Final-redhat-1
11:54:16,591 INFO  [org.jboss.msc] (main) JBoss MSC version 1.1.5.Final-redhat-1
11:54:16,767 INFO  [org.jboss.as] (MSC service thread 1-6) JBAS015899: JBoss Data Grid 6.5.1 (AS 7.5.0.Final-redhat-21) starting
11:54:21,894 INFO  [org.xnio] (MSC service thread 1-2) XNIO Version 3.0.13.GA-redhat-1
11:54:21,901 INFO  [org.jboss.as.server] (Controller Boot Thread) JBAS015888: Creating http management service using socket-binding (management-http)
11:54:21,905 INFO  [org.xnio.nio] (MSC service thread 1-2) XNIO NIO Implementation Version 3.0.13.GA-redhat-1
11:54:21,972 WARN  [org.jboss.as.txn] (ServerService Thread Pool -- 28) JBAS010153: Node identifier property is set to the default value. Please make sure it is unique.
11:54:21,982 INFO  [org.jboss.as.clustering.infinispan] (ServerService Thread Pool -- 20) JBAS010280: Activating Infinispan subsystem.
11:54:21,977 INFO  [org.jboss.as.naming] (ServerService Thread Pool -- 24) JBAS011800: Activating Naming Subsystem
11:54:21,979 INFO  [org.jboss.as.security] (ServerService Thread Pool -- 26) JBAS013171: Activating Security Subsystem
11:54:22,047 INFO  [org.jboss.as.security] (MSC service thread 1-5) JBAS013170: Current PicketBox version=4.1.1.Final-redhat-1
11:54:22,099 INFO  [org.jboss.as.connector.logging] (MSC service thread 1-8) JBAS010408: Starting JCA Subsystem (IronJacamar 1.0.31.Final-redhat-1)
11:54:22,252 INFO  [org.jboss.as.naming] (MSC service thread 1-3) JBAS011802: Starting Naming Service
11:54:22,510 INFO  [org.jboss.remoting] (MSC service thread 1-2) JBoss Remoting version 3.3.4.Final-redhat-1
11:54:22,953 INFO  [org.jboss.as.server.deployment.scanner] (MSC service thread 1-3) JBAS015012: Started FileSystemDeploymentService for directory D:\software\jboss-datagrid-6.5.1-server-0\standalone\deployments
11:54:24,142 INFO  [org.jboss.as.remoting] (MSC service thread 1-16) JBAS017100: Listening on 127.0.0.1:9999
11:54:24,142 INFO  [org.jboss.as.remoting] (MSC service thread 1-4) JBAS017100: Listening on 127.0.0.1:4447
11:54:24,188 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-15) JBWEB003001: Coyote HTTP/1.1 initializing on : http-/127.0.0.1:8080
11:54:24,188 INFO  [org.apache.coyote.ajp] (MSC service thread 1-14) JBWEB003046: Starting Coyote AJP/1.3 on ajp-/127.0.0.1:8009
11:54:24,190 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-15) JBWEB003000: Coyote HTTP/1.1 starting on: http-/127.0.0.1:8080
11:54:25,243 INFO  [org.infinispan.factories.GlobalComponentRegistry] (MSC service thread 1-5) ISPN000128: Infinispan version: Infinispan 'Infinium' 6.3.1.CR1-redhat-1
11:54:25,243 INFO  [org.infinispan.factories.GlobalComponentRegistry] (MSC service thread 1-9) ISPN000128: Infinispan version: Infinispan 'Infinium' 6.3.1.CR1-redhat-1
11:54:26,047 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-3) JBAS010281: Started ___protobuf_metadata cache from local container
11:54:26,047 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-9) JBAS010281: Started ___protobuf_metadata cache from security container
11:54:26,071 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-5) JBAS010281: Started memcachedCache cache from local container
11:54:26,071 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-9) JBAS010281: Started other cache from security container
11:54:26,071 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-11) JBAS010281: Started namedCache cache from local container
11:54:26,071 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-3) JBAS010281: Started default cache from local container
11:54:26,071 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-1) JBAS010281: Started jboss-web-policy cache from security container
11:54:26,072 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-8) JBAS010281: Started balance cache from local container
11:54:26,078 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-3) JDGS010000: MemcachedServer starting
11:54:26,078 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-7) JDGS010000: HotRodServer starting
11:54:26,079 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-3) JDGS010001: MemcachedServer listening on 127.0.0.1:11211
11:54:26,080 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-7) JDGS010001: HotRodServer listening on 127.0.0.1:11222
11:54:26,086 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-9) JDGS010000: REST starting
11:54:26,628 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-9) JDGS010002: REST mapped to /rest
11:54:27,843 INFO  [org.jboss.as] (Controller Boot Thread) JBAS015961: Http management interface listening on http://127.0.0.1:9990/management
11:54:27,845 INFO  [org.jboss.as] (Controller Boot Thread) JBAS015951: Admin console listening on http://127.0.0.1:9990
11:54:27,846 INFO  [org.jboss.as] (Controller Boot Thread) JBAS015874: JBoss Data Grid 6.5.1 (AS 7.5.0.Final-redhat-21) started in 21338ms - Started 135 of 142 services (41 services are lazy, passive or on-demand)
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
|  1 | 15300 |
|  2 | 9892 |
|  3 | 13412 |
|  4 | 14778 |
|  5 | 22640 |