package com.adazhdw.kthttp.request.exception

/**
 * name：HttpException
 * author：adazhdw
 * date：2021/3/5:16:43
 * description:
 */
class HttpException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}