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