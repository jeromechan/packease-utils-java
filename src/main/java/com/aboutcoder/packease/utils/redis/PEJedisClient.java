/**************************************************************************************** 
        途牛科技有限公司
 ****************************************************************************************/
package com.aboutcoder.packease.utils.redis;

import com.aboutcoder.packease.utils.string.PESerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.ShardedJedis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * <Description>
 * Copyright © 2013-2016 AboutCoder.COM. All rights reserved.<br />
 *
 * @author jeromechan<br />
 * @CreateDate 11/13/16 2:51 PM<br />
 * @description <br />
 */
@Repository("peJedisClient")
public class PEJedisClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PEJedisClient.class);

    @Autowired
    private PEJedisDataSource redisDataSource;

    /**
     * Disconnect redis datasource
     */
    public void disconnect() {
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        shardedJedis.disconnect();
    }

    /**
     * To delete the specific redis k-v pair.
     *
     * @param key
     */
    public void del(String key) {
        byte[] keyBytes = PESerializationUtils.stringSerialize(key);
        redisDataSource.getRedisClient().del(keyBytes);
        LOGGER.info("delete redis key:{} ", key);
    }

    /**
     * Setting the single object with serialization.
     *
     * @param key
     * @param object
     * @param expireTime
     * @param <T>
     */
    public <T extends Serializable> void setSerializableValue(final String key, T object, int expireTime) {
        byte[] valueBytes = PESerializationUtils.serialize(object);
        if (null != valueBytes) {
            byte[] keyBytes = PESerializationUtils.stringSerialize(key);
            redisDataSource.getRedisClient().setex(keyBytes, expireTime, valueBytes);
        }
    }

    /**
     * To get the single object with Class.
     *
     * @param key
     * @param <T>
     */
    public <T extends Serializable> T getSerializableValue(final String key, Class<T> clazz) {
        byte[] keyBytes = PESerializationUtils.stringSerialize(key);
        byte[] value = redisDataSource.getRedisClient().get(keyBytes);
        if (null != value) {
            return clazz.cast(PESerializationUtils.deserialize(value));
        } else {
            return null;
        }
    }

    /**
     * Setting the List object with serialization.
     *
     * @param key
     * @param object
     * @param expireTime
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Serializable> void setSerializableList(final String key, List<T> object, int expireTime) {
        PEJedisListVo baseRedisListVo = new PEJedisListVo(object);
        byte[] valueBytes = PESerializationUtils.serialize(baseRedisListVo);
        if (null != valueBytes) {
            byte[] keyBytes = PESerializationUtils.stringSerialize(key);
            redisDataSource.getRedisClient().setex(keyBytes, expireTime, valueBytes);
        }
    }

    /**
     * To get the List object with serialization.
     *
     * @param key
     * @param <T>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Serializable> List<T> getSerializableList(final String key) {
        byte[] keyBytes = PESerializationUtils.stringSerialize(key);
        byte[] valueBytes = redisDataSource.getRedisClient().get(keyBytes);
        try {
            if (null == valueBytes) {
                return null;
            }

            Object value = PESerializationUtils.deserialize(valueBytes);
            if (null == value) {
                return null;
            }

            return ((PEJedisListVo) value).getValueList();
        } catch (Exception e) {
            LOGGER.error("BaseRedisClient-getSerializableList", e);
            return null;
        }
    }

    /**
     * Setting the Map object with serialization.
     *
     * @param key
     * @param mapData
     * @param expireTime
     */
    public <K, V extends Serializable> void setSerializableMap(final String key, Map<K, V> mapData, int expireTime) {
        PEJedisMapVo baseRedisMapVo = new PEJedisMapVo<K, V>(mapData);
        byte[] valueBytes = PESerializationUtils.serialize(baseRedisMapVo);
        if (null != valueBytes) {
            byte[] keyBytes = PESerializationUtils.stringSerialize(key);
            redisDataSource.getRedisClient().setex(keyBytes, expireTime, valueBytes);
        }
    }

    /**
     * To get the Map object with serialization.
     *
     * @param key
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <K, V extends Serializable> Map<K, V> getSerializableMap(final String key) {
        byte[] keyBytes = PESerializationUtils.stringSerialize(key);
        byte[] valueBytes = redisDataSource.getRedisClient().get(keyBytes);
        try {
            if (null == valueBytes) {
                return null;
            }

            Object value = PESerializationUtils.deserialize(valueBytes);
            if (null == value) {
                return null;
            }

            return ((PEJedisMapVo) value).getValueMap();
        } catch (Exception e) {
            LOGGER.error("BaseRedisClient-getSerializableList", e);
            return null;
        }
    }

}