package cz.utb.fai.jwt

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)