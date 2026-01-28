import Logger.info
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket


/**
 * 客户端处理
 *
 *@author AfeiBaili
 *@version 2026/1/28 16:35
 */

class NetClient(val listenHost: String, val listenPort: Int) {
    fun handle(client: Socket) {
        "${client.remoteSocketAddress}已连接".info(false)
        val target = Socket()
        target.connect(InetSocketAddress(listenHost, listenPort))

        pipe(client.inputStream, target.outputStream)
        pipe(target.inputStream, client.outputStream)
    }

    fun pipe(input: InputStream, output: OutputStream) {
        serverScope.launch {
            runCatching {
                val bytes = ByteArray(1024 * 8)
                var len = -1
                while (input.read(bytes).also { len = it } != -1) {
                    output.write(bytes, 0, len)
                    output.flush()
                }
            }.onFailure {
                input.close()
                output.close()
            }
        }
    }
}