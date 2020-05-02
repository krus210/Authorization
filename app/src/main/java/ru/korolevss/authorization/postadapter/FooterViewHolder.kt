package ru.korolevss.authorization.postadapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.korolevss.authorization.R
import ru.korolevss.authorization.Repository
import java.io.IOException

class FooterViewHolder(private val adapter: PostAdapter, view: View) :
    RecyclerView.ViewHolder(view) {
    init {
        with(view) {
            loadMoreBtn.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        it.isEnabled = false
                        progressbarMore.isEnabled = true
                        val lastItemId = adapter.list[0].id
                        val response = Repository.getPostsBefore(lastItemId)
                        if (response.isSuccessful) {
                            val newItems = response.body()!!
                            adapter.list.addAll(adapter.list.size -1, newItems)
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
                        progressbarMore.isEnabled = false
                    }
                }
            }
        }
    }

}