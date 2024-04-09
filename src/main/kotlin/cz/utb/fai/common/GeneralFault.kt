package cz.utb.fai.common

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

data class GeneralFault(
    var code: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    var timestamp: ZonedDateTime = ZonedDateTime.now(),
    var message: String
) {
    constructor(code: Int, message: String) : this(code, ZonedDateTime.now(), message)
}
