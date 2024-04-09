package cz.utb.fai.common

import cz.utb.fai.jwt.JwtAuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(JwtAuthenticationException::class)
    fun jwtAuthenticationException(e: JwtAuthenticationException): ResponseEntity<GeneralFault> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            GeneralFault(
                code = HttpStatus.UNAUTHORIZED.value(),
                message = e.message.toString()
            )
        )
}