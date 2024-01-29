package com.ksteindl.chemstore.domain.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class AppUserInput implements Input {

    public static AppUserInputBuilder builder() {
        return new AppUserInputBuilder();
    }

    @Email(message = "Username must be an email")
    @NotBlank(message = "Username is required")
    protected String username;

    @NotBlank(message = "full name cannot be blank")
    protected String fullName;

    private List<String> labKeysAsUser = new ArrayList<>();

    private List<String> labKeysAsAdmin = new ArrayList<>();

    private List<String> roles = new ArrayList<>();

    public static class AppUserInputBuilder {
        private String username;
        private String fullName;
        private List<String> labKeysAsUser = new ArrayList<>();
        private List<String> labKeysAsAdmin = new ArrayList<>();
        private List<String> roles = new ArrayList<>();

        public AppUserInputBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AppUserInputBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AppUserInputBuilder labKeysAsUser(List<String> labKeysAsUser) {
            this.labKeysAsUser = labKeysAsUser;
            return this;
        }

        public AppUserInputBuilder labKeysAsAdmin(List<String> labKeysAsAdmin) {
            this.labKeysAsAdmin = labKeysAsAdmin;
            return this;
        }

        public AppUserInputBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }


        public AppUserInput build() {
            return new AppUserInput(username, fullName, labKeysAsUser, labKeysAsAdmin, roles);
        }
    }
}
