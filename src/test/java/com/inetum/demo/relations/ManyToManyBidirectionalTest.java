package com.inetum.demo.relations;

import com.inetum.demo.domain.manytomany.Role;
import com.inetum.demo.domain.manytomany.User;
import com.inetum.demo.repositories.manytomany.RoleRepository;
import com.inetum.demo.repositories.manytomany.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@Slf4j
public class ManyToManyBidirectionalTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void saveRole(){
        User user = new User();
        user.setFirstName("pepesan");
        this.userRepository.save(user);

        User admin = new User();
        admin.setFirstName("admin");
        this.userRepository.save(admin);

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        roleAdmin.getUsers().add(user);
        roleAdmin.getUsers().add(admin);

        roleRepository.save(roleAdmin);
        log.info(""+user);
        log.info(""+admin);
        log.info(""+roleAdmin);
        assertTrue(roleAdmin.getUsers().contains(admin));
    }

    @Test
    void fetchRole(){
        List<Role> roles = roleRepository.findAll();
        System.out.println(roles);
    }
}
