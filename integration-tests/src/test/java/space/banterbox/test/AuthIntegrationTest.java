package space.banterbox.test;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuthIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8000/api/v1";
    }

    @Test
    public void testLogin() {
        String loginPayload = """
          {
            "username": "harry",
            "password": "pass123"
          }
        """;

        RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200);
    }
}
