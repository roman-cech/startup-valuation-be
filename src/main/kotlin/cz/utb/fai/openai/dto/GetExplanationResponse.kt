package cz.utb.fai.openai.dto

import cz.utb.fai.openai.dto.type.Message

data class GetExplanationResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val message: Message
    )
}


