package ru.korolevss.authorization.postadapter

import androidx.recyclerview.widget.DiffUtil
import ru.korolevss.authorization.dto.PostModel

class PostDiffUtilCallback(
    private val oldList: MutableList<PostModel>,
    private val newList: MutableList<PostModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldModel = oldList[oldItemPosition]
        val newModel = newList[newItemPosition]
        return oldModel.id == newModel.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldModel = oldList[oldItemPosition]
        val newModel = newList[newItemPosition]
        return oldModel.textOfPost == newModel.textOfPost
                && oldModel.likesCount == newModel.likesCount
                && oldModel.isLikedByUser == newModel.isLikedByUser
                && oldModel.likeActionPerforming == newModel.likeActionPerforming
                && oldModel.repostsCount == newModel.repostsCount
                && oldModel.isRepostedByUser == newModel.isRepostedByUser
                && oldModel.repostActionPerforming == newModel.repostActionPerforming
    }

}