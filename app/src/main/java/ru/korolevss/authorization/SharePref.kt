package ru.korolevss.authorization

import android.app.Activity
import android.content.Context
import android.content.Intent
import ru.korolevss.authorization.api.Token

fun savedToken(token: Token?, context: Context) {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.shared_pref),
        Context.MODE_PRIVATE
    )
    with(sharedPref.edit()) {
        putString(
            context.getString(R.string.token_key),
            token?.token
        )
        commit()
    }
}

fun isAuthorized(context: Context) {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.shared_pref),
        Context.MODE_PRIVATE
    )
    val token =sharedPref.getString(
        context.getString(R.string.token_key),
        ""
    )
    if (token != "") {
        val intent = Intent(context, FeedActivity::class.java)
        context.startActivity(intent)
        val activity = context as Activity
        activity.finish()
    }
}