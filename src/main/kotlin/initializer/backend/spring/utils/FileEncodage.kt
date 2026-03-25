package initializer.backend.spring.utils

import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.util.Base64
import java.io.*

class Base64DecodedMultipartFile(
    private val content: ByteArray,
    private val headerName: String,
    private val originalFileName: String,
    private val mimeType: String
) : MultipartFile {

    override fun getName(): String = headerName
    override fun getOriginalFilename(): String = originalFileName
    override fun getContentType(): String = mimeType
    override fun isEmpty(): Boolean = content.isEmpty()
    override fun getSize(): Long = content.size.toLong()
    override fun getBytes(): ByteArray = content
    override fun getInputStream(): InputStream = ByteArrayInputStream(content)

    override fun transferTo(dest: File) {
        FileOutputStream(dest).use { it.write(content) }
    }
}


//fun base64ToMultipartFile(
//    base64: String,
//    prefix : String ="",
//    fieldName: String = "file",
//    mimeType: String = "image/png"
//): Base64DecodedMultipartFile {
//    val timestamp = System.currentTimeMillis()
//    val fileName: String = "image_${prefix}_${timestamp}.png"
//    val imageBytes = Base64.getDecoder().decode(base64)
//
//    return Base64DecodedMultipartFile(fieldName, fileName, mimeType, imageBytes)
//}

fun base64ToMultipartFile(
    base64: String,
    prefix: String = "",
    fieldName: String = "file",
    mimeType: String = "image/png"
): MultipartFile {
    if(!isBase64(base64)) throw ResponseStatusException(HttpStatus.BAD_REQUEST,"La chaine fournit n'est pas en Base 64")
    val timestamp = System.currentTimeMillis()
    val fileName = "image_${prefix}_${timestamp}.png"
    val imageBytes = Base64.getDecoder().decode(base64)

    return Base64DecodedMultipartFile(imageBytes, fieldName, fileName, mimeType)
}