/**
 * Created by gbenincasa on 2/7/18.
 */

package us.ihmc.simon.util.time

import java.text.SimpleDateFormat
import java.time.Duration

fun zuluToMilliseconds(date: String): Long {
    // 2017-09-25T10:00:00Z
    val fmtTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return fmtTimestamp.parse(date).time
}

fun toDuration(startTimeInMillis: Long, endTimeInMillis: Long) : Duration {
    return Duration.ZERO.plusMillis(System.currentTimeMillis() - startTimeInMillis)
}

fun formattedDuration(startTimeInMillis: Long): String {
    val duration = toDuration(startTimeInMillis, System.currentTimeMillis())
    var time = ""
    if (duration.toHours() > 0) {
        time += duration.toHours().toString() + "h"
    }
    if (duration.toMinutes() > 0) {
        time += duration.toMinutes().toString() + "m"
    }
    if (duration.toMinutes() > 0) {
        time += (duration.toMillis()/1000).toString() + "s"
    }
    return time
}