package com.inetum.demo.services.onetomany;

import com.inetum.demo.domain.onetomany.Book;
import com.inetum.demo.domain.onetomany.Gender;
import com.inetum.demo.repositories.onetomany.BookRepository;
import com.inetum.demo.repositories.onetomany.GenderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OneToManyService {
    BookRepository bookRepository;
    GenderRepository genderRepository;
    OneToManyService(BookRepository bookRepository,
    GenderRepository genderRepository){
        this.bookRepository = bookRepository;
        this.genderRepository = genderRepository;
    }


    @Transactional
    public List<Gender> doSomething() {
        log.info("onetomany");
        this.bookRepository.deleteAll();
        this.genderRepository.deleteAll();
        Gender gender = new Gender();
        gender.setName("Fantasía");
        this.genderRepository.save(gender);
        Book book = new Book();
        book.setTitle("El Color de la Magia");
        gender.getBooks().add(book);
        this.genderRepository.save(gender);
        return this.genderRepository.findAll();
    }
}
