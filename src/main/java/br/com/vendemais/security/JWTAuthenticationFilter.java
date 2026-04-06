package br.com.vendemais.security;


import br.com.vendemais.domain.dtos.CredentialsDTO;
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
 * Este filtro é responsável por interceptar requisições POST para o endpoint "/login",
 * que é reservado pelo Spring Security para autenticação.
 *
 * Ao estender UsernamePasswordAuthenticationFilter, o Spring automaticamente entende
 * que esta classe será usada para o processo de login.
 */
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JWTUtil jwtUtil;


    public JWTAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login"); // Define o endpoint que será interceptado por este filtro
    }

    /**
     * Tenta autenticar o usuário com base nas credenciais enviadas na requisição.
     *
     * @param request  Requisição HTTP contendo o corpo com email e senha
     * @param response Resposta HTTP
     * @return Objeto Authentication representando o usuário autenticado
     * @throws AuthenticationException se a autenticação falhar
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        CredentialsDTO credentials = new ObjectMapper().readValue(request.getInputStream(), CredentialsDTO.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * Este método é chamado automaticamente quando a autenticação for bem-sucedida.
     * Ele gera o token JWT e o adiciona no cabeçalho da resposta.
     *
     * @param request     Requisição HTTP
     * @param response    Resposta HTTP
     * @param chain       Filtro da requisição
     * @param authResult  Resultado da autenticação
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
     * Este método é chamado automaticamente quando a autenticação falha.
     * Aqui você pode personalizar a resposta de erro (401 - Unauthorized).
     *
     * @param request  Requisição HTTP
     * @param response Resposta HTTP
     * @param failed   Exceção lançada na tentativa de autenticação
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.getWriter().write(buildJsonError());
    }

    /**
     * Gera a estrutura JSON da resposta de erro 401.
     *
     * @return String JSON com detalhes do erro
     */
    private String buildJsonError() {
        long timestamp = new Date().getTime();
        return "{"
                + "\"timestamp\": " + timestamp + ","
                + "\"status\": 401,"
                + "\"error\": \"Não autorizado\","
                + "\"message\": \"Email ou senha inválidos\","
                + "\"path\": \"/login\""
                + "}";
    }
}
