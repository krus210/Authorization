package ru.korolevss.authorization

import android.content.Context
import androidx.core.content.edit
import ru.korolevss.authorization.api.Token


private const val tokenKey = "TOKEN_KEY"
private const val sharedPrefKey = "SHARED_PREF"

fun savedToken(token: Token?, context: Context) {
    val sharedPref = context.getSharedPreferences(
        sharedPrefKey,
        Context.MODE_PRIVATE
    )
    sharedPref.edit {
        putString(
            tokenKey,
            token?.token
        )
    }
}

fun isAuthorized(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences(
        sharedPrefKey,
        Context.MODE_PRIVATE
    )
    val token =sharedPref.getString(
        tokenKey,
        ""
    )
    return !token.isNullOrEmpty()
}