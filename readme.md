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

## clustered.xml configuration
```xml
<server name="node00" xmlns="urn:jboss:domain:1.6">
    ....
        <subsystem xmlns="urn:infinispan:server:core:6.3" default-cache-container="clustered">
            <cache-container name="clustered" default-cache="default" statistics="true">
                <transport executor="infinispan-transport" lock-timeout="60000"/>
                <distributed-cache name="default" mode="SYNC" segments="20" owners="2" remote-timeout="30000" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                    <transaction mode="NONE"/>
                </distributed-cache>
                <distributed-cache name="memcachedCache" mode="SYNC" segments="20" owners="2" remote-timeout="30000" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                    <transaction mode="NONE"/>
                </distributed-cache>
                <distributed-cache name="namedCache" mode="SYNC" start="EAGER"/>
				
				<replicated-cache name="balance" mode="SYNC" remote-timeout="300000" start="EAGER" statistics="true">
					<locking isolation="READ_COMMITTED" striping="false" acquire-timeout="100000" concurrency-level="1500"/>
					<transaction mode="NONE" locking="PESSIMISTIC"/>
				</replicated-cache>
				
            </cache-container>
            <cache-container name="security"/>
        </subsystem>
    .....
    <socket-binding-group name="standard-sockets" default-interface="public" port-offset="0">
        <socket-binding name="management-native" interface="management" port="${jboss.management.native.port:9999}"/>
        <socket-binding name="management-http" interface="management" port="${jboss.management.http.port:9990}"/>
        <socket-binding name="management-https" interface="management" port="${jboss.management.https.port:9443}"/>
        <socket-binding name="ajp" port="8009"/>
        <socket-binding name="hotrod" interface="management" port="11222"/>
        <socket-binding name="http" port="8080"/>
        <socket-binding name="https" port="8443"/>
        <socket-binding name="jgroups-mping" port="0" multicast-address="${jboss.default.multicast.address:234.99.54.14}" multicast-port="45700"/>
        <socket-binding name="jgroups-tcp" port="7600"/>
        <socket-binding name="jgroups-tcp-fd" port="57600"/>
        <socket-binding name="jgroups-udp" port="55200" multicast-address="${jboss.default.multicast.address:234.99.54.14}" multicast-port="45688"/>
        <socket-binding name="jgroups-udp-fd" port="54200"/>
        <socket-binding name="memcached" interface="management" port="11211"/>
        <socket-binding name="modcluster" port="0" multicast-address="224.0.1.115" multicast-port="23364"/>
        <socket-binding name="remoting" port="4447"/>
        <socket-binding name="txn-recovery-environment" port="4712"/>
        <socket-binding name="txn-status-manager" port="4713"/>
    </socket-binding-group>
</server>
```

## Infinispan Logs
```
$ standalone.bat -c clustered.xml

Calling "D:\software\jboss-datagrid-6.5.1-server-0\bin\standalone.conf.bat"
===============================================================================

  JBoss Bootstrap Environment

  JBOSS_HOME: D:\software\jboss-datagrid-6.5.1-server-0

  JAVA: D:\Java\jdk1.8.0_221\bin\java

  JAVA_OPTS: -XX:+TieredCompilation -XX:+UseCompressedOops -Dprogram.name=standalone.bat -Xms64M -Xmx512M  -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman

===============================================================================

10:06:56,600 INFO  [org.jboss.modules] (main) JBoss Modules version 1.3.6.Final-redhat-1
10:06:58,194 INFO  [org.jboss.msc] (main) JBoss MSC version 1.1.5.Final-redhat-1
10:06:58,340 INFO  [org.jboss.as] (MSC service thread 1-6) JBAS015899: JBoss Data Grid 6.5.1 (AS 7.5.0.Final-redhat-21) starting
10:07:06,380 INFO  [org.xnio] (MSC service thread 1-1) XNIO Version 3.0.13.GA-redhat-1
10:07:06,393 INFO  [org.jboss.as.server] (Controller Boot Thread) JBAS015888: Creating http management service using socket-binding (management-http)
10:07:06,399 INFO  [org.xnio.nio] (MSC service thread 1-1) XNIO NIO Implementation Version 3.0.13.GA-redhat-1
10:07:06,483 WARN  [org.jboss.as.txn] (ServerService Thread Pool -- 32) JBAS010153: Node identifier property is set to the default value. Please make sure it is unique.
10:07:06,504 INFO  [org.jboss.as.clustering.jgroups] (ServerService Thread Pool -- 25) JBAS010260: Activating JGroups subsystem.
10:07:06,507 INFO  [org.jboss.as.security] (ServerService Thread Pool -- 30) JBAS013171: Activating Security Subsystem
10:07:06,504 INFO  [org.jboss.as.naming] (ServerService Thread Pool -- 28) JBAS011800: Activating Naming Subsystem
10:07:06,531 INFO  [org.jboss.as.clustering.infinispan] (ServerService Thread Pool -- 22) JBAS010280: Activating Infinispan subsystem.
10:07:06,575 INFO  [org.jboss.as.security] (MSC service thread 1-4) JBAS013170: Current PicketBox version=4.1.1.Final-redhat-1
10:07:06,691 INFO  [org.jboss.as.connector.logging] (MSC service thread 1-14) JBAS010408: Starting JCA Subsystem (IronJacamar 1.0.31.Final-redhat-1)
10:07:06,860 INFO  [org.jboss.as.naming] (MSC service thread 1-3) JBAS011802: Starting Naming Service
10:07:07,195 INFO  [org.jboss.remoting] (MSC service thread 1-1) JBoss Remoting version 3.3.4.Final-redhat-1
10:07:07,690 INFO  [org.jboss.as.server.deployment.scanner] (MSC service thread 1-12) JBAS015012: Started FileSystemDeploymentService for directory D:\software\jboss-datagrid-6.5.1-server-0\standalone\deployments
10:07:09,186 INFO  [org.jboss.as.remoting] (MSC service thread 1-12) JBAS017100: Listening on 127.0.0.1:4447
10:07:09,186 INFO  [org.jboss.as.remoting] (MSC service thread 1-15) JBAS017100: Listening on 127.0.0.1:9999
10:07:10,622 INFO  [org.apache.coyote.ajp] (MSC service thread 1-4) JBWEB003046: Starting Coyote AJP/1.3 on ajp-/127.0.0.1:8009
10:07:10,621 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-10) JBWEB003001: Coyote HTTP/1.1 initializing on : http-/127.0.0.1:8080
10:07:10,626 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-10) JBWEB003000: Coyote HTTP/1.1 starting on: http-/127.0.0.1:8080
10:07:10,661 WARN  [org.jgroups.stack.Configurator] (MSC service thread 1-1) JGRP000014: TP.singleton_name has been deprecated: Use fork channels instead
10:07:10,676 INFO  [org.jboss.modcluster] (ServerService Thread Pool -- 34) MODCLUSTER000001: Initializing mod_cluster version 1.2.11.Final-redhat-1
10:07:10,701 INFO  [org.jboss.modcluster] (ServerService Thread Pool -- 34) MODCLUSTER000032: Listening to proxy advertisements on /224.0.1.115:23364
10:07:13,562 INFO  [org.infinispan.factories.GlobalComponentRegistry] (MSC service thread 1-10) ISPN000128: Infinispan version: Infinispan 'Infinium' 6.3.1.CR1-redhat-1
10:07:15,239 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-10) JBAS010281: Started ___protobuf_metadata cache from security container
10:07:15,254 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-9) JBAS010281: Started other cache from security container
10:07:15,255 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-10) JBAS010281: Started jboss-web-policy cache from security container
10:07:19,110 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-11) JDGS010000: REST starting
10:07:19,116 INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport] (MSC service thread 1-7) ISPN000078: Starting JGroups Channel
10:07:20,725 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-11) JDGS010002: REST mapped to /rest
10:07:34,209 INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport] (MSC service thread 1-7) ISPN000094: Received new cluster view: [node00/clustered|0] (1) [node00/clustered]
10:07:34,652 INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport] (MSC service thread 1-7) ISPN000079: Cache local address is node00/clustered, physical addresses are [127.0.0.1:7600]
10:07:35,029 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-5) JBAS010281: Started ___protobuf_metadata cache from clustered container
10:07:35,042 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-5) JBAS010281: Started balance cache from clustered container
10:07:35,078 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-6) JBAS010281: Started memcachedCache cache from clustered container
10:07:35,088 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-13) JBAS010281: Started default cache from clustered container
10:07:35,082 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-7) JBAS010281: Started namedCache cache from clustered container
10:07:35,096 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-13) JDGS010000: MemcachedServer starting
10:07:35,095 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-8) JDGS010000: HotRodServer starting
10:07:35,104 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-13) JDGS010001: MemcachedServer listening on 127.0.0.1:11211
10:07:35,106 INFO  [org.infinispan.server.endpoint] (MSC service thread 1-8) JDGS010001: HotRodServer listening on 127.0.0.1:11222
10:07:39,575 INFO  [org.jboss.as.clustering.infinispan] (MSC service thread 1-8) JBAS010281: Started ___hotRodTopologyCache cache from clustered container
10:07:39,822 INFO  [org.jboss.as] (Controller Boot Thread) JBAS015961: Http management interface listening on http://127.0.0.1:9990/management
10:07:39,824 INFO  [org.jboss.as] (Controller Boot Thread) JBAS015951: Admin console listening on http://127.0.0.1:9990
10:07:39,830 INFO  [org.jboss.as] (Controller Boot Thread) JBAS015874: JBoss Data Grid 6.5.1 (AS 7.5.0.Final-redhat-21) started in 46046ms - Started 144 of 156 services (54 services are lazy, passive or on-demand)
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