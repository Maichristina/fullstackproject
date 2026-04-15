//Without Security Anyone can do ANYTHING:
package com.christinamai.project.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
//οταν ο χρηστης κανει λογκ ιν και ειναι σωστα τα στοιχεια,φτιαχνει ενα τοκεν με ονομα χρηστη ρολο κλπ
@Component //@Component means Spring manages this class. Any other class can use it by just saying @Autowired.
public class JwtUtils {
    //final->dont change,Logger->type,logger->varname,loggerfactory->create loggers,.get->jwtutils
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}") //reads from application.properties file
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(String username, String role) {
        return Jwts.builder()  //builder->Easier to make objects
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact(); //finish and build token
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class); //get the value of role and expect to be string.stringclass->reurn as string role admin
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("JWT malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            logger.error("JWT security error: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT illegal argument: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser() //read a token
                .verifyWith(getSigningKey()) // use secret key to verify
                .build()
                .parseSignedClaims(token) //open the token
                .getPayload(); // get the data inside
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // cryptographic algorithm
    }
}