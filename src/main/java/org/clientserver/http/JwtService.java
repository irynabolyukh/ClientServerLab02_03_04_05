package org.clientserver.http;

import  io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.clientserver.Dao.User;

import java.security.Key;

public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(final User user) {
        return Jwts.builder()
                .setSubject(user.getLogin())
                .signWith(SECRET_KEY)
                .claim("role", user.getRole())
                .compact();
    }

    public static String getUsernameFromToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

}
