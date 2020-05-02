package ru.korolevss.authorization.postadapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_new.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.korolevss.authorization.R
import ru.korolevss.authorization.Repository
import java.io.IOException


class HeaderViewHolder(private val adapter: PostAdapter, view: View) :
    RecyclerView.ViewHolder(view) {
    init {
        with(view) {
            loadNewButton.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        it.isEnabled = false
                        progressbarNew.isEnabled = true
                        val firstItemId = adapter.list[0].id
                        val response = Repository.getPostsAfter(firstItemId)
                        if (response.isSuccessful) {
                            val newItems = response.body()!!
                            adapter.list.addAll(0, newItems)
                            adapter.notifyItemRangeInserted(0, newItems.size)
                        } else {
                            Toast.makeText(
                                context,
                                R.string.loading_posts_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            context,
                            R.string.connect_to_server_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        it.isEnabled = true
                        progressbarNew.isEnabled = false
                    }
                }
            }
        }
    }

}