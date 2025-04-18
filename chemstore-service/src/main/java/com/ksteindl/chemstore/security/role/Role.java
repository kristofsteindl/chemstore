package com.ksteindl.chemstore.security.role;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
