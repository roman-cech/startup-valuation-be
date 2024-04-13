package cz.utb.fai.jwt

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/rest"])
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping(path = ["/v1/auth"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun authenticate(@RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(
            with(authenticationService.authentication(request.email, request.password)) {
                AuthenticationResponse(
                    token = AuthenticationResponse.Token(accessToken = token.accessToken, refreshToken = token.refreshToken),
                    user = AuthenticationResponse.UserSession(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email
                    )
                )
            }
        )

    @PostMapping(path = ["/v1/refresh"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun refreshToken(@RequestParam token: String): ResponseEntity<String> =
        authenticationService.refreshAccessToken(token)?.let { refreshToken ->
            ResponseEntity.status(HttpStatus.OK).body(refreshToken)
        } ?: ResponseEntity.status(HttpStatus.FORBIDDEN).build()

    @PostMapping("/v1/log-out")
    @ResponseBody
    fun logOut(@RequestParam token: String): ResponseEntity<Unit> {
        authenticationService.logOut(token)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}