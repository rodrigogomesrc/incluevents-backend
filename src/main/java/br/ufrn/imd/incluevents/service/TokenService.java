package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
    public String generateToken(Usuario usuario) throws BusinessException{
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer("incluevents")
                .withSubject(usuario.getUsername())
                .withExpiresAt(generateExpirationDate())
                .sign(algorithm);

        } catch (JWTCreationException exception){
            throw new BusinessException("Error enquanto estava gerando token", ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    public String validateToken(String token) throws BusinessException{
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                .withIssuer("incluevents")
                .build()
                .verify(token)
                .getSubject();

        } catch (JWTVerificationException e){
            throw new BusinessException("Token expirado ou inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
