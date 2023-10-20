package com.inetum.demo.services.manytomany;

import com.inetum.demo.domain.manytomany.Role;
import com.inetum.demo.domain.manytomany.User;
import com.inetum.demo.repositories.manytomany.RoleRepository;
import com.inetum.demo.repositories.manytomany.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManyToManyService {
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Autowired
    ManyToManyService(
        UserRepository userRepository,
        RoleRepository roleRepository
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> doSomething() {
        this.userRepository.deleteAll();
        this.roleRepository.deleteAll();
        User user = new User();
        user.setFirstName("David");
        this.userRepository.save(user);
        Role role = new Role ();
        role.setName("Admin");
        this.roleRepository.save(role);
        return this.userRepository.findAll();
    }
}
