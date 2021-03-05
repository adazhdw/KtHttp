package com.adazhdw.kthttp.request.entity

/**
 * name：MapEntity
 * author：adazhdw
 * date：2021/3/1:10:24
 * description:
 */
open class MapEntity<V> : MutableMap<String, V> {

    val contents: HashMap<String, V> = hashMapOf()

    override val size: Int
        get() = contents.size

    override fun containsKey(key: String): Boolean {
        return contents.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return contents.containsValue(value)
    }

    override fun get(key: String): V? {
        return contents[key]
    }

    override fun isEmpty(): Boolean {
        return contents.isEmpty()
    }

    fun isNotEmpty() = contents.isNotEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
        get() = contents.entries
    override val keys: MutableSet<String>
        get() = contents.keys
    override val values: MutableCollection<V>
        get() = contents.values

    override fun clear() {
        contents.clear()
    }

    override fun put(key: String, value: V): V? {
        return contents.put(key, value)
    }

    override fun putAll(from: Map<out String, V>) {
        contents.putAll(from)
    }

    override fun remove(key: String): V? {
        return contents.remove(key)
    }
}