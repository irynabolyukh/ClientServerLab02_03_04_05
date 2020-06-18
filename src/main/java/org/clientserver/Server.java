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
import org.clientserver.http.*;

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
                    Product.of(i, "name-" + i, 100, 10, "good", "Harvest", 1)
            );
        }

        this.enpoints = new ArrayList<Enpoint>();
        enpoints.add(Enpoint.of("\\/login", this::loginHandler, (a, b) -> new HashMap<>()));
        enpoints.add(Enpoint.of("\\/api\\/product", this::putProductHandler, (a, b) -> new HashMap<>()));
        enpoints.add(Enpoint.of("^\\/api\\/product\\/(\\d+)$", this::getProductByIdHandler, this::getProductParamId));


        this.server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.createContext("/", this::rootHandler)
                .setAuthenticator(new MyAuthenticator());
        server.start();
    }

    public void stop() {
        this.server.stop(1);//timeout - 1
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
            handlerNoFound(exchange);
        }
    }

    private void getProductByIdHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream inputStream = exchange.getRequestBody(); final OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = exchange.getRequestMethod();

            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }

            final int productId = Integer.parseInt(pathParams.get("productId"));
            final Product product = PRODUCT_DAO.getProduct(productId);

            if (method.equals("GET")) {

                if (product != null) {
                    writeResponse(exchange, 200, product);
                } else {
                    writeResponse(exchange, 404, ErrorResponse.of("No such product"));
                }

            } else if (method.equals("DELETE")) {

                if (product != null) {
                    int deleted = PRODUCT_DAO.deleteProduct(productId);

                    if (deleted == productId) {
                        exchange.sendResponseHeaders(204, -1);
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("Deletion failed"));
                    }
                } else {
                    writeResponse(exchange, 404, ErrorResponse.of("No such product"));
                }

            } else {
                writeResponse(exchange, 404, ErrorResponse.of("Not appropriate command"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putProductHandler(final HttpExchange exchange, final Map<String, String> pathParams) {
        try (final InputStream requestBody = exchange.getRequestBody()) {
            exchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            String method = exchange.getRequestMethod();

            if (!exchange.getPrincipal().getRealm().equals("admin")) {
                writeResponse(exchange, 403, ErrorResponse.of("No permission"));
                return;
            }

            if (method.equals("PUT")) {
                final Product product = OBJECT_MAPPER.readValue(requestBody, Product.class);

                if (product != null) {

                    if (product.getId() > 0 && product.getAmount() >= 0 && product.getPrice() > 0 && product.getGroup_id() > 0) {

                        PRODUCT_DAO.insertProduct(product);
                        writeResponse(exchange, 201, SuccessResponse.of("Successfully created product!", product.getId()));

                    } else {
                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                    }
                } else {
                    writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                }

            } else if (method.equals("POST")) {

                final Product productReceived = OBJECT_MAPPER.readValue(requestBody, Product.class);

                Product product = PRODUCT_DAO.getProduct(productReceived.getId());

                if (product != null) {

                    String name = productReceived.getName();
                    if (name != null) {
                        product.setName(name);
                    }

                    double price = productReceived.getPrice();
                    if (price > 0) {
                        product.setPrice(price);
                    } else if (price < 0) {
                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                        return;
                    }

                    double amount = productReceived.getAmount();
                    if (amount > 0) {
                        product.setAmount(amount);
                    } else if (amount < 0) {
                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                        return;
                    }

                    String description = productReceived.getDescription();
                    if (description != null) {
                        product.setDescription(description);
                    }

                    String manufacturer = productReceived.getManufacturer();
                    if (manufacturer != null) {
                        product.setManufacturer(manufacturer);
                    }

                    Integer group_id = productReceived.getGroup_id();
                    if (group_id > 0) {
                        product.setGroup_id(group_id);
                    } else if (group_id < 0) {
                        writeResponse(exchange, 409, ErrorResponse.of("Wrong input"));
                        return;
                    }

                    int updated = PRODUCT_DAO.updateProduct(product);

                    if (updated > 0) {
                        exchange.sendResponseHeaders(204, -1);
                    } else {
                        writeResponse(exchange, 404, ErrorResponse.of("Can't update product"));
                    }
                } else {
                    writeResponse(exchange, 404, ErrorResponse.of("No such product"));
                }
            } else {
                writeResponse(exchange, 404, ErrorResponse.of("Not appropriate command"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getProductParamId(final String uri, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(uri);
        matcher.find();

        return new HashMap<String, String>() {{
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