package com.sh.test;

import com.sh.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author sh
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring.xml"})
public class ClusterTest {

    @Autowired
    private UserService userService;

    @Test
    public void testSetUserName() {
        userService.setUserName("101", "史恒");
    }

    @Test
    public void testGetUserName() {
        String name = userService.getUserName("101");
        System.out.println(name);
    }
}
