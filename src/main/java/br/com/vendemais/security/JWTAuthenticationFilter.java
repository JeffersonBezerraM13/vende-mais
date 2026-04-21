package br.com.vendemais.security;


import br.com.vendemais.controller.exceptions.StandardError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Date;

/**
 * Intercepts login requests, authenticates the submitted credentials, and emits
 * a JWT that grants access to protected CRM endpoints.
 */
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JWTUtil jwtUtil;


    public JWTAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login"); // Define o endpoint que serÃ¡ interceptado por este filtro
    }

    /**
     * Attempts to authenticate the user with the email and password supplied in
     * the login request body.
     *
     * @param request HTTP request containing the submitted credentials
     * @param response HTTP response associated with the login attempt
     * @return the authenticated principal returned by the authentication manager
     * @throws AuthenticationException if the credentials are rejected
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        CredentialsDTO credentials = new ObjectMapper().readValue(request.getInputStream(), CredentialsDTO.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * Writes the JWT into the response headers when authentication succeeds so
     * the client can authenticate subsequent API calls.
     *
     * @param request HTTP request that triggered authentication
     * @param response HTTP response that will carry the generated token
     * @param chain filter chain associated with the current request
     * @param authResult successful authentication result
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        String userName = authResult.getName();
        String token = jwtUtil.generateToken(userName);

        response.setHeader("access-control-expose-headers", "Authorization");
        response.setHeader("Authorization", "Bearer " + token);
    }

    /**
     * Returns the standardized 401 payload used when login credentials are
     * invalid.
     *
     * @param request HTTP request that failed authentication
     * @param response HTTP response that will receive the error payload
     * @param failed authentication exception raised by the login attempt
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpServletResponse.SC_UNAUTHORIZED,
                "Não autorizado",
                "Email ou senha inválidos",
                request.getRequestURI()
        );

        new ObjectMapper().writeValue(response.getWriter(), error);
    }
}
