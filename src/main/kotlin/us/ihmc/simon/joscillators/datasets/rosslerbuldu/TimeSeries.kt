package us.ihmc.simon.joscillators.preproc.datasets.rosslerbaldu.timeseries

/**
 * Created by gbenincasa on 2/9/18.
 */

fun chronologicalEventsByOscillator(events: Sequence<Pair<Byte, Double>>) = events
        .groupBy({it.first}, {it.second})