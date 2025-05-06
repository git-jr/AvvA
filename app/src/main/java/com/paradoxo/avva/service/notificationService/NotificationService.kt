package com.paradoxo.avva.service.notificationService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.paradoxo.avva.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.coroutines.CoroutineContext

class NotificationService : Service() {

    lateinit var job: Job
    lateinit var coroutineScope: CoroutineScope

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
    }
}
