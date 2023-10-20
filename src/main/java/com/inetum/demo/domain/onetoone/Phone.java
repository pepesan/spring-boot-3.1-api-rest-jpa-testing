package com.inetum.demo.domain.onetoone;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Phone")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "`number`")
    private String number;
    @OneToOne
    @JoinColumn(name = "details_id")
    private PhoneDetails details;
}
