package ru.korolevss.authorization.service

import android.util.Log
import com.auth0.android.jwt.JWT
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.korolevss.authorization.*
import ru.korolevss.authorization.api.Token
import java.io.IOException

class FCMService : FirebaseMessagingService(), CoroutineScope by MainScope() {

    override fun onMessageReceived(message: RemoteMessage) {
        val recipientId = message.data["recipientId"] ?: ""
        val title = message.data["title"] ?: ""
        val token = getToken(this) ?: ""
        if (token.isNotEmpty() && recipientId.isNotEmpty()) {
            val jwt = JWT(token)
            Log.i("firebase", "$recipientId = ${jwt.getClaim("id").asString()}, $title")
            if (recipientId != jwt.getClaim("id").asString()) {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } else {
                NotificationHelper.notifyFromFCM(this, title)

            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        launch {
            try {
                val response = Repository.firebasePushToken(Token(token))
                if (!response.isSuccessful) {
                        NotificationHelper.notifyFromFCM(
                            this@FCMService,
                            getString(R.string.push_token_on_server_failed)
                        )
                }
            } catch (e: IOException) {
                NotificationHelper.notifyFromFCM(
                    this@FCMService,
                    getString(R.string.push_token_on_server_failed)
                )
            }
        }

    }
}