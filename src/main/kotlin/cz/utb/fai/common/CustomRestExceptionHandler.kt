package cz.utb.fai.common

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@RestControllerAdvice
class CustomRestExceptionHandler {

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseBody
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<Any> {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val body = GeneralFault(
            errorCode = HttpStatus.UNAUTHORIZED.value(),
            error = e.message.toString(),
            path = request.requestURI
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        return ResponseEntity(body, headers, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseBody
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<Any> {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val body = GeneralFault(
            errorCode = HttpStatus.NOT_FOUND.value(),
            error = e.message.toString(),
            path = request.requestURI
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        return ResponseEntity(body, headers, HttpStatus.NOT_FOUND)
    }
}