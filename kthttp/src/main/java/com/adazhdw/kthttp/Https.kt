package com.adazhdw.kthttp

/**
 * author：daguozhu
 * date-time：2020/11/16 15:05
 * description：
 **/
object Https {

    internal val httpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { httpClient() }

    private fun httpClient(): HttpClient {
        val builder = HttpClient.Builder()
        return builder.build()
    }

    fun newBuilder(): HttpClient.Builder {
        return httpClient.newBuilder()
    }

    fun request() = httpClient.request()
    fun getRequest(url: String) = httpClient.request().get().url(url)
    fun postRequest(url: String) = httpClient.request().post().url(url)
    fun headRequest(url: String) = httpClient.request().head().url(url)
    fun deleteRequest(url: String) = httpClient.request().delete().url(url)
    fun putRequest(url: String) = httpClient.request().put().url(url)
    fun patchRequest(url: String) = httpClient.request().patch().url(url)

}