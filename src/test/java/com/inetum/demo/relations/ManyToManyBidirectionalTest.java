package com.inetum.demo.relations;

import com.inetum.demo.domain.manytomany.Role;
import com.inetum.demo.domain.manytomany.User;
import com.inetum.demo.repositories.manytomany.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ManyToManyBidirectionalTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void saveRole(){
        User user = new User();
        user.setFirstName("ramesh");


        User admin = new User();
        admin.setFirstName("admin");


        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        roleAdmin.getUsers().add(user);
        roleAdmin.getUsers().add(admin);

        roleRepository.save(roleAdmin);
    }

    @Test
    void fetchRole(){
        List<Role> roles = roleRepository.findAll();
        System.out.println(roles);
    }
}
