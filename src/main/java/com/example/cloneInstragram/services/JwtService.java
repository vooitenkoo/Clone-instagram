package com.example.cloneInstragram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;  // Секретный ключ для подписи токенов

    private static final long JWT_EXPIRATION_MS = 86400000;  // Время жизни токена (24 часа)

    // Генерация токена
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // Устанавливаем имя пользователя в качестве subject
                .setIssuedAt(new Date())  // Время выпуска
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))  // Время истечения
                .signWith(SignatureAlgorithm.HS512, jwtSecret)  // Подпись токена
                .compact();
    }

    // Получение имени пользователя из токена
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)  // Устанавливаем секретный ключ
                .parseClaimsJws(token)
                .getBody()
                .getSubject();  // Извлекаем имя пользователя
    }

    // Проверка валидности токена
    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)  // Устанавливаем секретный ключ
                    .parseClaimsJws(token);  // Пытаемся распарсить токен
            return true;  // Если не выбрасывается исключение, токен валиден
        } catch (Exception e) {
            return false;  // Если ошибка при парсинге токена, значит он недействителен
        }
    }

    // Проверка, не истек ли токен
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();  // Получаем дату истечения
        return expiration.before(new Date());  // Сравниваем с текущей датой
    }
}

