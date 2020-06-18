package org.clientserver;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import java.io.IOException;

import org.clientserver.Dao.Product;
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
    void shouldReturn404OnGetByInvalidId_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .header("Authorization", loginResponse.getToken())
        .when()
                .get("/api/product/678")
        .then()
                .statusCode(404)
                .body("message", is("No such product"));
    }

    @Test
    void shouldReturnProduct_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .header("Authorization", loginResponse.getToken())
                .when()
                .get("/api/product/4")
                .then()
                .statusCode(200)
                .body("id", is(4))
                .body("name", is("name-3"))
//                .body("price", is(100.0))
//                .body("amount", is(10.0))
                .body("description", is("good"))
                .body("manufacturer", is("Harvest"))
                .body("group_id", is(1));
    }

    @Test
    void shouldReturn201SuccessfullyAdded_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .body(Product.of(35, "water", 12, 3, "good", "Harvest", 2))
                .header("Authorization", loginResponse.getToken())
                .when()
                .put("/api/product")
                .then()
                .statusCode(201)
                .body("response", is("Successfully created product!"))
                .body("id", is(35));
    }

    @Test
    void shouldReturn204SuccessfullyUpdated_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .body(Product.of(4, "water", 12, 3, null, "Harvest", 0))
                .header("Authorization", loginResponse.getToken())
                .when()
                .post("/api/product")
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404NothingToUpdate_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .body(Product.of(234, "water", 12, 3, "good", "Harvest", 2))
                .header("Authorization", loginResponse.getToken())
                .when()
                .post("/api/product")
                .then()
                .statusCode(404)
                .body("message", is("No such product"));
    }

    @Test
    void shouldReturn409WrongInputOnUpdate_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .body(Product.of(2, "water", -12, 3, null, null, 10))
                .header("Authorization", loginResponse.getToken())
                .when()
                .post("/api/product")
                .then()
                .statusCode(409)
                .body("message", is("Wrong input"));
    }

    @Test
    void shouldReturn409WrongInputOnInsert_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .body(Product.of(35, "water", -12, 3, "good", "Harvest", 2))
                .header("Authorization", loginResponse.getToken())
                .when()
                .put("/api/product")
                .then()
                .statusCode(409)
                .body("message", is("Wrong input"));
    }

    @Test
    void shouldReturn204OnDeleteProduct_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .header("Authorization", loginResponse.getToken())
                .when()
                .delete("/api/product/4")
        .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404onInvalidDelete_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .header("Authorization", loginResponse.getToken())
                .when()
        .delete("/api/product/43254345")
                .then()
                .statusCode(404)
                .body("message", is("No such product"));
    }

    @Test
    void shouldReturn404NotAppropriateCommand_whenValidToken() {
        final LoginResponse loginResponse = getToken("login", "password");

        given()
                .header("Authorization", loginResponse.getToken())
                .when()
                .post("/api/product/4")
                .then()
                .statusCode(404)
                .body("message", is("Not appropriate command"));
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