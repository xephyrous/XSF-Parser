# XSFParser Class

`utils.xsf.XSFParser<T>` is a class designed to parse `.xsf` (Xephyr Storage Format) files into a designated class structure. This parser supports different parsing error levels for different usages.

## Constructor

### `XSFParser(_class: T, inFile: File, parseLevel: XSFParseLevel = XSFParseLevel.STRICT)`

#### Parameters:
- **_class**: The class to parse into.
- **inFile**: The `.xsf` file from which to parse.
- **parseLevel**: The strictness level for error handling during parsing. Defaults to `XSFParseLevel.STRICT`.

---

## Public Methods

### `setParseCallbackFun(func: (String) -> Unit, parseLevel: XSFParseLevel)`
Sets the callback function to handle a specific level of parsing errors.

#### Parameters:
- **func**: The function to be set as the callback. Receives a `String` that describes the error.
- **parseLevel**: The error strictness level (`STRICT`, `WARNING`, or `LENIENT`).

### `parse(): Result<T>`
Parses the `.xsf` file into the class provided in the constructor. 

#### Returns:
- **Result<T>**: A `Result` that contains the parsed class instance, or an error if parsing failed.

#### Additional Notes:
- The parsing handles primitive types such as `String`, `Int`, `Boolean`, and also supports collection types.
- It performs a validity check on the file to ensure it is readable and that its extension is `.xsf`.
- Error handling depends on the `XSFParseLevel` set for the parser.

---
## Variable Mismatches and Error Handling

### Mismatching Variables

One of the key areas where errors can occur in the `XSFParser` is due to mismatched variables during the parsing process. The parser tries to map the variables declared in the `.xsf` file to the properties of the class provided in the constructor. Several conditions can lead to errors being thrown:

1. **Type Mismatch**:
   - If a variable in the `.xsf` file does not match the expected type in the target class, an error will be thrown. For example, if a field in the class expects an `Int`, but the `.xsf` file provides a `String`, the parser will fail.

2. **Missing Fields**:
   - If the `.xsf` file is missing a field that is required by the target class, an error will be thrown unless a default value is provided for that field in the class. This ensures that all required data is present in the file.
   - The `XSFParseLevel` can influence how missing fields are handled:
     - In `STRICT` mode, the parser throws an error immediately.
     - In `WARNING` or `LENIENT` modes, it may log a warning but continue parsing.

3. **Additional Fields**:
   - If the `.xsf` file contains fields that are not declared in the target class, the parser may throw an error in `STRICT` mode. However, in more lenient modes (`WARNING` or `LENIENT`), these extra fields may be ignored, or a warning could be logged.
   
4. **Reserved Keywords**:
   - Certain keywords are reserved and cannot be used as variable names in the `.xsf` file. If a reserved keyword is used as a variable name, the `reservedKeywordCheck` method throws an error.

5. **Collection Type Mismatch**:
   - If the `.xsf` file contains a collection (e.g., `List`, `Set`) that does not match the expected type in the target class (e.g., expecting a `List` but receiving a `Set`), the parser will throw an error. Collection groupings are stored in `storedGroups`, and any inconsistencies between the file and class definitions lead to parsing errors.

6. **Invalid Declarations or Syntax**:
   - Syntax errors within the `.xsf` file itself, such as malformed variable declarations or invalid groupings, can trigger parsing failures. The parser expects the file to follow a predefined structure, and any deviations from this structure will result in an error.
   - In some cases, errors may be more leniently handled depending on the `XSFParseLevel`—with `STRICT` mode throwing exceptions immediately, and `WARNING` or `LENIENT` modes potentially continuing the parsing process while logging warnings.

### Error Classes

- **InvalidXSFFile**:
  - This error is thrown when the `.xsf` file is found to be invalid, either due to syntax errors, unreadable content, or mismatches in variable types.
  
- **DecoratedWarning**:
  - This warning is thrown when non-strict parsing modes (`WARNING` or `LENIENT`) are used, and the parser encounters a non-critical error, such as an additional field or type mismatch that does not break the parsing process.
  
### How to Handle Mismatches

To minimize parsing errors, ensure that:
- The `.xsf` file strictly matches the structure and types of the target class.
- All necessary fields are present in the file, and their values are of the correct types.
- Reserved keywords are not used inappropriately within the file.
- Collection types (e.g., `List`, `Set`) are properly declared and aligned with the target class's expectations.

The `XSFParseLevel` can be adjusted to control the strictness of the parser and how it handles these mismatches, providing flexibility in handling various parsing scenarios.


## Example Usage

```kotlin
// Initialize the parser
val parser = XSFParser(MyClass(), File("data.xsf"))

// Set a callback for warnings
parser.setParseCallbackFun({ message -> println("Warning: $message") }, XSFParseLevel.WARNING)

// Parse the file
val result = parser.parse()

// Handle the result
result.onSuccess { parsedClass -> 
    // Do something with the parsed class
}.onFailure { error -> 
    println("Parsing failed: ${error.message}")
}
```

# XSFFile Class

The `XSFFile` class is responsible for managing `.xsf` files, which include saving, loading, and serializing class member variables to a custom file format. It supports a limited set of types and currently provides functionality for stringifying class members into a predefined format.

#### Functions:

1. **`loadClass(classObj: T)`**
   - This function loads and serializes the members of the provided class object (`classObj`) into the XSFFile, to be saved on the next call of (`save()`).
   - It supports certain types of variables (based on `_allowedClasses`), including `String`, `Char`, and primitive number types (`Int`, `Long`, etc.), along with `Map` and `HashMap`.
   
2. **`setPath(path: Path | path: String)`**
   - Sets the save path for the `.xsf` file.
   - The path is validated to ensure it is a directory and writable, if invalid, it throws an `IOException`.

3. **`getPath(): Path`**
   - Returns the current file save path.

4. **`save()`**
   - Writes the serialized contents (`_contents`) to the `.xsf` file at the specified path.
   - Throws an `IOException()` if there is an error during saving

---

#### Potential Errors:
- **Unsupported Types**: If a class object passed to `loadClass` contains a member of a type that is not supported by XSF, an `IOException` error is thrown.
- **Invalid Path**: When calling `setPath()`, if the directory does not exist or lacks write permissions, the class will throw an error. 
- **IOException**: Errors can occur during file creation or writing in `save()`.

---

## Error Classes

1. **`IOException`**:
   - This exception is thrown when there is an issue with file writing, such as attempting to write to an invalid or restricted path, or when there is an error creating the file at the target location.
