package cz.utb.fai.common

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

data class GeneralFault(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    var timestamp: ZonedDateTime = ZonedDateTime.now(),
    var errorCode: Int,
    var error: String,
    var path: String,
) {
    constructor(errorCode: Int, error: String, path: String) : this(ZonedDateTime.now(), errorCode, error, path)
}
