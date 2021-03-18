package com.adazhdw.kthttp.internal

import java.lang.reflect.Type


interface Toable {

    /**消息体转字符串*/
    fun bodyToString():String

    /**
     * @param <T> 目标泛型
     * @param type 目标类型
     * @return 报文体Json文本转JavaBean
     */
    fun <T> toBean(type: Class<T>): T?
    fun <T> toBean(type: Type): T?
    fun <T> toBean(typeRef: TypeRef<T>): T?

    /**
     * @param <T> 目标泛型
     * @param type 目标类型
     * @return 报文体Json文本转JavaBean列表
     */
    fun <T> toList(type: Class<T>): List<T>

}