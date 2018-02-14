package us.ihmc.simon.util.logging

import java.io.PrintWriter
import java.io.StringWriter

fun getStackTrace(aThrowable: Throwable): String {
    val result = StringWriter()
    val printWriter = PrintWriter(result)
    aThrowable.printStackTrace(printWriter)
    return result.toString()
}

fun humanReadableByteSize(bytes: Long, si: Boolean): String {
    val unit = if (si) 1000 else 1024
    if (bytes < unit) return bytes.toString() + " B"
    val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
    val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
    return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
}

fun humanReadableByteSize(bytes: Long): String = humanReadableByteSize(bytes, false)