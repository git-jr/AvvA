package com.paradoxo.avva.model

import com.paradoxo.avva.R

data class Action(
    val text: Int,
    val command: Int,
    val type: ActionType = ActionType.SMART_REPLY,
    val icon: Int? = null
)

enum class ActionType {
    SMART_REPLY,
    EXPLAIN,
    CHECK_INFO,
    TRANSLATE,
}

val listActions = listOf(
    Action(R.string.explain_screen,R.string.explain_screen_command, ActionType.EXPLAIN, R.drawable.ic_explain_screen),
    Action(R.string.check_information, R.string.check_information_command, ActionType.CHECK_INFO, R.drawable.ic_check_info),
    Action(R.string.translate,R.string.translate_command, ActionType.TRANSLATE, R.drawable.ic_translate)
)