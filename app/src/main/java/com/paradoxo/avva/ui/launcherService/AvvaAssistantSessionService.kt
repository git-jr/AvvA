package com.paradoxo.avva.ui.launcherService

import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService


class AvvaAssistantSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(bundle: Bundle): VoiceInteractionSession {
        return AssistantSessionService(this)
    }
}