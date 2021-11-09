package com.ksteindl.chemstore.security.role;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private String name;

    public Role(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public Role() {
    }
}
