package api;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class LoginAPI {
    public Response login(String username, String password) {
        String body = String.format("""
            { "username": "%s", "password": "%s" }
            """, username, password);

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/login");
    }
}