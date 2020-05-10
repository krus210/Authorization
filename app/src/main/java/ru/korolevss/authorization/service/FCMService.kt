package ru.korolevss.authorization.service

import android.util.Log
import com.auth0.android.jwt.JWT
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.korolevss.authorization.NotificationHelper
import ru.korolevss.authorization.api.Token
import ru.korolevss.authorization.getToken
import ru.korolevss.authorization.savedToken

class FCMService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val recipientId = message.data["recipientId"] ?: ""
        val title = message.data["title"] ?: ""
        val token = getToken(this) ?: ""
        if (token.isNotEmpty() && recipientId.isNotEmpty()) {
            val jwt = JWT(token)
            Log.i("firebase", "$recipientId = ${jwt.getClaim("id").asString()}, $title")
            if (recipientId != jwt.getClaim("id").asString()) {
                savedToken(Token(""), this)
            } else {
                NotificationHelper.notifyFromFCM(this, title)

            }
        }

    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}