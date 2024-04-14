package cz.utb.fai.dto.controller

import java.util.*

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val expirationDate: Date
)
