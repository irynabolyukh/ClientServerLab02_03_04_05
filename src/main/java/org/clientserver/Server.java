package org.clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

import org.clientserver.Dao.*;
import org.clientserver.http.Enpoint;
import org.clientserver.http.ErrorResponse;
import org.clientserver.http.JwtService;
import org.clientserver.http.LoginResponse;

public class Server {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final HttpPrincipal ANONYMOUS_USER = new HttpPrincipal("anonymous", "anonymous");

    private final UserDao USER_DAO = new UserDao(":memory:");
    private final DaoProduct PRODUCT_DAO = new DaoProduct(":memory:");
    private final List<Enpoint> enpoints;

    private final HttpServer server;

    public Server() throws IOException {
        USER_DAO.insert(
                User.builder()
                        .login("login")
                        .password(DigestUtils.md5Hex("password"))
                        .role("admin")
                        .build()
        );

        USER_DAO.insert(
                User.builder()
                        .login("user")
                        .password(DigestUtils.md5Hex("password"))
                        .role("user")
                        .build()
        );

        for (int i = 0; i < 10; i++) {
            PRODUCT_DAO.insertProduct(
                   new Product("name-" + i,Math.random() * 100,10,"good","Harvest",1)
            );
        }

        this.enpoints = new ArrayList<Enpoint>();
        enpoints.add(Enpoint.of("\\/login", this::loginHandler,  (a, b) -> new HashMap<>()));
        enpoints.add(Enpoint.of("^\\/api\\/product\\/(\\d+)$", this::getProductByIdHandler, this::getProductParamId));

        this.server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.createContext("/", this::rootHandler)
                .setAuthenticator(new MyAuthenticator());
        server.start();
    }

    public void stop() {
        this.server.stop(1);
    }

    private void rootHandler(final HttpExchange exchange) throws IOException {
        final String uri = exchange.getRequestURI().toString();

        final Optional<Enpoint> enpoint = enpoints.stream()
                .filter(endpoint -> endpoint.matches(uri))
                .findFirst();

        if (enpoint.isPresent()) {
            enpoint.get().handler()
                    .handle(exchange);
        } else {
            // default handler
            // 404
            handlerNoFound(exchange);
        }
    }

    private void getProductByIdHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream inputStream = exchange.getRequestBody(); final OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }

            final int productId = Integer.parseInt(pathParams.get("productId"));
            final Product product = PRODUCT_DAO.getProduct(productId);

            if (product != null) {
                writeResponse(exchange, 200, product);
            } else {
                writeResponse(exchange, 404, ErrorResponse.of("No such product"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getProductParamId(final String uri, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(uri);
        matcher.find();

        return new HashMap<String, String>(){{
            put("productId", matcher.group(1));
        }};
    }

    private void loginHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream requestBody = exchange.getRequestBody()) {
            final UserCredential userCredential = OBJECT_MAPPER.readValue(requestBody, UserCredential.class);
            final User user = USER_DAO.getByLogin(userCredential.getLogin());

            exchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            if (user != null) {
                if (user.getPassword().equals(DigestUtils.md5Hex(userCredential.getPassword()))) {
                    final LoginResponse loginResponse = LoginResponse.of(JwtService.generateToken(user), user.getLogin(), user.getRole());
                    writeResponse(exchange, 200, loginResponse);
                } else {
                    writeResponse(exchange, 401, ErrorResponse.of("invalid password"));
                }
            } else {
                writeResponse(exchange, 401, ErrorResponse.of("unknown user"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlerNoFound(final HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(404, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeResponse(final HttpExchange exchange, final int statusCode, final Object response) throws IOException {
        final byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(response);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private class MyAuthenticator extends Authenticator {

        @Override
        public Result authenticate(final HttpExchange httpExchange) {
            final String token = httpExchange.getRequestHeaders().getFirst(AUTHORIZATION_HEADER);

            if (token != null) {
                try {
                    final String username = JwtService.getUsernameFromToken(token);
                    final User user = USER_DAO.getByLogin(username);
                    if (user != null) {
                        return new Success(new HttpPrincipal(username, user.getRole()));
                    } else {
                        return new Retry(401);
                    }

                } catch (Exception e) {
                    return new Failure(403);
                }
            }

            return new Success(ANONYMOUS_USER);
        }
    }

}