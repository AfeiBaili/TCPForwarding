import Logger.info

/**
 * 解析参数
 *
 *@author AfeiBaili
 *@version 2026/1/28 15:59
 */

object ParamParser {
    fun parse(params: Array<String>, nameList: List<ParamName>): Map<String, Any>? {
        if (params.isEmpty()) {
            return null.also {
                buildString {
                    appendLine()
                    appendLine("可用参数，值请用等号（=）分割：")
                    nameList.forEach { appendLine("\t$it\t${it.description}") }
                }.info(true)
            }
        }
        val mutableMapOf = mutableMapOf<String, Any>()
        val paramNameSet = nameList.map { it.name }.toSet()

        params.forEach { param ->
            val split: List<String> = param.split('=')
            if (split.size != 2) return null.also { "无法解析参数${param}".info(true) }
            if (split[0] in paramNameSet) mutableMapOf[split[0]] = split[1]
        }

        return mutableMapOf
    }
}