package cz.utb.fai.openai

data class GetExplanationResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val message: Message
    )
}


