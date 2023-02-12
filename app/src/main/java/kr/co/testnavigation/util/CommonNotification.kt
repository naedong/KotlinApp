package kr.co.testnavigation.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.speech.RecognitionListener
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.co.testnavigation.R
import kr.co.testnavigation.main.MainActivity
import java.time.Instant


private var intent : Intent? = null
private var pendingIntent : PendingIntent? = null
private var uri : Uri? = null



//internal fun CommonNotificationce(packageContext: Context,
//                                  title: String,
//                                  message: String,
//                                CHANNEL_ID : String) :
//        Notification.Builder {
//    return Notification.Builder(packageContext, CHANNEL_ID)
//        .setSmallIcon(R.drawable.ic_baseline_mic_24)
//        .setContentTitle(title)
//        .setContentText(message)
//}


internal fun CommonNotification
            (packageContext: Context,
             title: String,
             message: String): Notification {
    intent = Intent(packageContext, MainActivity::class.java)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    pendingIntent = PendingIntent.getActivity(packageContext, 0, intent,
                                PendingIntent.FLAG_IMMUTABLE)
    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                                                // 채널아이디는 차후에 설정
    var builder = NotificationCompat.Builder(packageContext, "test")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setSound(uri)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(1000, 1000, 1000))
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
        builder = builder.setContent(getCustomDesign(packageContext,title, message))
    }
    else {
        builder = builder.setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_baseline_mic_24)
    }



    return builder.build()

}

fun getCustomDesign(context: Context, title : String, message : String) : RemoteViews {
        var remoteViews =  RemoteViews( context.packageName, R.layout.notication)
        remoteViews.setTextViewText(R.id.noti_title, title)
        remoteViews.setTextViewText(R.id.noti_message, message)
        remoteViews.setImageViewResource(R.id.logo, R.drawable.ic_baseline_mic_24)
    return remoteViews
}


