import Logger.error
import Logger.info
import kotlinx.coroutines.*
import java.net.ServerSocket
import java.net.Socket

fun main(args: Array<String>) {
    val params: Map<String, Any>? = ParamParser.parse(args, paramNameList)
    params ?: return

    init()

    val port: Int = runCatching { params["-p"]!!.toString().toInt() }.getOrElse {
        runCatching {
            params["--open-port"]!!.toString().toInt()
        }.getOrElse {
            "未指定开放端口（--open-port=33393 或 -p=33393）".error()
            return
        }
    }

    val targetPort: Int = runCatching { params["-t"]!!.toString().toInt() }.getOrElse {
        runCatching {
            params["--target-port"]!!.toString().toInt()
        }.getOrElse {
            "未指定目标端口（--target-port=33394 或 -t=33394）".error()
            return
        }
    }

    val host: String = runCatching { params["-h"]!!.toString() }.getOrElse {
        runCatching {
            params["--host"]!!.toString()
        }.getOrElse {
            "未指定主机地址（--host=127.0.0.1 或 -h=127.0.0.1）".info()
            return
        }
    }

    val serverSocket = ServerSocket(port)
    serverSocket.soTimeout = 10000
    "转发已开启在${port}端口".info(false)
    while (serverScope.isActive) {
        val client: Socket? = runCatching {
            serverSocket.accept()
        }.getOrNull()
        if (client == null) continue
        serverScope.launch {
            runCatching {
                NetClient(client,host, targetPort).connect()
            }.onFailure { exception ->
                "网络错误: ${exception.message}".error()
            }
        }
    }
}

val serverScope = CoroutineScope(Dispatchers.Default)

val paramNameList = listOf(
    ParamName("-p", "开放端口"),
    ParamName("-t", "目标端口"),
    ParamName("-h", "监听地址"),
    ParamName("--open-port", "开放端口"),
    ParamName("--listen-host", "监听地址"),
    ParamName("--listen-port", "目标端口"),
)

fun init() {
    Runtime.getRuntime().addShutdownHook(Thread {
        serverScope.cancel()
        "端口转发已关闭".info()
    })
}