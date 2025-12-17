package com.sleekydz86.service.healthcare.service.sharding;

import org.springframework.stereotype.Component;

@Component
public class ShardingKeyGenerator {

    public int generateShardKey(String regionId) {
        return Math.abs(regionId.hashCode()) % 4;
    }

    public int generateShardKey(Long patientId) {
        return (int) (patientId % 4);
    }

    public int generateShardKey(Integer dateHash) {
        return dateHash % 4;
    }
}

