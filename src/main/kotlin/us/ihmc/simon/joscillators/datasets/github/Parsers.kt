package us.ihmc.simon.joscillators.preproc.datasets.github

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import us.ihmc.simon.util.time.zuluToMilliseconds

/**
 * Created by gbenincasa on 2/7/18.
 */

val csvRecordToGitHubEvent: (Map<String, String>) -> Event =  { token ->
    var creation = token.get("creation_date")
    if (creation == null) {
        creation = "0"
    }
    var name = token.get("repo_name") as String
    Event(EventType.PushEvent, Entity(), Entity(name), creation.toLong())
}

val jsonToGitHubEvent: (JsonNode) -> Event = { token ->
    // Get type and timestamp
    val type = EventType.valueOf(token.get("type").asText())
    val timestamp = zuluToMilliseconds(token.get("created_at").asText())

    // Get the Actor details
    val a = token.get("actor")
    val actor: Entity = if (a == null) {
        Entity()
    } else {
        val id = a.get("id").asLong()
        val name = a.get("login").asText()
        Entity(name, id)
    }

    // Get the Repo details
    val r = token.get("repo")
    val repo: Entity = if (a == null) {
        Entity()
    } else {
        val id = r.get("id").asLong()
        val name = r.get("name").asText()
        Entity(name, id)
    }

    Event(type, actor, repo, timestamp)
}

val stringToGitHubEvent: (String) -> Event = { token ->
    jsonToGitHubEvent(ObjectMapper().readTree(token))
}

val stringToGitHubEvent2: (Any) -> Event = { token ->
    Event()
}