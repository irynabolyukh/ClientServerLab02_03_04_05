package org.clientserver.http;

import java.security.Key;
import io.jsonwebtoken.*;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.clientserver.Dao.User;

public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ttlMillis = 4000000;

    public static String generateToken(final User user) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(user.getLogin())
                .claim("role", user.getRole())
                .signWith(SECRET_KEY);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        System.out.println(builder.compact());
        return builder.compact();
    }

    public static String getUsernameFromToken(String jwt) {
        System.out.println(jwt);
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}