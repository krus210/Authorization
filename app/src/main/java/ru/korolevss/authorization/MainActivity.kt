package ru.korolevss.authorization

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import ru.korolevss.authorization.api.Token

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivityIfAuthorized()

        logInButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username == "" || password == "") {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show()
            } else {
                launch {
                    switchDeterminateBar(true)
                    val response = Repository.authenticate(username, password)
                    if (response.isSuccessful) {
                        val token: Token? = response.body()
                        savedToken(token, this@MainActivity)
                        Toast.makeText(
                            this@MainActivity,
                            R.string.authorization_successful,
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(
                            this@MainActivity,
                            FeedActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.authorization_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    switchDeterminateBar(false)
                }
            }
        }

        registrationButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        startActivityIfAuthorized()
    }

    private fun startActivityIfAuthorized() {
        if (isAuthorized(this)) {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarMain.visibility = View.VISIBLE
            logInButton.isEnabled = false
            registrationButton.isEnabled = false
        } else {
            determinateBarMain.visibility = View.GONE
            logInButton.isEnabled = true
            registrationButton.isEnabled = true
        }
    }

}
