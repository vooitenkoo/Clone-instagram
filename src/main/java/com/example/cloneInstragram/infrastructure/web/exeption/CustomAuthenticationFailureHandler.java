package com.example.cloneInstragram.infrastructure.web.exeption;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        // Перенаправляем обратно на страницу логина с сообщением об ошибке в URL
        String errorMessage = exception.getMessage();
        response.sendRedirect("/login?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}
