package com.xpg.usercenter;
import java.util.Date;

import com.xpg.usercenter.mapper.UserMapper;
import com.xpg.usercenter.model.domain.User;
import com.xpg.usercenter.service.UserService;
import com.xpg.usercenter.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import net.bytebuddy.asm.MemberSubstitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UsercenterApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {



    }

}
