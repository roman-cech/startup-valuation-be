package cz.utb.fai.jwt

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(message: String) : AuthenticationException(message) {
    companion object {
        const val NOT_AUTHORIZED_MESSAGE = "Not Authorized!"
    }
}