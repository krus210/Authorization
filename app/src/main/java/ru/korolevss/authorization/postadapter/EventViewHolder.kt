package ru.korolevss.authorization.postadapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_card.view.*
import ru.korolevss.authorization.dto.PostModel

class EventViewHolder(
    adapter: PostAdapter,
    view: View,
    list: MutableList<PostModel>): PostViewHolder(adapter, view, list) {

    init {
        this.clickEventListener()
    }


    override fun bind(post: PostModel) {
        super.bind(post)
        with (view) {
            textViewPost.text = post.textOfPost
            textViewSnippet.visibility = View.VISIBLE
            textViewSnippet.text = post.address
            imageButtonLocation.visibility = View.VISIBLE
        }
    }

    private fun clickEventListener() {
        with (view) {
            imageButtonLocation.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[adapterPosition]
                    val lat = item.coordinates?.longitude
                    val lng = item.coordinates?.latitude
                    val dataOfCoordinates = Uri.parse("geo:$lat,$lng")
                    transitionToApp(context, dataOfCoordinates)
                }
            }
        }
    }

}

fun transitionToApp(context: Context, dataTransition: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = dataTransition
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}