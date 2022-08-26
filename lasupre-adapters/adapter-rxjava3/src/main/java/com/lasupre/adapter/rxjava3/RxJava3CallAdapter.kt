package com.lasupre.adapter.rxjava3

import com.adazhdw.lasupre.Call
import com.adazhdw.lasupre.CallAdapter
import com.adazhdw.lasupre.Response
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.lang.reflect.Type

class RxJava3CallAdapter<R>(
    private val responseType: Type,
    private val scheduler: Scheduler?,
    private val isAsync: Boolean,
    private val isResult: Boolean,
    private val isBody: Boolean,
    private val isFlowable: Boolean,
    private val isSingle: Boolean,
    private val isMaybe: Boolean,
    private val isCompletable: Boolean
) : CallAdapter<R, Any> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Any {
        val responseObservable: Observable<Response<R>> = if (isAsync) CallEnqueueObservable<R>(call) else CallExecuteObservable<R>(call)

        var observable: Observable<*>
        observable = if (isResult) {
            ResultObservable(responseObservable)
        } else if (isBody) {
            BodyObservable(responseObservable)
        } else {
            responseObservable
        }

        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler)
        }
        if (isFlowable) {
            // We only ever deliver a single value, and the RS spec states that you MUST request at least
            // one element which means we never need to honor backpressure.
            return observable.toFlowable(BackpressureStrategy.MISSING)
        }
        if (isSingle) {
            return observable.singleOrError()
        }
        if (isMaybe) {
            return observable.singleElement()
        }
        return if (isCompletable) {
            observable.ignoreElements()
        } else RxJavaPlugins.onAssembly(observable)
    }
}