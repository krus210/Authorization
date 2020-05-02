package ru.korolevss.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.coroutines.launch
import ru.korolevss.authorization.dto.PostModel
import ru.korolevss.authorization.postadapter.PostAdapter
import java.io.IOException

class FeedActivity : AppCompatActivity(), PostAdapter.OnLikeBtnClickListener,
    PostAdapter.OnRepostBtnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onStart() {
        super.onStart()
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
                    with (container) {
                        (adapter as PostAdapter).newRecentPosts(newData.body()!!)
                        adapter?.notifyDataSetChanged()
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    R.string.connect_to_server_failed,
                    Toast.LENGTH_SHORT
                ).show()
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
}
