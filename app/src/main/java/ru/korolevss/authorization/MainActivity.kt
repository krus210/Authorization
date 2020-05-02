package ru.korolevss.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import ru.korolevss.authorization.api.Token
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivityIfAuthorized()

        logInButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    try {
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
                            startActivityIfAuthorized()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.authorization_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        switchDeterminateBar(false)
                    }
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
        val token = getToken(this)
        if (!token.isNullOrEmpty()) {
            Repository.createRetrofitWithAuth(token)
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarMain.isVisible = true
            logInButton.isEnabled = false
            registrationButton.isEnabled = false
        } else {
            determinateBarMain.isVisible = false
            logInButton.isEnabled = true
            registrationButton.isEnabled = true
            determinateBarMain.isVisible = false
        }
    }

}
