package com.sh.service;

/**
 * @author sh
 */
public interface UserService {

    void setUserName(String sessionId, String userName);

    String getUserName(String sessionId);
}
