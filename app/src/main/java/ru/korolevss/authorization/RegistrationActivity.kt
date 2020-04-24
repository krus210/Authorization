package ru.korolevss.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.*
import ru.korolevss.authorization.api.Token

class RegistrationActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        signUpButton.setOnClickListener {
            val username = usernameRegistrationEditText.text.toString()
            val password1 = password1RegistrationEditText.text.toString()
            val password2 = password2RegistrationEditText.text.toString()
            if (username == "" || password1 == "") {
                Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show()
            } else if (password1 != password2) {
                Toast.makeText(this, R.string.passwords_not_equal, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch{
                    switchDeterminateBar(true)
                    val response = Repository.signUp(username, password1)
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
                    switchDeterminateBar(false)
                }
            }
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarRegistration.isVisible = true
            signUpButton.isEnabled = false
        } else {
            determinateBarRegistration.isVisible = false
            signUpButton.isEnabled = true
        }
    }
}
