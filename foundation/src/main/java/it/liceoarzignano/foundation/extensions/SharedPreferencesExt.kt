package it.liceoarzignano.foundation.extensions

import android.content.SharedPreferences

inline operator fun <reified T : Any> SharedPreferences.get(key: String,
                                                            defaultValue: T? = null): T {
    return when (T::class.java.simpleName) {
        "Boolean" -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        "Float" -> getFloat(key, defaultValue as? Float ?: 0f) as T
        "Integer" -> getInt(key, defaultValue as? Int ?: 0) as T
        "Long" -> getLong(key, defaultValue as? Long ?: 0) as T
        "String" -> getString(key, defaultValue as? String ?: "") as T
        else -> throw IllegalArgumentException("Expected type ${T::class.java.simpleName}" +
                "is not supported")
    }
}

inline operator fun <reified T : Any> SharedPreferences.Editor.set(key: String, value: T) {
    when (value) {
        is Boolean -> putBoolean(key, value)
        is Float -> putFloat(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is String -> putString(key, value)
        else -> throw IllegalArgumentException("Expected type ${T::class.java.simpleName}" +
                "is not supported")
    }
}

inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    operation(editor)
    editor.apply()
}
