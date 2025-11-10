package middleware;

import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import util.JwtUtil;

import java.util.Optional;

public class AuthMiddleware {
    public static Optional<Claims> authenticate(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = JwtUtil.verifyToken(token);
                return Optional.of(claims);
            } catch (JwtException e) {
                return Optional.empty();
            }
        }
        
        return Optional.empty();
    }

    public static void requireAuth(HttpExchange exchange) throws Exception {
        if (authenticate(exchange).isEmpty()) {
            exchange.sendResponseHeaders(401, -1);
            exchange.close();
            throw new Exception("Unauthorized");
        }
    }
}