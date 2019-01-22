package com.sh.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author sh
 */
@Service
public class RedisClient {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置一个对象 可以是string
     *
     * @param key
     * @param obj
     * @param seconds 超时时间 0 代表永不过期
     */
    public <T> void set(final String key, final T obj, final int seconds) {
        redisTemplate.execute(new RedisCallback<T>() {
            @Override
            public T doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] byteKey = getBytesKey(key);
                connection.set(byteKey, toBytes(obj));
                if (seconds != 0) {
                    connection.expire(byteKey, seconds);
                }
                return null;
            }
        });
    }

    /**
     * 设置一个对象 可以是string
     *
     * @param key
     * @param clazz
     */
    public <T> T get(final String key, Class<T> clazz) {
        return (T) redisTemplate.execute(new RedisCallback<T>() {
            @Override
            public T doInRedis(RedisConnection connection)
                    throws DataAccessException {
                T returnValue = null;
                byte[] byteKey = getBytesKey(key);
                if (connection.exists(byteKey)) {
                    returnValue = (T) toObject(connection.get(byteKey));
                }
                return returnValue;
            }
        });
    }

    /**
     * 取一个对象测试
     */
    public <T> void set(final String key, final T obj) {
        this.set(key, obj, 0);
    }

    /**
     * 设置字符串
     *
     * @param key
     * @param value
     * @param seconds 超时时间 0 代表永不过期
     */
    public void setString(final String key, final String value, final int seconds) {
        this.set(key, value, seconds);
    }

    /**
     * 设置字符串
     *
     * @param key
     * @param value
     */
    public void setString(final String key, final String value) {
        this.set(key, value);
    }

    /**
     * 检查给定 key 是否存在。
     *
     * @param key
     * @return 若 key 存在，返回 true ，否则返回 false 。
     */
    public Boolean exists(final String key) {
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.exists(getBytesKey(key));
            }
        });
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key
     * @param seconds 单位秒
     * @return 设置成功返回true  ，反之false
     */
    public Boolean expire(final String key, final int seconds) {
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.expire(getBytesKey(key), seconds);
            }
        });
    }

    /**
     * 移除给定 key 的生存时间
     *
     * @param key
     * @return 当生存时间移除成功时true  如果 key 不存在或 key 没有设置生存时间，返回 false
     */
    public Boolean persist(final String key) {
        return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.persist(getBytesKey(key));
            }
        });
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间
     *
     * @param key
     * @return 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以秒为单位，返回 key 的剩余生存时间。
     */
    public Long ttl(final String key) {
        return (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] byteKey = getBytesKey(key);
                return connection.ttl(byteKey);
            }
        });
    }

    /**
     * 获取byte[]类型Key
     *
     * @param object
     * @return
     */
    private byte[] getBytesKey(Object object) {
        if (!(object instanceof String)) {
            return serialize(object);
        }
        try {
            return ((String) object).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ((String) object).getBytes();
        }
    }

    /**
     * Object转换byte[]类型
     *
     * @param object
     * @return
     */
    private byte[] toBytes(Object object) {
        return serialize(object);
    }

    /**
     * byte[]型转换Object
     *
     * @param bytes
     * @return
     */
    private Object toObject(byte[] bytes) {
        return unSerialize(bytes);
    }

    /**
     * 序列化对象
     *
     * @param object
     * @return
     */
    private byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化对象
     *
     * @param bytes
     * @return
     */
    private Object unSerialize(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
