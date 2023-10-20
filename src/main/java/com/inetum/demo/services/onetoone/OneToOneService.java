package com.inetum.demo.services.onetoone;

import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.domain.onetoone.PhoneDetails;
import com.inetum.demo.repositories.onetoone.PhoneDetailsRepository;
import com.inetum.demo.repositories.onetoone.PhoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OneToOneService {
    PhoneRepository phoneRepository;
    PhoneDetailsRepository phoneDetailsRepository;

    OneToOneService(
            PhoneRepository phoneRepository,
            PhoneDetailsRepository phoneDetailsRepository
    ){
        this.phoneRepository = phoneRepository;
        this.phoneDetailsRepository = phoneDetailsRepository;
    }

    @Transactional
    public List<Phone> doSomething(){
        Phone phone = new Phone();
        phone.setNumber("923124578");
        this.phoneRepository.save(phone);
        PhoneDetails phoneDetails = new PhoneDetails();
        phoneDetails.setProvider("PepePhone");
        this.phoneDetailsRepository.save(phoneDetails);
        phone.setDetails(phoneDetails);
        return this.phoneRepository.findAll();
    }
}
