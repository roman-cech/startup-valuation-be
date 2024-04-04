package cz.utb.fai.jwt

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
data class AuthenticationResponse(
    val token: Token,
    val user: UserSession,
) {
    data class Token(
        val accessToken: String,
        val refreshToken: String,
    )

    data class UserSession (
        val firstName: String,
        val lastName: String,
        val email: String
    )
}

