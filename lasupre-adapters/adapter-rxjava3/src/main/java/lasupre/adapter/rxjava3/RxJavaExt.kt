package lasupre.adapter.rxjava3

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