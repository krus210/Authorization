package ru.korolevss.authorization.postadapter

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.post_card.view.*
import ru.korolevss.authorization.R
import ru.korolevss.authorization.dto.PostModel

open class PostViewHolder(
    private val adapter: PostAdapter, val view: View, var list: MutableList<PostModel>
) : RecyclerView.ViewHolder(view) {

    init {
        this.clickButtonListener()
    }

    open fun bind(post: PostModel) {
        with(view) {
            textViewDate.text = post.dateOfPost
            textViewPost.text = post.textOfPost
            textViewAuthor.text = post.nameAuthor
            textViewLikeCount.text = post.likesCount.toString()
            textViewRepostCount.text = post.repostsCount.toString()
            fillCount(textViewLikeCount, post.likesCount)
            fillCount(textViewRepostCount, post.repostsCount)
            when {
                post.likeActionPerforming -> {
                    imageButtonLike.setImageResource(R.drawable.ic_like_blue)
                }
                post.isLikedByUser -> {
                    imageButtonLike.setImageResource(R.drawable.ic_like_red)
                    textViewLikeCount.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorRed
                        )
                    )
                }
                else -> {
                    imageButtonLike.setImageResource(R.drawable.ic_like_gray)
                    textViewLikeCount.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.tab_indicator_text
                        )
                    )
                }
            }
            when {
                post.repostActionPerforming -> {
                    imageButtonRepost.setImageResource(R.drawable.ic_reply_blue)
                }
                post.isRepostedByUser -> {
                    imageButtonRepost.setImageResource(R.drawable.ic_reply_red)
                    textViewRepostCount.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorRed
                        )
                    )
                }
                else -> {
                    imageButtonRepost.setImageResource(R.drawable.ic_reply_gray)
                    textViewRepostCount.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.tab_indicator_text
                        )
                    )
                }
            }


        }
    }

    private fun clickButtonListener() {
        with(view) {
            imageButtonLike.setOnClickListener {
                it as ImageButton
                val currentPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[currentPosition]
                    if (item.likeActionPerforming) {
                        Toast.makeText(
                            context,
                            R.string.like_is_performing,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter.likeBtnClickListener?.onLikeBtnClicked(item, currentPosition)
                    }
                }
            }
            imageButtonRepost.setOnClickListener {
                it as ImageButton
                val currentPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[currentPosition]
                    if (item.isRepostedByUser) {
                        Toast.makeText(
                            context,
                            R.string.cannot_repost_2_time,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showDialog(context) {content ->
                            adapter.repostBtnClickListener?.onRepostBtnClicked(
                                item,
                                currentPosition,
                                content
                            )
                        }
                    }
                }
            }

            imageButtonDelete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    list.removeAt(adapterPosition)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

}

fun showDialog(context: Context, createBtnClicked: (content: String) -> Unit) {
    val dialog = AlertDialog.Builder(context)
        .setView(R.layout.activity_create_post)
        .show()
    dialog.createButton.setOnClickListener {
        createBtnClicked(dialog.enterContentEditText.text.toString())
        dialog.dismiss()
    }
}

fun fillCount(view: TextView, count: Int) {
    if (count == 0) {
        view.isEnabled = false
    } else {
        view.isEnabled = true
        view.text = count.toString()
    }
}
