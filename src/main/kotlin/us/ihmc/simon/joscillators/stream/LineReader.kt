package us.ihmc.simon.stream

/**
 Copyright 2017 Florida Institute for Human & Machine Cognition (IHMC).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class LineReader(private val reader: BufferedReader, private val keepCarriageReturns: Boolean) : Iterable<String> {

    private val bufSize = 4 * 1024 * 1024 // 8 MB

    constructor(input: InputStream) : this(BufferedReader(InputStreamReader(input, "UTF-8")), false)

    /**
     * Reads the next line from the Reader.
     *
     * @return Line read from reader. It could be null if not more lines are found
     * @throws IOException On error from BufferedReader
     */
    @Throws(IOException::class)
    private fun readLine(): String? {
        return if (keepCarriageReturns) readUntilNewline() else reader.readLine()
    }

    @Throws(IOException::class)
    private fun readUntilNewline(): String? {
        val sb = StringBuilder(bufSize)
        var c = reader.read()
        while (c > -1 && c != '\n'.toInt()) {
            sb.append(c.toChar())
            c = reader.read()
        }

        return if (sb.isNotEmpty()) sb.toString() else null
    }

    override fun iterator(): Iterator<String> {

        return object : Iterator<String> {

            var next: String?
            init {
                next = readLine()
            }

            override fun hasNext(): Boolean = next != null

            @Synchronized
            override fun next(): String {
                val current = next
                if (current != null) next = reader.readLine()
                if (current == null) throw IndexOutOfBoundsException("Stream ended")
                return current
            }
        }
    }
}
