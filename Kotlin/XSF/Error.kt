/**
 * TODO : Document DecoratedError
 */
open class DecoratedError(type: String, message: String, code: Int? = null) : Throwable(message) {
    init {
        print(
            "\n\u001b[31m\u001b[1m[\u001b[0m\u001b[31m ${
                type.uppercase().replace(" ", "-")
            }-ERROR : $message \u001b[1m]"
        )

        if (code != null) {
            print(" <Code $code>")
        }

        print("\u001b[0m\n\n")
    }
}

/**
 * TODO : Document DecoratedWarning
 */
open class DecoratedWarning(type: String, message: String) : Exception(message) {
    init {
        print("\u001b[31m")
        println("\u001b[1m[\u001B[0m\u001b[31m ${type.uppercase()}-WARNING : $message \u001B[1m]\u001B[0m")
        print("\u001b[0m")
    }
}

/**
 * Custom error class for XSF files
 * @param message The error message to display
 */
class InvalidXSFFile(message: String) : DecoratedError("XSF", message)
