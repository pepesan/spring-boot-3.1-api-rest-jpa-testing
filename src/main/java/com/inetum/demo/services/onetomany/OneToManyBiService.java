package com.inetum.demo.services.onetomany;

import com.inetum.demo.repositories.onetomany.Address2Repository;
import com.inetum.demo.repositories.onetomany.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inetum.demo.domain.onetomany.Person;
import com.inetum.demo.domain.onetomany.Address2;
import java.util.List;

@Service
@Slf4j
public class OneToManyBiService {

    private PersonRepository personRepository;

    private Address2Repository address2Repository;
    @Autowired
    OneToManyBiService(PersonRepository personRepository,
                       Address2Repository address2Repository){
        this.personRepository= personRepository;
        this.address2Repository= address2Repository;
    }

    @Transactional
    public List<Person> doSomething() {
        log.info("onetomany");
        this.address2Repository.deleteAll();
        this.personRepository.deleteAll();
        Person p = new Person();
        p.setName("David");
        this.personRepository.save(p);
        Address2 address2 = new Address2();
        address2.setCity("Salamanca");
        address2.setStreet("Mayor");
        this.address2Repository.save(address2);
        p.getAddresses().add(address2);
        this.personRepository.save(p);
        return this.personRepository.findAll();
    }
}
