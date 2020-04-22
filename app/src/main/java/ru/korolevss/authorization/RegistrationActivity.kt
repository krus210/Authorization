package ru.korolevss.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.*
import ru.korolevss.authorization.api.Token

class RegistrationActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        signInButton.setOnClickListener {
            val username = usernameRegistrationEditText.text.toString()
            val password1 = password1RegistrationEditText.text.toString()
            val password2 = password2RegistrationEditText.text.toString()
            if (username == "" || password1 == "") {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show()
            } else if (password1 != password2) {
                Toast.makeText(this, R.string.passwords_not_equal, Toast.LENGTH_SHORT).show()
            } else {
                launch{
                    determinateBarRegistration.visibility = View.VISIBLE
                    val response = Repository.signIn(username, password1)
                    if (response.isSuccessful) {
                        val token: Token? = response.body()
                        savedToken(token, this@RegistrationActivity)
                        Toast.makeText(
                            this@RegistrationActivity,
                            R.string.registration_successful,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegistrationActivity,
                            R.string.registration_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    determinateBarRegistration.visibility = View.GONE
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        cancel()
    }
}
