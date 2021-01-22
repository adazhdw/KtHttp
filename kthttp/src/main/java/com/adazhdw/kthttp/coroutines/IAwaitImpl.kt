package com.adazhdw.kthttp.coroutines

import com.adazhdw.kthttp.coroutines.parser.Parser
import com.adazhdw.kthttp.request.IRequest

/**
 * author：daguozhu
 * date-time：2020/11/18 13:29
 * description：
 **/
class IAwaitImpl<T>(
    private val iRequest: IRequest<*>,
    private val parser: Parser<T>
) : IAwait<T> {
    override suspend fun await(): T {
        return try {
            val call = iRequest.getRawCall()
            call.await(parser)
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}