package com.Sridevi.Campushelpdesk.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
 @Value("${jwt.secret}")
 private String secretKey;
 @Value("${jwt.expiration}")
 private long jwtExpiration;
    public String  generateToken(Map<String,Object> extraClaims,UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
public <T> T extractClaims(String token, Function<Claims,T>claimResolver){
        final Claims claims=extractAllClaims(token);
        return claimResolver.apply(claims);

}
public String extractUserName(String token){
        return extractClaims(token,Claims::getSubject);
}
public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
}
public boolean isTokenValid(String token, UserDetails userDetails){
        return (extractUserName(token).equals(userDetails.getUsername()) && !isExpired(token));

        }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
