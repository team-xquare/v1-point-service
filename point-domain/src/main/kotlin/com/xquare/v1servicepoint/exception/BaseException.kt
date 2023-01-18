package com.xquare.v1servicepoint.exception

abstract class BaseException(
    override val errorMessage: String,
    override val statusCode: Int
) : RuntimeException(errorMessage), ExceptionProperty {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}
