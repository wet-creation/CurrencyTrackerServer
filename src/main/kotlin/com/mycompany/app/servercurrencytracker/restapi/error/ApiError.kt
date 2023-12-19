import org.springframework.http.HttpStatus


sealed class ApiError  {
    open class Base(
        val timestamp: Long = System.currentTimeMillis(),
        val status: HttpStatus,
        val message: String
    )

    class Unexpected(
        timestamp: Long = System.currentTimeMillis(),
        status: HttpStatus = HttpStatus.BAD_REQUEST,
        message: String = "Unexpected error occurred"
    ) : Base(timestamp, status, message)
    class InternalServerError(
        timestamp: Long = System.currentTimeMillis(),
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        message: String = "Server error occured"
    ) : Base(timestamp, status, message)

    class BadRequest(
        timestamp: Long = System.currentTimeMillis(),
        message: String
    ) : Base(timestamp, HttpStatus.BAD_REQUEST, message)

    class NotFound(
        timestamp: Long = System.currentTimeMillis(),
        message: String
    ) : Base(timestamp, HttpStatus.NOT_FOUND, message)
}