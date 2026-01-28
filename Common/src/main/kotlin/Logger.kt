import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss.SSS")

    fun String.info(sync: Boolean = true) {
        if (sync)
            printSync("I", this)
        else
            print("I", this)
    }

    fun String.warn(sync: Boolean = true) {
        print("W", this)
        if (sync)
            printSync("W", this)
        else
            print("W", this)

    }

    fun String.error(sync: Boolean = true) {
        if (sync)
            printSync("E", this)
        else
            print("E", this)
    }

    private fun printSync(level: String, message: Any) {
        println("${getDateTime()} [$level]: $message")
    }

    private fun print(level: String, message: Any) = scope.launch {
        println("${getDateTime()} [$level]: $message")
    }

    private fun getDateTime() = LocalDateTime.now().format(timeFormatter)
}