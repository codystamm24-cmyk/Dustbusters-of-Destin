import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ServiceDao;
import dao.UserDao;
import model.Service;
import model.User;
import config.DatabaseConfig;
import util.JwtUtil;
import middleware.AuthMiddleware;
import java.util.List;
import java.util.Map;

public class Main {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ServiceDao serviceDao = new ServiceDao();
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/jobs", (exchange) -> handleJobs(exchange));
        server.createContext("/book", (exchange) -> handleBook(exchange));
        server.createContext("/auth/register", (exchange) -> handleRegister(exchange));
        server.createContext("/auth/login", (exchange) -> handleLogin(exchange));
        server.setExecutor(null);
        server.start();
        System.out.println("Backend server running on http://localhost:8080");
    }

    private static void loadBookings() {
        try {
            if (Files.exists(BOOKING_FILE)) {
                List<String> lines = Files.readAllLines(BOOKING_FILE, StandardCharsets.UTF_8);
                BOOKINGS.clear();
                int maxId = 0;
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) continue;
                    BOOKINGS.add(trimmed);
                    // attempt to extract id from pattern "id":NUMBER
                    int idIndex = trimmed.indexOf("\"id\"");
                    if (idIndex >= 0) {
                        int colon = trimmed.indexOf(':', idIndex);
                        if (colon > idIndex) {
                            int i = colon + 1;
                            while (i < trimmed.length() && Character.isWhitespace(trimmed.charAt(i))) i++;
                            int start = i;
                            while (i < trimmed.length() && (Character.isDigit(trimmed.charAt(i)))) i++;
                            if (i > start) {
                                try {
                                    int id = Integer.parseInt(trimmed.substring(start, i));
                                    if (id > maxId) maxId = id;
                                } catch (NumberFormatException ex) {
                                    // ignore
                                }
                            }
                        }
                    }
                }
                if (maxId > 0) nextBookingId = Math.max(nextBookingId, maxId + 1);
            }
        } catch (Exception e) {
            System.err.println("Failed to load bookings: " + e.getMessage());
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void handleJobs(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Service> services = serviceDao.getAllServices();
            String json = OBJECT_MAPPER.writeValueAsString(services);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private static void handleRegister(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = OBJECT_MAPPER.readValue(body, Map.class);

            try {
                User user = userDao.createUser(
                    data.get("email"),
                    data.get("password"),
                    data.get("firstName"),
                    data.get("lastName"),
                    data.get("phone")
                );

                String token = JwtUtil.generateToken(user);
                Map<String, Object> response = Map.of(
                    "token", token,
                    "user", user
                );

                String json = OBJECT_MAPPER.writeValueAsString(response);
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(201, resp.length);
                OutputStream os = exchange.getResponseBody();
                os.write(resp);
                os.close();
            } catch (Exception e) {
                String error = OBJECT_MAPPER.writeValueAsString(Map.of("error", e.getMessage()));
                byte[] resp = error.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(400, resp.length);
                OutputStream os = exchange.getResponseBody();
                os.write(resp);
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> data = OBJECT_MAPPER.readValue(body, Map.class);

            String email = data.get("email");
            String password = data.get("password");

            if (userDao.verifyPassword(email, password)) {
                User user = userDao.findByEmail(email).orElseThrow();
                String token = JwtUtil.generateToken(user);
                
                Map<String, Object> response = Map.of(
                    "token", token,
                    "user", user
                );

                String json = OBJECT_MAPPER.writeValueAsString(response);
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, resp.length);
                OutputStream os = exchange.getResponseBody();
                os.write(resp);
                os.close();
            } else {
                String error = OBJECT_MAPPER.writeValueAsString(Map.of("error", "Invalid credentials"));
                byte[] resp = error.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(401, resp.length);
                OutputStream os = exchange.getResponseBody();
                os.write(resp);
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private static void handleBook(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = readRequestBody(exchange.getRequestBody());
            int id;
            String bookingLine;
            synchronized (BOOKINGS) {
                id = nextBookingId++;
                bookingLine = "{\"id\":" + id + ", \"payload\":" + body + "}";
                BOOKINGS.add(bookingLine);
            }

            try {
                writeBooking(bookingLine);
            } catch (IOException e) {
                System.err.println("Failed to persist booking: " + e.getMessage());
            }

            String respJson = "{\"bookingId\":" + id + ", \"status\": \"confirmed\"}";
            byte[] resp = respJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
        } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            synchronized (BOOKINGS) {
                for (int i = 0; i < BOOKINGS.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append(BOOKINGS.get(i));
                }
            }
            sb.append("]");
            byte[] resp = sb.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private static void writeBooking(String line) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(BOOKING_FILE, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }

    private static String readRequestBody(InputStream is) throws IOException {
        byte[] buf = is.readAllBytes();
        return new String(buf, StandardCharsets.UTF_8);
    }
}
