package com.lasupre.adapter.flow

import com.adazhdw.lasupre.Lasupre
import com.adazhdw.lasupre.RequestFactory
import kotlinx.coroutines.flow.Flow

inline fun <reified T : Any> Lasupre.flowGet(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return get(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}

inline fun <reified T : Any> Lasupre.flowPost(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return post(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}

inline fun <reified T : Any> Lasupre.flowPut(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return put(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}

inline fun <reified T : Any> Lasupre.flowPatch(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return patch(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}

inline fun <reified T : Any> Lasupre.flowDelete(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return delete(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}

inline fun <reified T : Any> Lasupre.flowHead(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return head(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}

inline fun <reified T : Any> Lasupre.flowOptions(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Flow<T> {
    return options(urlPath).apply { block.invoke(this) }.enqueue<T, Flow<T>>()
}
