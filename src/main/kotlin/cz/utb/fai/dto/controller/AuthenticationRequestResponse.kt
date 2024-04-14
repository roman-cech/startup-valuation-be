package cz.utb.fai.dto.controller

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
data class AuthenticationResponse(
    val token: Token,
    val user: UserSession,
) {
    data class UserSession (
        val firstName: String,
        val lastName: String,
        val email: String
    )
}

