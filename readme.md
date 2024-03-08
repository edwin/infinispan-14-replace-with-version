# Infinispan 14 and replaceWithVersion

## About
Java project to simulate `replaceWithVersion` on Red Hat Datagrid 6.5.1. 

## Cache Configuration
```xml
<replicated-cache name="balance" mode="SYNC" remote-timeout="300000" start="EAGER" statistics="true">
    <locking isolation="READ_COMMITTED" striping="false" acquire-timeout="100000" concurrency-level="1500"/>
    <transaction mode="NONE" locking="PESSIMISTIC"/>
</replicated-cache>
```