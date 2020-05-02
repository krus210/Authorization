package ru.korolevss.authorization.postadapter

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_card.view.*
import ru.korolevss.authorization.R
import ru.korolevss.authorization.dto.PostModel

class AdViewHolder(
    adapter: PostAdapter,
    view: View,
    list: MutableList<PostModel>): PostViewHolder(adapter, view, list)  {

    init {
        this.clickAdListener()
    }

    override fun bind(post: PostModel) {
        super.bind(post)
        with (view) {
            textViewPost.text = post.textOfPost
            textViewSnippet.visibility = View.VISIBLE
            textViewSnippet.text = context.getString(R.string.ad_post)
            imageButtonLink.visibility = View.VISIBLE
            imageButtonLink.setImageResource(R.drawable.photo_ad)
        }
    }

    private fun clickAdListener() {
        with (view) {
            imageButtonLink.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[adapterPosition]
                    val data = Uri.parse(item.sourceAd)
                    transitionToApp(context, data)
                }
            }
        }
    }
}