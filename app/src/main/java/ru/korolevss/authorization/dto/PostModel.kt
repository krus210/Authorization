package ru.korolevss.authorization.dto

data class PostModel(val id: Long,
                     var textOfPost: String? = null,
                     val dateOfPost: String? = null,
                     val nameAuthor: String?,
                     var repostsCount: Int,
                     var likesCount: Int,
                     var isLikedByUser: Boolean,
                     var isRepostedByUser: Boolean,
                     val postType: PostType = PostType.POST,
                     val sourceId: Long? = null,
                     val address: String? = null,
                     val coordinates: Coordinates? = null,
                     val sourceVideo: String? = null,
                     val sourceAd: String? = null,
                     val attachmentId: String? = null) {
    var likeActionPerforming = false
    var repostActionPerforming = false

    fun updatePost(updatedModel: PostModel) {
        if (id != updatedModel.id) throw IllegalAccessException("Ids are different")
        likesCount = updatedModel.likesCount
        isLikedByUser = updatedModel.isLikedByUser
        textOfPost = updatedModel.textOfPost
        repostsCount = updatedModel.repostsCount
        isRepostedByUser = updatedModel.isRepostedByUser
    }
}

enum class PostType {
    POST, EVENT, REPOST, YOUTUBE, AD_POST
}

data class Coordinates(
    val longitude: String,
    val latitude: String
)


