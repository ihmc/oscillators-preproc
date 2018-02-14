package us.ihmc.simon

import org.apache.commons.csv.CSVFormat
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import us.ihmc.simon.joscillators.preproc.datasets.github.EventType
import us.ihmc.simon.joscillators.preproc.datasets.github.csvRecordToGitHubEvent
import us.ihmc.simon.joscillators.preproc.datasets.github.jsonToGitHubEvent
import us.ihmc.simon.joscillators.preproc.datasets.github.timeseries.chronologicalEventsByRepo
import us.ihmc.simon.joscillators.preproc.datasets.rosslerbaldu.timeseries.chronologicalEventsByOscillator
import us.ihmc.simon.stream.*
import us.ihmc.simon.util.logging.humanReadableByteSize
import us.ihmc.simon.util.time.formattedDuration
import us.ihmc.simon.util.time.toDuration

/**
 * Created by gbenincasa on 2/6/18.
 */

val datasets = arrayOf(
    "SIMONCore/input/2015-01-01-15.json",
    "SIMONCore/input/2015-01-01-16.json",
    "SIMONCore/input/2015-01-01-17.json",
    "SIMONCore/input/2017-09/training/2017-09-25-10.json",
    "SIMONCore/input/2017-09/training/2017-09-25-9.json",
    "SIMONCore/input/2017-09/test/2017-09-25-11.json"/*,
    "/home/gbenincasa/Downloads/TS_0.dat"*/
)

fun preprocessGitHub(file: File) {

    FileInputStream(file).use {stream ->

        val (reader, parser) = when(file.extension.toLowerCase()) {
            "csv"  -> Pair(CSVLineReader(stream), csvRecordToGitHubEvent)
            "json" -> Pair(JsonDocumentsStream(stream), jsonToGitHubEvent)
            else -> throw RuntimeException("File type not supported: " + file.name)
        }

        val eventStream = reader.asSequence()
                .map { e -> parser(e) }
                .filter { e ->
                    when (e.type) {
                        EventType.PushEvent, EventType.ReleaseEvent -> true
                        else -> false
                    }
                }

        val byRepo = chronologicalEventsByRepo(eventStream)

        byRepo.entries.stream().forEach({(k, v) -> println("$k -> $v")})
    }
}

fun preprocess(file: File) {

    FileInputStream(file).use { stream ->
        val eventStream = CSVDoubleCellReader(stream, CSVFormat.TDF).asSequence()
        val byOscillator = chronologicalEventsByOscillator(eventStream)
        byOscillator.entries.stream().forEach({(k, v) -> println("$k -> $v")})
    }
}

fun main(args : Array<String>) {

    var totalSize = BigInteger("0")
    val startTime = System.currentTimeMillis()
    for(dataset in datasets) {
        val file = File(dataset)
        totalSize = totalSize.add(BigInteger.valueOf(file.length()))
        preprocessGitHub(file)
    }

    println("All files parsed in " + formattedDuration(startTime) + "s for a total of " +
            humanReadableByteSize(totalSize.toLong()))
    System.exit(0)
}