package com.lasupre.adapter.rxjava3

import com.adazhdw.lasupre.Lasupre
import com.adazhdw.lasupre.RequestFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


fun <T : Any> Observable<T>.subscribeAndroid(
    onResult: ((data: T) -> Unit),
    onError: ((error: Throwable) -> Unit) = {},
    onComplete: (() -> Unit) = {}
) {
    this.subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onError, onComplete, onResult)
}


inline fun <reified T : Any> Lasupre.observableGet(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return get(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}

inline fun <reified T : Any> Lasupre.observablePost(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return post(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}

inline fun <reified T : Any> Lasupre.observablePut(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return put(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}

inline fun <reified T : Any> Lasupre.observablePatch(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return patch(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}

inline fun <reified T : Any> Lasupre.observableDelete(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return delete(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}

inline fun <reified T : Any> Lasupre.observableHead(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return head(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}

inline fun <reified T : Any> Lasupre.observableOptions(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): Observable<T> {
    return options(urlPath).apply { block.invoke(this) }.enqueue<T, Observable<T>>()
}
