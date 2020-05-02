package ru.korolevss.authorization.postadapter

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.post_card.view.*
import ru.korolevss.authorization.R
import ru.korolevss.authorization.dto.PostModel

class RepostViewHolder(
    adapter: PostAdapter,
    view: View,
    list: MutableList<PostModel>): PostViewHolder(adapter, view, list)  {

    @SuppressLint("SetTextI18n")
    override fun bind(post: PostModel) {
        super.bind(post)
        with (view) {
            textViewPost.text = post.textOfPost ?: ""
            textViewSnippet.visibility = View.VISIBLE
            textViewSnippet.text = context.getString(R.string.repost)
        }
    }
}