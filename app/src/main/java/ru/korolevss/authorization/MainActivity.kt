package ru.korolevss.authorization

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

        isAuthorized(this)

        logInButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username == "" || password == "") {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show()
            } else {
                launch {
                    determinateBarMain.visibility = View.VISIBLE
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
                    determinateBarMain.visibility = View.GONE
                }
            }
        }

        registrationButton.setOnClickListener {
            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        isAuthorized(this)
    }

    override fun onStop() {
        super.onStop()
        cancel()
    }
}
