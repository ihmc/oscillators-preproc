package us.ihmc.simon.joscillators.preproc.datasets.github.timeseries

import us.ihmc.simon.joscillators.preproc.datasets.github.Entity
import us.ihmc.simon.joscillators.preproc.datasets.github.Event
import java.util.Comparator.comparing
import java.util.stream.Collectors.*
import java.util.stream.Stream


/**
 * Created by gbenincasa on 2/8/18.
 */

fun chronologicalEventsByRepo(events: Sequence<Event>): Map<Entity, List<Long>> = events
        .sortedWith(compareBy({it.repo}, {it.timestamp}))
        .groupBy({it.repo}, {e -> e.timestamp})

fun chronologicalEventsByRepo(events: Stream<Event>): Map<Entity, List<Long>> = events
        .sorted(comparing(Event::repo).thenComparing(Event::timestamp))
        .collect(groupingBy(Event::repo, mapping(Event::timestamp, toList<Long>())))

fun chronologicalEventsByUser(events: Sequence<Event>) = events
        .sortedWith(compareBy({it.actor}, {it.timestamp}))
        .groupBy({it.actor}, {e -> e.timestamp})

fun chronologicalEventsByUser(events: Stream<Event>): Map<Entity, List<Long>> = events
        .sorted(comparing(Event::actor).thenComparing(Event::timestamp))
        .collect(groupingBy(Event::actor, mapping(Event::timestamp, toList<Long>())))
