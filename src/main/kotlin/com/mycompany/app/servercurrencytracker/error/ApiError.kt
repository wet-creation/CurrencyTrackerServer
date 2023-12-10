import org.springframework.http.HttpStatus
import java.sql.Timestamp


sealed class ApiError {
    class Unexpected(
        val timestamp: Long = System.currentTimeMillis(),
        val status: HttpStatus = HttpStatus.BAD_REQUEST,
        val message: String = "Unexpected error occured"
    ): ApiError()
    class BadRequest(
        val timestamp: Long = System.currentTimeMillis(),
        val status: HttpStatus = HttpStatus.BAD_REQUEST,
        val message: String
    ): ApiError()
    class NotFound(
        val timestamp: Long = System.currentTimeMillis(),
        val status: HttpStatus = HttpStatus.NOT_FOUND,
        val message: String
    ): ApiError()
}
//data class ApiError(
//    val timestamp: Long = System.currentTimeMillis(),
//    val status: HttpStatus,
//    val message: String
//){
//    constructor(timestamp: Long = System.currentTimeMillis(), status: HttpStatus = HttpStatus.BAD_REQUEST, message: String = "") : this()
//}