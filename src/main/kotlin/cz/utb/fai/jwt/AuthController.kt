package cz.utb.fai.jwt

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping(path = ["/rest/v1/auth"])
    fun authenticate(@RequestBody request: AuthenticationRequest): String =
        authenticationService.authentication(request.email, request.password)
}

data class AuthenticationRequest(
    val email: String,
    val password: String,
)