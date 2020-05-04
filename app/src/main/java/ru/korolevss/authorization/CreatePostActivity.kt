package ru.korolevss.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.launch
import java.io.IOException

class CreatePostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        createButton.setOnClickListener {
            val textContent = enterContentEditText.text.toString()
            if (textContent.isEmpty()) {
                Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    try {
                        switchDeterminateBar(true)
                        val response = Repository.createPost(textContent)
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@CreatePostActivity,
                                R.string.create_post_is_successful,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@CreatePostActivity,
                                FeedActivity::class.java
                            )
                            startActivityForResult(intent, 1)
                            finish()
                        } else {
                            Toast.makeText(
                                this@CreatePostActivity,
                                R.string.create_post_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@CreatePostActivity,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        switchDeterminateBar(false)
                    }

                }
            }
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarCreatePost.isVisible = true
            createButton.isEnabled = false
        } else {
            determinateBarCreatePost.isVisible = false
            createButton.isEnabled = true
        }
    }
}
