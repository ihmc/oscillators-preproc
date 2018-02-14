package us.ihmc.simon.joscillators.preproc.datasets.github

/**
 * Created by gbenincasa on 2/7/18.
 */

enum class EventType {
    Empty,

    WatchEvent,
    ForkEvent,
    PushEvent,
    PullRequestEvent,
    IssuesEvent,
    CreateEvent,
    DeleteEvent,

    IssueCommentEvent,
    PullRequestReviewCommentEvent,
    GollumEvent,
    ReleaseEvent,
    MemberEvent,
    CommitCommentEvent,

    PublicEvent
}

data class Entity(val name: String, val id: Long) : Comparable<Entity> {

    constructor() : this("", Long.MIN_VALUE)
    constructor(name: String) : this(name, Long.MIN_VALUE)

    fun isUndefined(): Boolean {
        return (name == "" && id == Long.MIN_VALUE)
    }

    override fun compareTo(other: Entity): Int {
        if (other is Entity) {
            if (id < other.id) return -1
            if (id == other.id) return 0
            return 1
        } else return -1
    }
}

data class Event(val type: EventType, val actor: Entity, val repo: Entity, val timestamp: Long) : Comparable<Event> {

    constructor() : this (EventType.Empty, Entity(), Entity(), Long.MIN_VALUE)
    constructor(type: EventType) : this (type, Entity(), Entity(), Long.MIN_VALUE)

    override fun compareTo(other: Event): Int {
        if (other is Event) {
            if (timestamp < other.timestamp) return -1
            if (timestamp == other.timestamp) return 0
            return 1
        } else return -1
    }

    fun timestampByDay(): Long = timestamp / (24 * 1000 * 60 * 60)
}