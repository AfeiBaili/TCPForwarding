import Logger.info
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*


/**
 * 客户端处理
 *
 *@author AfeiBaili
 *@version 2026/1/28 16:35
 */

class NetClient(val client: Socket, val listenHost: String, val listenPort: Int) {
    var forwardingSize = 0
    val target = Socket(listenHost, listenPort)
    fun connect() {
        "${client.remoteSocketAddress}已连接".info(false)

        clients.add(client)
        if (verifyCommand(target)) return
        pipe(client.inputStream, target.outputStream)
        pipe(target.inputStream, client.outputStream)
    }

    fun pipe(input: InputStream, output: OutputStream) {
        serverScope.launch {
            runCatching {
                val bytes = ByteArray(1024 * 8)
                var len = -1
                while (input.read(bytes).also { len = it } != -1) {
                    forwardingSize += len
                    output.write(bytes, 0, len)
                    output.flush()
                }
            }.onFailure {
                runCatching {
                    client.close()
                    target.close()
                    clients.remove(client)
                    "断开连接：${client.remoteSocketAddress}。累计转发${forwardingSize}字节；".info(false)
                }.onFailure { exception ->
                    "关闭流时出现一个错误：${exception.message}".info(false)
                }
            }
        }
    }

    fun verifyCommand(target: Socket): Boolean {
        val verifyByte: ByteArray = "/command/count".toByteArray()
        val bytes = ByteArray(verifyByte.size)
        val length: Int = client.inputStream.read(bytes)

        if (bytes.contentEquals(verifyByte)) {
            clients.remove(client)
            client.outputStream.write(ByteArray(1) { clients.size.toByte() })
            client.close()
            target.close()
            return true
        }

        target.outputStream.write(bytes, 0, length)
        target.outputStream.flush()
        return false

    }

    companion object {
        val clients: MutableSet<Socket> = Collections.synchronizedSet(mutableSetOf<Socket>())
    }
}