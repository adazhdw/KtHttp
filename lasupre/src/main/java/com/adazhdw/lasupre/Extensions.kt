package com.adazhdw.lasupre


suspend inline fun <reified T : Any> Lasupre.get(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return get(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

suspend inline fun <reified T : Any> Lasupre.post(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return post(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

suspend inline fun <reified T : Any> Lasupre.put(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return put(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

suspend inline fun <reified T : Any> Lasupre.patch(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return patch(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

suspend inline fun <reified T : Any> Lasupre.delete(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return delete(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

suspend inline fun <reified T : Any> Lasupre.head(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return head(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

suspend inline fun <reified T : Any> Lasupre.options(
    urlPath: String,
    block: RequestFactory.Builder.() -> Unit = {}
): T {
    return options(urlPath).apply { block.invoke(this) }.parse<T>().await()
}

