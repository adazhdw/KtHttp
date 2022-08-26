/*
 * Copyright (C) 2020 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lasupre.adapter.rxjava3;

import static com.adazhdw.lasupre.TypeUtils.getParameterUpperBound;
import static com.adazhdw.lasupre.TypeUtils.getRawType;
import com.adazhdw.lasupre.CallAdapter;
import com.adazhdw.lasupre.Lasupre;
import com.adazhdw.lasupre.Response;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;

public final class RxJava3CallAdapterFactory extends CallAdapter.Factory {
    /**
     * Returns an instance which creates asynchronous observables that run on a background thread by
     * default. Applying {@code subscribeOn(..)} has no effect on instances created by the returned
     * factory.
     */
    public static RxJava3CallAdapterFactory create() {
        return new RxJava3CallAdapterFactory(null, true);
    }

    /**
     * Returns an instance which creates synchronous observables that do not operate on any scheduler
     * by default. Applying {@code subscribeOn(..)} will change the scheduler on which the HTTP calls
     * are made.
     */
    public static RxJava3CallAdapterFactory createSynchronous() {
        return new RxJava3CallAdapterFactory(null, false);
    }

    /**
     * Returns an instance which creates synchronous observables that {@code subscribeOn(..)} the
     * supplied {@code scheduler} by default.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static RxJava3CallAdapterFactory createWithScheduler(Scheduler scheduler) {
        if (scheduler == null) throw new NullPointerException("scheduler == null");
        return new RxJava3CallAdapterFactory(scheduler, false);
    }

    private final @Nullable
    Scheduler scheduler;
    private final boolean isAsync;

    private RxJava3CallAdapterFactory(@Nullable Scheduler scheduler, boolean isAsync) {
        this.scheduler = scheduler;
        this.isAsync = isAsync;
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Lasupre net) {
        Class<?> rawType = getRawType(returnType);

        if (rawType == Completable.class) {
            // Completable is not parameterized (which is what the rest of this method deals with) so it
            // can only be created with a single configuration.
            return new RxJava3CallAdapter(Void.class, scheduler, isAsync, false, true, false, false, false, true);
        }

        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;
        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }

        boolean isResult = false;
        boolean isBody = false;
        Type responseType;
        if (!(returnType instanceof ParameterizedType)) {
            String name =
                    isFlowable ? "Flowable" : isSingle ? "Single" : isMaybe ? "Maybe" : "Observable";
            throw new IllegalStateException(
                    name
                            + " return type must be parameterized"
                            + " as "
                            + name
                            + "<Foo> or "
                            + name
                            + "<? extends Foo>");
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "Response must be parameterized" + " as Response<Foo> or Response<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
        } else if (rawObservableType == Result.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "Result must be parameterized" + " as Result<Foo> or Result<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isResult = true;
        } else {
            responseType = observableType;
            isBody = true;
        }

        return new RxJava3CallAdapter(responseType, scheduler, isAsync, isResult, isBody, isFlowable, isSingle, isMaybe, false);
    }
}
