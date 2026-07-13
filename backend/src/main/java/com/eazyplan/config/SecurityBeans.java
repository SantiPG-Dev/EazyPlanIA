package com.eazyplan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Beans de infraestructura.
 *
 * <p>Expone un {@link PasswordEncoder} (BCrypt) para que las contraseñas se
 * almacenen hasheadas desde el primer momento — resolviendo la deuda técnica
 * del legado JavaFX (que las guardaba en texto plano). La cadena completa de
 * filtros de Spring Security (JWT, autorización por ruta) llega en el Paso 5.
 */
@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
