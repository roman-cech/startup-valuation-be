package cz.utb.fai.jwt

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class AuthController(
    private val authenticationService: AuthenticationService,
    private val tokenService: TokenService
) {
    @PostMapping(path = ["/rest/v1/auth"])
    fun authenticate(@RequestBody request: AuthenticationRequest): String =
        authenticationService.authentication(request.email, request.password)

    @PostMapping("/rest/v1/auth/{token}/sign-out")
    fun signOut(@PathVariable token: String): String =
        tokenService.addToBlacklist(token).let { "Token invalidated successfully" }
}

data class AuthenticationRequest(
    val email: String,
    val password: String,
)