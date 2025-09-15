package com.vitalcajavet.msagendamentoconsultas.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class JwtService {

    private final WebClient webClient;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtService.class);

    public JwtService(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtValidationException("Token de autenticação não fornecido");
        }

        try {
            String tokenToSend = token.startsWith("Bearer ") ? token : "Bearer " + token;

            logger.debug("Validando token com endpoint /profile/me/");

            // Usando o endpoint /profile/me/ como no Python
            // Se retornar 200 OK, o token é válido
            webClient.get()
                    .uri("/profile/me/")
                    .header(HttpHeaders.AUTHORIZATION, tokenToSend)
                    .retrieve()
                    .onStatus(
                            status -> status == HttpStatus.UNAUTHORIZED,
                            response -> Mono.error(new JwtValidationException("Token inválido ou expirado"))
                    )
                    .onStatus(
                            status -> status == HttpStatus.FORBIDDEN,
                            response -> {
                                // 403 FORBIDDEN pode significar que o token é válido mas o usuário não tem acesso ao endpoint
                                // No contexto de validação, podemos considerar como token válido
                                logger.warn("Token retornou 403 FORBIDDEN - considerado válido para fins de autenticação");
                                return Mono.empty(); // Não lança exceção
                            }
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> Mono.error(new JwtValidationException("Serviço de autenticação indisponível"))
                    )
                    .toBodilessEntity() // Só nos importa o status code, não o corpo
                    .block();

            // Se chegou até aqui sem exception, o token é válido
            logger.debug("Token validado com sucesso via /profile/me/");
            return true;

        } catch (JwtValidationException e) {
            throw e; // Re-lança exceções específicas
        } catch (Exception e) {
            throw new JwtValidationException("Falha na validação do token: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        try {
            if (token == null || token.isBlank()) {
                return "unknown";
            }

            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String[] parts = jwt.split("\\.");

            if (parts.length != 3) {
                return "unknown";
            }

            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            // Tenta extrair username de diferentes formas
            if (payload.contains("\"username\"")) {
                return payload.split("\"username\":\"")[1].split("\"")[0];
            }
            if (payload.contains("\"sub\"")) {
                return payload.split("\"sub\":\"")[1].split("\"")[0];
            }
            if (payload.contains("\"preferred_username\"")) {
                return payload.split("\"preferred_username\":\"")[1].split("\"")[0];
            }

            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
}