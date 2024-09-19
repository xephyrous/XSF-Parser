package xsf

import xsf.Error.DecoratedError
import java.security.MessageDigest
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * The level of error strictness during parsing
 *
 * @property STRICT Fatal error, program exits, callback function is called if set
 * @property WARNING Minor error, prints warning of error, callback function is called if set
 * @property LENIENT Insignificant error, callback function is called if set
 */
enum class XSFParseLevel {
    STRICT,
    WARNING,
    LENIENT,
}

/**
 * Custom error class for XSF files
 * @param message The error message to display
 */
class InvalidXSFFile(message: String) : Error("[ XSF-ERROR] $message")

fun getStableHash(obj: Any): String {
    val messageDigest = MessageDigest.getInstance("SHA-1")
    val bytes = obj.toString().toByteArray()
    val hashBytes = messageDigest.digest(bytes)
    return hashBytes.joinToString("") { String.format("%02x", it) }
}

/**
 * Extracts the class type from a [KCallable]
 * @param obj The object to extract from
 */
fun extractClass(obj: KCallable<*>): KClass<*> {
    return obj.returnType.classifier as KClass<*>
}

/**
 * Exctracts the class parameter type from a [KCallable]
 * @param obj THe object to extract from
 * @index The index of the parameter to extract
 */
fun extractClassParam(obj: KCallable<*>, index: Int): KClass<*> {
    if (index >= obj.returnType.arguments.size) {
        throw IndexOutOfBoundsException("Invalid type parameter index of $index!")
    }

    return obj.returnType.arguments[index].type?.classifier as KClass<*>
}

/**
 * Houses the internal variables for the XSF file format
 * @see XSFParser
 * @see XSFFile
 */
object Internal {
    /**
     * Flags for keyword functionality
     */
    enum class XSFKeywordFlag(val value: Int) {
        NONE(0b00000000),
        SKIP(0b00000001),
        COLLECTION(0b00000010),
        TYPED(0b00000100)
    }

    /**
     * A map of kotlin types with differing XSF names
     */
    val keywordConversions: Map<String, String> = mapOf(
        "Map" to "NamedMap",
        "HashMap" to "NamedHMap"
    )

    /**
     * A map of .xsf keywords and their kotlin class equivalents
     */
    val keywords: Map<String, KClass<*>> = mapOf(
        "Object" to Object::class,
        "String" to String::class,
        "Bool" to Boolean::class,
        "Int" to Int::class,
        "Short" to Short::class,
        "Long" to Long::class,
        "Double" to Double::class,
        "Char" to Char::class,
        "NamedMap" to Map::class,
        "NamedHMap" to HashMap::class,
    )

    /**
     *  List of flags for each keyword, used for parsing
     */
    val keywordFlags: Map<String, Int> = mapOf(
        "Object" to (XSFKeywordFlag.SKIP.value),
        "String" to (XSFKeywordFlag.NONE.value),
        "Bool" to (XSFKeywordFlag.NONE.value),
        "Int" to (XSFKeywordFlag.NONE.value),
        "Short" to (XSFKeywordFlag.NONE.value),
        "Long" to (XSFKeywordFlag.NONE.value),
        "Double" to (XSFKeywordFlag.NONE.value),
        "Char" to (XSFKeywordFlag.NONE.value),
        "NamedMap" to (XSFKeywordFlag.COLLECTION.value or XSFKeywordFlag.TYPED.value),
        "NamedHMap" to (XSFKeywordFlag.COLLECTION.value or XSFKeywordFlag.TYPED.value),
        "Group" to (XSFKeywordFlag.COLLECTION.value or XSFKeywordFlag.TYPED.value)
    )

    /**
     * Internal Kotlin class members to avoid during parsing
     */
    val parseMask = arrayListOf(
        "equals",
        "hashCode",
        "toString"
    )

    /**
     * TODO : Document
     */
    val specialChars = arrayListOf(
        ":",
        "{",
        "}",
        ","
    )
}
