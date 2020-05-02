package ru.korolevss.authorization.postadapter

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_card.view.*
import ru.korolevss.authorization.R
import ru.korolevss.authorization.dto.PostModel

class YoutubeViewHolder(
    adapter: PostAdapter,
    view: View,
    list: MutableList<PostModel>): PostViewHolder(adapter, view, list)  {

    init {
        this.clickYoutubeListener()
    }

    override fun bind(post: PostModel) {
        super.bind(post)
        with(view) {
            textViewPost.text = post.textOfPost
            imageButtonLink.visibility = View.VISIBLE
            imageButtonLink.setImageResource(R.drawable.photo_youtube)
        }
    }

    private fun clickYoutubeListener() {
        with (view) {
            imageButtonLink.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[adapterPosition]
                    val data = Uri.parse(
                        "vnd.youtube:" +
                                item.sourceVideo
                    )
                    transitionToApp(context, data)
                }
            }
        }
    }
}