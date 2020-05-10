package ru.korolevss.authorization

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_load_after_fail.*
import kotlinx.android.synthetic.main.item_token_after_fail.*
import kotlinx.coroutines.launch
import ru.korolevss.authorization.api.Token
import ru.korolevss.authorization.dto.PostModel
import ru.korolevss.authorization.postadapter.PostAdapter
import ru.korolevss.authorization.postadapter.PostDiffUtilCallback
import java.io.IOException

class FeedActivity : AppCompatActivity(), PostAdapter.OnLikeBtnClickListener,
    PostAdapter.OnRepostBtnClickListener {

    private companion object {
        const val CREATE_POST_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        requestToken()

        fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }

        lifecycleScope.launch {
            try {
                switchDeterminateBar(true)
                val result = Repository.getRecent()
                if (result.isSuccessful) {
                    with(container) {
                        layoutManager = LinearLayoutManager(this@FeedActivity)
                        adapter = PostAdapter(
                            (result.body() ?: emptyList()) as MutableList<PostModel>
                        ).apply {
                            likeBtnClickListener = this@FeedActivity
                            repostBtnClickListener = this@FeedActivity
                        }
                    }
                } else {
                    Toast.makeText(
                        this@FeedActivity,
                        R.string.loading_posts_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }
        swipeContainer.setOnRefreshListener {
            refreshData()
        }

    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val newData = Repository.getRecent()
                swipeContainer.isRefreshing = false
                if (newData.isSuccessful) {
                    with(container) {
                        try {
                            val oldList = (adapter as PostAdapter).list
                            val newList = newData.body()!! as MutableList<PostModel>
                            val postDiffUtilCallback = PostDiffUtilCallback(oldList, newList)
                            val postDiffResult = DiffUtil.calculateDiff(postDiffUtilCallback)
                            (adapter as PostAdapter).newRecentPosts(newList)
                            postDiffResult.dispatchUpdatesTo(adapter as PostAdapter)
                        } catch (e: TypeCastException) {
                            layoutManager = LinearLayoutManager(this@FeedActivity)
                            adapter = PostAdapter(
                                (newData.body() ?: emptyList()) as MutableList<PostModel>
                            ).apply {
                                likeBtnClickListener = this@FeedActivity
                                repostBtnClickListener = this@FeedActivity
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                swipeContainer.isRefreshing = false
                showDialogLoadAfterFail()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREATE_POST_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    try {
                        switchDeterminateBar(true)
                        val result = Repository.getRecent()
                        if (result.isSuccessful) {
                            with(container) {
                                val oldList = (adapter as PostAdapter).list
                                val newList = result.body()!! as MutableList<PostModel>
                                val postDiffUtilCallback = PostDiffUtilCallback(oldList, newList)
                                val postDiffResult = DiffUtil.calculateDiff(postDiffUtilCallback)
                                (adapter as PostAdapter).newRecentPosts(newList)
                                postDiffResult.dispatchUpdatesTo(adapter as PostAdapter)
                            }
                        } else {
                            Toast.makeText(
                                this@FeedActivity,
                                R.string.loading_posts_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            this@FeedActivity,
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

    override fun onLikeBtnClicked(item: PostModel, position: Int) {
        lifecycleScope.launch {
            switchDeterminateBar(true)
            try {
                item.likeActionPerforming = true
                with(container) {
                    adapter?.notifyItemChanged(position)
                    val response = if (item.isLikedByUser) {
                        Repository.dislikedByUser(item.id)
                    } else {
                        Repository.likedByUser(item.id)
                    }
                    item.likeActionPerforming = false
                    if (response.isSuccessful) {
                        item.updatePost(response.body()!!)
                    }
                    adapter?.notifyItemChanged(position)
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }
    }

    override fun onRepostBtnClicked(item: PostModel, position: Int, content: String) {
        lifecycleScope.launch {
            switchDeterminateBar(true)
            try {
                with(container) {
                    adapter?.notifyItemChanged(position)
                    if (item.isRepostedByUser) {
                        Toast.makeText(
                            this@FeedActivity,
                            R.string.cannot_repost_2_time,
                            Toast.LENGTH_SHORT
                        ).show()
                        item.repostActionPerforming = false
                    } else {
                        val response = Repository.repostedByUser(item.id, content)
                        item.repostActionPerforming = false
                        if (response.isSuccessful) {
                            item.updatePost(response.body()!!)
                        }
                    }
                    adapter?.notifyItemChanged(position)
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                switchDeterminateBar(false)
            }
        }
    }

    private fun switchDeterminateBar(isLaunch: Boolean) {
        if (isLaunch) {
            determinateBarFeed.isVisible = true
            fab.isEnabled = false

        } else {
            determinateBarFeed.isVisible = false
            fab.isEnabled = true
        }
    }

    private fun showDialogLoadAfterFail() {
        val dialog = AlertDialog.Builder(this)
            .setView(R.layout.item_load_after_fail)
            .show()
        dialog.loadButtonAfterFail.setOnClickListener {
            refreshData()
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFirstTime(this)) {
            NotificationHelper.sayGoodbye(this)
            setNotFirstTime(this)
        }
    }

    private fun requestToken() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@FeedActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }

            if (isUserResolvableError(code)) {
                getErrorDialog(this@FeedActivity, code, 9000).show()
                return
            }

            Snackbar.make(
                constraint_feed,
                getString(R.string.google_play_unavailable),
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            lifecycleScope.launch {
                Log.i("token", it.token)
                val token = Token(it.token)
                try {
                    val response = Repository.firebasePushToken(token)
                    if (!response.isSuccessful) {
                        showDialogTokenAfterFail()
                    }
                } catch (e: IOException) {
                    showDialogTokenAfterFail()
                }
            }
        }
    }

    private fun showDialogTokenAfterFail() {
        val dialog = AlertDialog.Builder(this)
            .setView(R.layout.item_token_after_fail)
            .show()
        dialog.tokenButtonAfterFail.setOnClickListener {
            requestToken()
            dialog.dismiss()
        }
    }
}
