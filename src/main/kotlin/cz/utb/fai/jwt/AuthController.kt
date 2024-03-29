package cz.utb.fai.jwt

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
@CrossOrigin(origins = ["\${frontend.scheme}"])
class AuthController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping(path = ["/rest/v1/auth"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun authenticate(@RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(
            authenticationService.authentication(request.email, request.password).let { res ->
                AuthenticationResponse(
                    accessToken = res.accessToken,
                    refreshToken = res.refreshToken
                )
            }
        )

    @PostMapping(path = ["/rest/v1/refresh"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun refreshToken(@RequestParam token: String): ResponseEntity<String> =
        authenticationService.refreshAccessToken(token)?.let { refreshToken ->
            ResponseEntity.status(HttpStatus.OK).body(refreshToken)
        } ?: ResponseEntity.status(HttpStatus.FORBIDDEN).build()

    @PostMapping("/rest/v1/log-out", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun logOut(@RequestParam token: String): ResponseEntity<Unit> {
        authenticationService.logOut(token)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}