# Xephyr Storage Format (XSF)

- XSF is a simple storage format offering typed declarations and inbuilt class parsing, validation, and creation.
- The XSF parser also offers customization of the parsing process though several parse levels and error callbacks.

## Usage
- To use XSF in your project, copy the contents of the folder titled as the language your project uses and copy the directory/contents into your project's directory.
- From there reference the mini-manual included alongside each distribution, marked manual.md

## Example File (Kotlin)
```kotlin
String name: "Dark Theme"

Long icon: 4286217861
Long header: 4281019696
Long button: 4286217861
Long border: 4280690734
Long fab: 4280690734

NamedMap textColors: Long {
    text1: 4290757063,
    text2: 4290757063,
    textGrey: 4290757063
}
```
- The file above gets parsed into the following pre-defined class object:
```kotlin
class ThemeData(private var name: String) {
    var icon: Long = 0
    var header: Long = 0
    var button: Long = 0
    var border: Long = 0
    var fab: Long = 0
    var card: Map<String, Long> = mapOf()
    var textColors: Map<String, Long> = mapOf()
}
```
