package cz.utb.fai.controller

import cz.utb.fai.dto.controller.AuthenticationRequest
import cz.utb.fai.dto.controller.AuthenticationResponse
import cz.utb.fai.dto.controller.Token
import cz.utb.fai.security.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/rest"])
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping(
        path = ["/v1/auth/login"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun authenticate(@RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(
            with(authenticationService.authentication(request.email, request.password)) {
                AuthenticationResponse(
                    token = Token(
                        accessToken = token.accessToken,
                        refreshToken = token.refreshToken,
                        expirationDate = token.expirationDate
                    ),
                    user = AuthenticationResponse.UserSession(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email
                    )
                )
            }
        )

    @PostMapping(path = ["/v1/auth/refresh"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun refreshToken(@RequestParam token: String): ResponseEntity<Token> =
        authenticationService.refreshAccessToken(token)?.let { refreshToken ->
            ResponseEntity.status(HttpStatus.OK).body(refreshToken)
        } ?: ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

    @PostMapping("/v1/auth/logout")
    @ResponseBody
    fun logOut(@RequestParam token: String): ResponseEntity<Unit> =
        authenticationService.logOut(token)?.let { ResponseEntity.noContent().build() } ?: ResponseEntity.badRequest()
            .build()
}