package com.inetum.demo.domain.herencia.tableperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gatos")
public class Gato extends Animal {
    private String colorPelo;

    private boolean indoor;
}
