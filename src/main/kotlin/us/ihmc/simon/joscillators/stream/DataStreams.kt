package us.ihmc.simon.stream

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * Created by gbenincasa on 2/7/18.
 */

abstract class Streamable<E> : Iterable<E> {
    fun stream(): Stream<E> = StreamSupport.stream(spliterator(), false)
    fun parallelStream(): Stream<E> = StreamSupport.stream(spliterator(), true)
}

/**
 * Parse UTF8 stream line by line
 */
class UTF8LineReader(input: InputStream): Streamable<String>() {

    private val keepCarriageReturns = false
    private val lineIter: Iterator<String?> = LineReader(
            BufferedReader(InputStreamReader(input, "UTF-8")),
            keepCarriageReturns).iterator()

    override fun iterator(): Iterator<String> {
        return object : Iterator<String> {
            override fun hasNext(): Boolean = lineIter.hasNext()

            override fun next(): String = lineIter.next() ?: throw IndexOutOfBoundsException("Stream ended")
        }
    }
}

/**
 * Parse CVS stream.  Iterable bottom up
 */
class CSVLineReader(input: InputStream, format: CSVFormat) : Streamable<Map<String, String>>() {

    private val records: Iterator<CSVRecord> = format
            .parse(InputStreamReader(input))
            .iterator()

    constructor(input: InputStream) : this(input,
            CSVFormat.DEFAULT.withQuote(null).withHeader())

    /**
     * Return a line of the CSV stream represented as a map.  The key is the column name
     */
    override fun iterator(): Iterator<Map<String, String>> {
        return object : Iterator<Map<String, String>> {
            override fun hasNext(): Boolean = records.hasNext()

            override fun next(): Map<String, String> = records.next().toMap()
        }
    }
}

/**
 * Reads a CSV stream containing doubles in each cell.  Iterable from bottom up, left to right.
 */
class CSVDoubleCellReader(input: InputStream, format: CSVFormat) : Streamable<Pair<Byte, Double> >() {

    private val lines: Iterator<CSVRecord> = format
            .parse(InputStreamReader(input))
            .iterator()

    private var columns: Iterator<String>? = null
    private var currentColumn: Byte = 0

    constructor(input: InputStream) : this(input, CSVFormat.TDF.withQuote(null))

    override fun iterator(): Iterator<Pair<Byte, Double>> {

        return object : Iterator<Pair<Byte, Double>> {

            @Synchronized
            override fun hasNext(): Boolean {
                val localColumns = columns
                return !(!lines.hasNext() && (localColumns == null || (!localColumns.hasNext())))
            }

            /**
             * Returns a pair where the first element is the column number, while the second element the value of the
             * cell
             */
            @Synchronized
            override fun next(): Pair<Byte, Double> {
                var localColumns = columns
                if ((localColumns == null) || (!localColumns.hasNext())) {
                    if (lines.hasNext()) {
                        localColumns = lines.next().iterator()
                        columns = localColumns
                        currentColumn = 0
                        return Pair(currentColumn, localColumns.next().toDouble())
                    }
                    else {
                        throw IndexOutOfBoundsException("Stream ended")
                    }
                }
                return Pair(++currentColumn, localColumns.next().toDouble())
            }
        }
    }
}

/**
 * Parse a stream containing a Json document at each line
 */
class JsonDocumentsStream(input: InputStream) : Streamable<JsonNode>() {

    private val keepCarriageReturns = false
    private val mapper = ObjectMapper()
    private val lineIter: Iterator<String?> = LineReader(
            BufferedReader(InputStreamReader(input, "UTF-8")),
            keepCarriageReturns).iterator()

    override fun iterator(): Iterator<JsonNode> {
        return object : Iterator<JsonNode> {
            override fun hasNext(): Boolean = lineIter.hasNext()

            override fun next(): JsonNode {
                val line = lineIter.next() ?: throw IndexOutOfBoundsException("Stream ended")
                return mapper.readTree(line)
            }
        }
    }
}
