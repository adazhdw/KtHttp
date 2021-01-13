package com.adazhdw.kthttp.coroutines

import com.adazhdw.kthttp.coroutines.parser.NormalParser
import com.adazhdw.kthttp.coroutines.parser.Parser
import com.adazhdw.kthttp.request.HttpRequest

/**
 * author：daguozhu
 * date-time：2020/11/18 13:37
 * description： BaseRequest,协程 await 方法
 **/

fun <T> HttpRequest.awaitImpl(
    parser: Parser<T>
): IAwait<T> = IAwaitImpl(this, parser)

inline fun <reified T : Any> HttpRequest.toClazz(): IAwait<T> =
    awaitImpl(object : NormalParser<T>() {})