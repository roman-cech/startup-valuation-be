package cz.utb.fai.connector

data class GetExplanationResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val message: Message
    )
}


