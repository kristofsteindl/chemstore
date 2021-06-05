package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AccountManagerControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;

    @Test
    void createUser() {
    }

    @Test
    void updateteUser() {
    }

    @Test
    void testGetAllAppUser_whenAuthorized_gotOkResponse() throws Exception {
        String url = "http://localhost:8080/api/account/user";
        HttpHeaders headers = new HttpHeaders();
        String token = jwtProvider.generateToken("kristof@kristof.com");
        headers.set("Authorization", token);
        headers.setBearerAuth(token);

        HttpEntity request = new HttpEntity<>(headers);


//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                request,
//                Object.class
//        );
        mvc.perform(MockMvcRequestBuilders.get(url).header("Authorization", token)).andExpect(MockMvcResultMatchers.status().isOk());

        //ResponseEntity response = testRestTemplate.getForEntity(url, request, Object.class);
        //Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void deleteAppUser() {
    }

    @Test
    void createLab() {
    }

    @Test
    void updateLab() {
    }

    @Test
    void getEveryLab() {
    }

    @Test
    void deleteLab() {
    }
}