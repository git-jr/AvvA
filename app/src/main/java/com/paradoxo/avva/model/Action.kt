package com.paradoxo.avva.model

data class Action(
    val text: String,
    val action: SuggestionAction = SuggestionAction.SMART_REPLY,
    val icon: Int? = null
)

enum class SuggestionAction {
    SMART_REPLY,
    EXPLAIN,
    CHECK_INFO,
    TRANSLATE,
}
