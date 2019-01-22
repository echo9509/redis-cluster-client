package com.sh.service.impl;

import com.sh.redis.RedisClient;
import com.sh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sh
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisClient redisClient;

    @Override
    public void setUserName(String sessionId, String name) {
        redisClient.set(sessionId, name);
    }

    @Override
    public String getUserName(String sessionId) {
        return redisClient.get(sessionId, String.class);
    }
}
