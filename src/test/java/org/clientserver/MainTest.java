package org.clientserver;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import java.io.IOException;

import org.clientserver.Dao.UserCredential;
import org.clientserver.http.LoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

class ServerTest {

    private Server server;

    @BeforeEach
    void init() throws IOException {
        server = new Server();

        RestAssured.port = 8080;
    }

    @AfterEach
    void cleanUp() {
        server.stop();
        server = null;
    }

    @Test
    void shouldLogin_whenValidCredentials() {
        getToken("login", "password");
    }

    @Test
    void shouldReturn403_whenInvalidLogin() {
        given()
                .body(UserCredential.of("unknown_login", "password"))
                .when()
                .post("/login")
                .then()
                .statusCode(401)
                .body("message", is("unknown user"));
    }

    @Test
    void shouldReturnProduct_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .header("Authorization", loginResponse.getToken())
                .when()
                .post("/api/product/4")
                .then()
                .statusCode(200)
                .body("id", is(4))
                .body("name", is("name-3"));
    }

    @Test
    void shouldReturn403_whenGetProductWithoutToken() {
        when()
                .post("/api/product/4")
                .then()
                .statusCode(403)
                .body("message", is("No permission"));
    }

    private static LoginResponse getToken(final String login, final String password) {
        return given()
                .body(UserCredential.of(login, password))
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", not(emptyOrNullString()))
                .extract()
                .as(LoginResponse.class);
    }

}