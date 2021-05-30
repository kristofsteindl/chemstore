package com.ksteindl.chemstore.security.role;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
@Data
public class Role {

    public static final String ACCOUNT_MANAGER = "ACCOUNT_MANAGER";

    public static final List<String> ROLES = List.of(
            ACCOUNT_MANAGER);


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;

    protected Role(String role) {
        this.role = role;
    }

    public Role() {
    }
}
