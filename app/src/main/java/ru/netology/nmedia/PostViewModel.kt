package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException

class PostViewModel (application: Application) : AndroidViewModel(application), PostInteractionCommands{

    private val repository : PostRepository = PostRepositoryHTTPImpl()

    private val _data = MutableLiveData(FeedModel())
    val data : LiveData<FeedModel>
        get() = _data

    private val emptyPost = Post(
        0L,
        "",
        "",
        "",
        "",
        "",
        false,
        0,
        0,
        0
    )

    val editedPost = MutableLiveData(emptyPost)
    val openedPost = MutableLiveData(emptyPost)

    private val _postUpdated = SingleLiveEvent<Post>()
    val postUpdated: LiveData<Post>
        get() = _postUpdated

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: MutableLiveData<String>
        get() = _errorMessage

    fun updatedPost(post: Post) {

        if (post.id == 0L) {

            loadPosts()

        } else {

            _data.value?.posts?.map { thisPost -> if (thisPost.id == post.id) { post } else { thisPost } }
                ?.let{ postsList ->
                    _data.value = FeedModel(posts = postsList, empty = postsList.isEmpty())
                }


        }

    }

    fun loadPosts() {

        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {

            override fun onSuccess(posts: List<Post>) {
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Exception) {
                _data.value = FeedModel(error = true)
            }


        })

    }

    private fun setObserveEditOpenPost(id: Long) {

        if (editedPost.value?.id != 0L && editedPost.value?.id == id) {

            data.value?.posts?.map { post ->
                if (post.id == editedPost.value?.id) { editedPost.value = post }
            }

        }

        if (openedPost.value?.id != 0L && openedPost.value?.id == id) {

            data.value?.posts?.map { post ->
                if (post.id == openedPost.value?.id) { openedPost.value = post }
            }

        }

    }

    override fun onLike(post: Post) {

        repository.getByIdAsync(post.id, object : PostRepository.GetByIdCallback {

            override fun onSuccess(post: Post) {

                if (post.likeByMe) {

                    repository.dislikeByIdAsync(post.id, object : PostRepository.DislikeByIdCallback {

                        override fun onSuccess(postOnServer: Post) {
                            _postUpdated.setValue(postOnServer)
                        }

                        override fun onError(e: Exception) {
                            val textError = "Не удалось снять лайк. "
                            _errorMessage.setValue(textError + e.toString())
                        }

                    })


                } else {

                    repository.likeByIdAsync(post.id, object : PostRepository.LikeByIdCallback {

                        override fun onSuccess(postOnServer: Post) {
                            _postUpdated.setValue(postOnServer)
                        }

                        override fun onError(e: Exception) {
                            val textError = "Не удалось установить лайк. "
                            _errorMessage.setValue(textError + e.toString())
                        }

                    })

                }

            }

            override fun onError(e: Exception) {
                val textError = "Не удалось получить данные поста с сервера. "
                _errorMessage.setValue(textError + e.toString())
            }

        })

    }

    override fun onShare(post: Post) {



    }

    override fun onRemove(post: Post) {

        repository.removeByIdAsync(post.id, object : PostRepository.RemoveByIdCallback {

            override fun onSuccess() {
                _postUpdated.postValue(emptyPost)

                onCancelEdit()
                onCancelOpen()
            }

            override fun onError(e: Exception) {
                val textError = "Не удалось удалить пост. "
                _errorMessage.setValue(textError + e.toString())
            }

        })

    }

    override fun onEditPost(post: Post) {

        editedPost.value = post

    }

    override fun onSaveContent(newContent: String) {

        val text = newContent.trim()

        editedPost.value?.let { thisEditedPost ->

            if (thisEditedPost.content != text) {

                val postForSaved = thisEditedPost.copy(content = text)

                if (thisEditedPost.id == 0L) {

                    repository.saveNewPostAsync(postForSaved, object : PostRepository.SaveNewPostCallback {

                        override fun onSuccess() {
                            _postUpdated.setValue(emptyPost)
                        }

                        override fun onError(e: Exception) {
                            val textError = "Не удалось сохранить новый пост. "
                            _errorMessage.setValue(textError + e.toString())
                        }

                    })

                } else {

                    repository.editPostAsync(postForSaved, object : PostRepository.EditPostCallback {

                        override fun onSuccess() {
                            _postUpdated.setValue(postForSaved)
                        }

                        override fun onError(e: Exception) {
                            val textError = "Не удалось обновить пост. "
                            _errorMessage.setValue(textError + e.toString())
                        }

                    })

                }

            }

            editedPost.value = emptyPost

            setObserveEditOpenPost(thisEditedPost.id)

        }


    }

    override fun onCancelEdit() {

        editedPost.value = emptyPost

    }

    override fun onOpenPost(post: Post) {

        openedPost.value = post

    }

    override fun onCancelOpen() {

        openedPost.value = emptyPost

    }

}
