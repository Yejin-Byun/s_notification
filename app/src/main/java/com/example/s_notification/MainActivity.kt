package com.example.s_notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.s_notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.notificationButton.setOnClickListener {
            notification()
        }
    }

    private fun notification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 26 버전 이상일 경우
            val channelId = "one-channel"
            val channelName = "My Channel One"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT

            ).apply {
                // 채널에 다양한 정보 설정
                description = "My Channel One Description"
                setShowBadge(true) // 뱃지 설정

                // 알림이 울릴 때 소리 설정
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(uri, audioAttributes)
                enableVibration(true) // 진동 넣을지 말지
            }
            // 채널을 NotificationManager 에 등록
            manager.createNotificationChannel(channel)

            // 채널을 이용하여 builder 생성
            builder = NotificationCompat.Builder(this, channelId)

        } else {
            // 26 버전 이하
            builder = NotificationCompat.Builder(this)
        }

        // 알림 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                // 알림 권한이 없다면, 사용자에게 권한 요청
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
        }

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
        val intent = Intent(this, SecondActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // 알림의 기본 경로
        builder.run {
            setSmallIcon(R.mipmap.ic_launcher)
            setWhen(System.currentTimeMillis()) // 알림 발생 시간 = 현재 시간
            setContentTitle("새로운 알림입니다.")
            setContentText("알림이 잘 보이시나요.")
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("이것은 긴 텍스트 샘플입니다. 아주 긴 텍스트를 쓸 때는 여기에 사용하면 됩니다. 이것은 매우 긴 텍스트 샘플입니다. 아주 긴 텍스트 샘플을 사용할 때는 이 기능을 사용하세요.")
            )
            setLargeIcon(bitmap)
//            setStyle(NotificationCompat.BigPictureStyle()
//                .bigPicture(bitmap)
//                .bigLargeIcon(null)) // hide Lagericon while expanding
            addAction(R.mipmap.ic_launcher, "Action", pendingIntent)// 알림을 클릭했을 때 pendingIntent 를 호출하여 SecondActivity가 실행됨
        }

        manager.notify(11, builder.build())
    }
}