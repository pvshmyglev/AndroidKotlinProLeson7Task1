package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import java.util.Collections.emptyList

class PostRepositoryHTTPImpl() : PostRepository {

    override val data: MutableLiveData<List<Post>> = MutableLiveData(emptyList())

    override fun getAll() : List<Post> {

        return PostsApi.retrofitService.getAll()
            .execute()
            .let {

                if (it.isSuccessful) {
                    it.body() ?: error("body is null!")
                } else {
                    throw RuntimeException(it.message())
                }

            }

    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {

        return PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {

                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: run {
                            callback.onError(RuntimeException("Body is null"))
                            return
                        }
                        callback.onSuccess(body)
                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    override fun getById(id: Long) : Post {

        return PostsApi.retrofitService.getById(id)
            .execute()
            .let {
                it.body() ?: throw RuntimeException("body is null!")
            }

    }

    override fun getByIdAsync(id: Long, callback: PostRepository.GetByIdCallback) {

        PostsApi.retrofitService.getById(id)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: run {
                            callback.onError(RuntimeException("Body is null"))
                            return
                        }
                        callback.onSuccess(body)
                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
               }
            })

    }

    override fun likeById(id: Long) {

        val postOnServer = getById(id)

        postOnServer.let { post ->
            if (post.likeByMe) {
                PostsApi.retrofitService.dislikeById(id)
            } else {
                PostsApi.retrofitService.likeById(id)
            }
        }

    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeByIdCallback) {


        PostsApi.retrofitService.likeById(id)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {

                    if (response.isSuccessful) {

                        val body = response.body() ?: run {
                            callback.onError(RuntimeException("Body is null"))
                            return
                        }
                        callback.onSuccess(body)

                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })

    }

    override fun dislikeByIdAsync(id: Long, callback: PostRepository.DislikeByIdCallback) {

        PostsApi.retrofitService.dislikeById(id)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: run {
                            callback.onError(RuntimeException("Body is null"))
                            return
                        }
                        callback.onSuccess(body)
                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })

    }


    override fun shareById(id: Long) {

    }


    override fun removeById(id: Long) {

        PostsApi.retrofitService.removeById(id)
        .execute()

    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveByIdCallback) {

        PostsApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {

                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }

                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })

    }

    override fun saveNewPost(post: Post) {
        PostsApi.retrofitService.save(post).execute()
    }

    override fun saveNewPostAsync(post: Post, callback: PostRepository.SaveNewPostCallback) {

        PostsApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    override fun editPost(post: Post) {
        PostsApi.retrofitService.save(post).execute()
    }

    override fun editPostAsync(post: Post, callback: PostRepository.EditPostCallback) {

        PostsApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(RuntimeException(response.toString()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })

    }

}