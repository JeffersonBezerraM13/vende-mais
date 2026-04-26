package br.com.vendemais.config;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.security.CredentialsDTO;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    @Bean
    public OpenAPI vendeMaisOpenApi() {
        Components components = new Components();
        registerSchema(components, CredentialsDTO.class);
        registerSchema(components, StandardError.class);

        return new OpenAPI()
                .components(components)
                .info(new Info()
                        .title("VendeMais API")
                        .version("v1")
                        .description("""
                                O VendeMais é uma API backend para gestão comercial (CRM) construída para organizar o fluxo de vendas de forma estruturada.
                                O sistema trabalha com o funil de vendas abrangendo as entidades Lead, Opportunity, Pipeline, Stage, Task e User.
                                A arquitetura separa DTOs por intenção de uso, incluindo fluxos específicos como o fechamento de oportunidades, e utiliza autenticação stateless via JWT.
                                """))
                .path("/login", new PathItem().post(new io.swagger.v3.oas.models.Operation()
                        .tags(List.of("Autenticação"))
                        .summary("Autentica um usuário e retorna um JWT")
                        .description("Endpoint público de login. Envie email e senha no corpo da requisição e utilize o token retornado no header Authorization para autorizar os demais endpoints.")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/CredentialsDTO"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new io.swagger.v3.oas.models.responses.ApiResponse()
                                        .description("Autenticação realizada com sucesso. O JWT é retornado no header Authorization.")
                                        .addHeaderObject("Authorization", new Header()
                                                .description("Token JWT no formato Bearer")
                                                .schema(new StringSchema().example("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlaW5zdGVpbkBnbWFpbC5jb20iLCJleHAiOjE5OTk5OTk5OTl9.signature"))))
                                .addApiResponse("401", new io.swagger.v3.oas.models.responses.ApiResponse()
                                        .description("Email ou senha inválidos.")
                                        .content(new Content().addMediaType(
                                                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/StandardError"))
                                        ))))));
    }

    private void registerSchema(Components components, Class<?> type) {
        Map<String, Schema> schemas = ModelConverters.getInstance().read(type);
        schemas.forEach(components::addSchemas);
    }
}
