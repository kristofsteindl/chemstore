package com.ksteindl.chemstore.domain.input;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
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

    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotBlank(message = "password2 cannot be blank")
    private String password2;

    public static class AppUserInputBuilder {
        private String username;
        private String fullName;
        private List<String> labKeysAsUser = new ArrayList<>();
        private List<String> labKeysAsAdmin = new ArrayList<>();
        private List<String> roles = new ArrayList<>();
        private String password;
        private String password2;

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

        public AppUserInputBuilder password(@NotBlank(message = "password cannot be blank") String password) {
            this.password = password;
            return this;
        }

        public AppUserInputBuilder password2(@NotBlank(message = "password2 cannot be blank") String password2) {
            this.password2 = password2;
            return this;
        }

        public AppUserInput build() {
            return new AppUserInput(username, fullName, labKeysAsUser, labKeysAsAdmin, roles, password, password2);
        }
    }
}
