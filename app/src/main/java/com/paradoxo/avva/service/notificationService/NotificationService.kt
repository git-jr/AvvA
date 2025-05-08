package com.paradoxo.avva.service.notificationService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.components.containers.Category
import com.paradoxo.avva.R
import com.paradoxo.avva.ui.voicedetection.AudioClassifierHelper
import com.paradoxo.avva.ui.voicedetection.AudioClassifierHelper.ResultBundle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class NotificationService : Service() {

    lateinit var job: Job
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var audioClassifierHelper: AudioClassifierHelper


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        job = Job()
        coroutineScope = CoroutineScope(Main + job)

        coroutineScope.launch(IO) {
            showNotificationWithImage(
                "Título da Notificação", "Mensagem da Notificação",
                "https://raw.githubusercontent.com/git-jr/sample-files/refs/heads/main/images/ai-generate/cerejeira-neon-1.webp"
            )
        }

        audioClassifierHelper.initClassifier()
        setResultListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Lógica adicional, se necessário
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Canal de Notificações",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Descrição do canal de notificações"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun showTextNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_notification) // Substitua pelo ícone do seu projeto
            .setSmallIcon(R.drawable.avva_face_eyes) // Substitua pelo ícone do seu projeto
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun showNotificationWithImage(
        title: String,
        message: String,
        imageUrl: String
    ) {
        val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())

//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.avva_face_mouth)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.avva_face_eyes) // Substitua pelo ícone do seu projeto
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "notification_service_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        audioClassifierHelper.stopAudioClassification()
    }

    private fun setResultListener() {
        val resultListener = object : AudioClassifierHelper.ClassifierListener {
            override fun onResult(resultBundle: ResultBundle) {
                coroutineScope.launch {
                    resultBundle.results[0].classificationResults().first()
                        .classifications()?.get(0)?.categories()
                        ?.let { categoryList: List<Category> ->
                            Log.d(
                                "ServiceAudioClassifier",
                                "ServiceAudioClassifier Category List: $categoryList"
                            )
                        }

                }
            }

            override fun onError(error: String) {
                Log.e("ServiceAudioClassifier", "ServiceAudioClassifier Error: $error")
            }
        }
        audioClassifierHelper.setListener(resultListener)
    }
}
