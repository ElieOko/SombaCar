package emy.backend.barua.utils

import java.util.Base64

fun transformRoute(location : String): String {
    var transform = location.lowercase()
    val state = transform.contains("_")
    if (state){
        transform = transform.replace('_','/')
    }
    return transform
}
fun locationApi(source: String,location: String): String {
    val endpoint = transformRoute(location)
    val src = source.lowercase().replace("/","")
    return "/$src/$endpoint"
}


fun isBase64(input: String): Boolean {
    return try {
        Base64.getDecoder().decode(input)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}