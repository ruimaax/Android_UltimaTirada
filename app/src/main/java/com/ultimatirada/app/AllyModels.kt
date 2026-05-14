package com.ultimatirada.app

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.random.Random

data class AllyMeResponse(
    val events: List<AllyEvent> = emptyList(),
    val tournaments: List<AllyTournament> = emptyList(),
)

data class AllyEvent(
    val id: String,
    val title: String,
    val isJoined: Boolean? = null,
    val tournaments: List<AllyTournament> = emptyList(),
)

data class AllyTournament(
    val id: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val totalGames: Int? = null,
    val name: String,
    val status: AllyTournamentStatus = AllyTournamentStatus.Waiting,
    val currentGame: AllyGame? = null,
    val games: List<AllyGame> = emptyList(),
    val pairings: List<AllyPairing> = emptyList(),
)

enum class AllyTournamentStatus { Waiting, Active, Finished }

data class AllyGame(
    val id: String,
    val number: Int,
    val total: Int? = null,
    val pairings: List<AllyPairing> = emptyList(),
)

data class AllyPairing(
    val id: String,
    val table: Int? = null,
    val gameNumber: Int? = null,
    val totalGames: Int? = null,
    val isBye: Boolean = false,
    val players: List<AllyPlayer> = emptyList(),
    val teamA: List<AllyPlayer> = emptyList(),
    val teamB: List<AllyPlayer> = emptyList(),
)

data class AllyPlayer(
    val id: String? = null,
    val userId: String? = null,
    val nick: String? = null,
    val name: String? = null,
) {
    val displayName: String get() = nick?.takeIf { it.isNotBlank() } ?: name?.takeIf { it.isNotBlank() } ?: "Jugador"
}

enum class AllyMode(val label: String, val teamASlots: Int, val teamBSlots: Int) {
    OneVsOne("1vs1", 1, 1),
    TwoVsOne("2vs1", 2, 1),
    TwoVsTwo("2vs2", 2, 2),
    ThreeVsThree("3vs3", 3, 3),
}

data class LifeCounterPlayer(
    val id: String,
    val name: String,
    val team: Int,
    val lives: Int,
    val dice: Int = 1,
    val isWinner: Boolean = false,
)

data class DiceRollResult(val winnerId: String, val rolls: Map<String, Int>)

data class AllyMatchSnapshot(
    val eventTitle: String,
    val tournament: AllyTournament,
    val game: AllyGame,
    val pairing: AllyPairing?,
) {
    val key: String get() = "${tournament.id}-${game.number}-${pairing?.id ?: "waiting"}"
    val isBye: Boolean get() = pairing?.isBye == true
}

fun parseAllyMeResponse(element: JsonElement): AllyMeResponse {
    if (element is JsonArray) {
        return AllyMeResponse(events = element.mapNotNull { it.asObjectOrNull()?.toAllyEvent() })
    }
    val objectValue = element.asObjectOrNull() ?: return AllyMeResponse()
    return AllyMeResponse(
        events = objectValue.array("events", "joinedEvents", "joined_events", "data").mapNotNull { it.asObjectOrNull()?.toAllyEvent() },
        tournaments = objectValue.array("tournaments", "allyTournaments", "ally_tournaments").mapNotNull { it.asObjectOrNull()?.toAllyTournament() },
    )
}

fun findAllyMatchForUser(response: AllyMeResponse?, user: ApiUser?): AllyMatchSnapshot? {
    if (response == null || user == null) return null
    val eventById = response.events.associateBy { it.id }
    val candidates = buildList {
        response.tournaments.forEach { add((eventById[it.eventId]?.title ?: it.eventTitle ?: it.name) to it) }
        response.events.forEach { event -> event.tournaments.forEach { add(event.title to it) } }
    }.filter { (_, tournament) -> tournament.status != AllyTournamentStatus.Finished }

    candidates.forEach { (eventTitle, tournament) ->
        val game = tournament.currentGame ?: tournament.games.firstOrNull() ?: AllyGame("1", 1, tournament.totalGames)
        val pairings = game.pairings.ifEmpty { tournament.pairings }
        val pairing = pairings.firstOrNull { it.matches(user) }
        if (pairing != null) return AllyMatchSnapshot(eventTitle, tournament, game, pairing)
        if (pairings.isEmpty()) return AllyMatchSnapshot(eventTitle, tournament, game, null)
    }
    return null
}

fun initialCountersFor(pairing: AllyPairing, mode: AllyMode, initialLives: Int): List<LifeCounterPlayer> {
    val teamA = pairing.teamA.ifEmpty { pairing.players.take(1) }
    val teamB = pairing.teamB.ifEmpty { pairing.players.drop(teamA.size).ifEmpty { pairing.players.drop(1) } }
    return buildList {
        repeat(mode.teamASlots) { index ->
            val player = teamA.getOrNull(index)
            add(
                LifeCounterPlayer(
                    id = "a-$index-${player?.userId ?: player?.id ?: index}",
                    name = player?.displayName ?: if (index == 0) "Equipo A" else "Compañero A${index + 1}",
                    team = 0,
                    lives = initialLives,
                ),
            )
        }
        repeat(mode.teamBSlots) { index ->
            val player = teamB.getOrNull(index)
            add(
                LifeCounterPlayer(
                    id = "b-$index-${player?.userId ?: player?.id ?: index}",
                    name = player?.displayName ?: if (index == 0) "Equipo B" else "Compañero B${index + 1}",
                    team = 1,
                    lives = initialLives,
                ),
            )
        }
    }
}

fun resolveDiceRoll(playerIds: List<String>): DiceRollResult {
    if (playerIds.isEmpty()) return DiceRollResult("", emptyMap())
    var rolls: Map<String, Int>
    var max: Int
    do {
        rolls = playerIds.associateWith { Random.nextInt(1, 7) }
        max = rolls.values.maxOrNull() ?: 0
    } while (rolls.values.count { it == max } > 1)
    val winner = rolls.maxBy { it.value }.key
    return DiceRollResult(winner, rolls)
}

private fun JsonObject.toAllyEvent(): AllyEvent {
    val tournaments = array("tournaments", "allyTournaments", "ally_tournaments")
        .mapNotNull { it.asObjectOrNull()?.toAllyTournament() }
        .toMutableList()
    firstObject("activeTournament", "active_tournament", "tournament")?.toAllyTournament()?.let { tournaments.add(0, it) }
    return AllyEvent(
        id = string("id", "eventId", "event_id") ?: "event-${hashCode()}",
        title = string("title", "name", "eventTitle", "event_title") ?: "Evento",
        isJoined = boolean("isJoined", "is_joined", "joined"),
        tournaments = tournaments,
    )
}

private fun JsonObject.toAllyTournament(): AllyTournament {
    val runtime = firstObject("state", "tournamentState", "tournament_state")
    val config = firstObject("config")
    val games = array("games", "rounds").mapNotNull { it.asObjectOrNull()?.toAllyGame() }
        .ifEmpty { runtime?.array("games", "rounds")?.mapNotNull { it.asObjectOrNull()?.toAllyGame() }.orEmpty() }
    val currentNumber = int("currentGame", "current_game")
        ?: runtime?.int("currentGame", "current_game")
        ?: 1
    val totalGames = config?.int("gamesCount", "games_count", "totalGames", "total_games")
        ?: int("totalGames", "total_games", "gamesCount", "games_count")
    val currentGame = firstObject("currentGame", "current_game", "round", "currentRound", "current_round")?.toAllyGame()
        ?: games.getOrNull(currentNumber - 1)
        ?: AllyGame(id = currentNumber.toString(), number = currentNumber, total = totalGames)
    val pairings = array("pairings", "currentPairings", "current_pairings", "matches").mapNotNull { it.asObjectOrNull()?.toAllyPairing() }
        .ifEmpty { currentGame.pairings }
    return AllyTournament(
        id = string("id", "tournamentId", "tournament_id") ?: "tournament-${hashCode()}",
        eventId = string("eventId", "event_id"),
        eventTitle = string("eventTitle", "event_title"),
        totalGames = totalGames,
        name = string("name", "title", "eventTitle", "event_title") ?: "Torneo",
        status = tournamentStatus(string("status") ?: runtime?.string("status")),
        currentGame = currentGame.copy(total = currentGame.total ?: totalGames),
        games = games,
        pairings = pairings,
    )
}

private fun JsonObject.toAllyGame(): AllyGame {
    val number = int("number", "round", "game", "currentGame", "current_game", "currentRound", "current_round") ?: 1
    return AllyGame(
        id = string("id", "gameId", "game_id", "roundId", "round_id") ?: number.toString(),
        number = number,
        total = int("total", "totalGames", "total_games", "rounds"),
        pairings = array("pairings", "matches").mapNotNull { it.asObjectOrNull()?.toAllyPairing() },
    )
}

private fun JsonObject.toAllyPairing(): AllyPairing {
    val playerA = firstObject("playerA", "player_a", "a")?.toAllyPlayer()
    val playerB = firstObject("playerB", "player_b", "b")?.toAllyPlayer()
    val teamA = array("teamA", "team_a", "playersA", "players_a").mapNotNull { it.asObjectOrNull()?.toAllyPlayer() }
        .ifEmpty { listOfNotNull(playerA) }
    val teamB = array("teamB", "team_b", "playersB", "players_b").mapNotNull { it.asObjectOrNull()?.toAllyPlayer() }
        .ifEmpty { listOfNotNull(playerB) }
    val players = array("players").mapNotNull { it.asObjectOrNull()?.toAllyPlayer() }.ifEmpty { teamA + teamB }
    return AllyPairing(
        id = string("id", "pairingId", "pairing_id") ?: players.joinToString("-") { it.displayName },
        table = int("table", "tableNumber", "table_number"),
        gameNumber = int("gameNumber", "game_number", "round", "currentGame", "current_game"),
        totalGames = int("totalGames", "total_games"),
        isBye = boolean("isBye", "is_bye", "bye") == true || teamB.isEmpty(),
        players = players,
        teamA = teamA,
        teamB = teamB,
    )
}

private fun JsonObject.toAllyPlayer(): AllyPlayer = AllyPlayer(
    id = string("id"),
    userId = string("userId", "user_id"),
    nick = string("nick", "nickname", "username"),
    name = string("name", "fullName", "full_name", "displayName", "display_name"),
)

private fun AllyPairing.matches(user: ApiUser): Boolean {
    val needles = setOf(user.id.toString(), user.nick.normalizedName(), user.name.normalizedName()).filter { it.isNotBlank() }
    return players.any { player ->
        listOfNotNull(player.userId, player.id).any { it == user.id.toString() } ||
            listOfNotNull(player.nick, player.name, player.displayName).map { it.normalizedName() }.any { it in needles }
    }
}

private fun tournamentStatus(raw: String?): AllyTournamentStatus = when (raw?.lowercase()) {
    "active", "started", "running", "in_progress", "inprogress" -> AllyTournamentStatus.Active
    "finished", "done", "closed", "completed" -> AllyTournamentStatus.Finished
    else -> AllyTournamentStatus.Waiting
}

private fun String.normalizedName(): String = trim().lowercase()

private fun JsonElement.asObjectOrNull(): JsonObject? = this as? JsonObject

private fun JsonObject.firstObject(vararg keys: String): JsonObject? = keys.firstNotNullOfOrNull { this[it] as? JsonObject }

private fun JsonObject.array(vararg keys: String): List<JsonElement> =
    keys.firstNotNullOfOrNull { key -> (this[key] as? JsonArray)?.jsonArray } ?: emptyList()

private fun JsonObject.string(vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
    val value = this[key] ?: return@firstNotNullOfOrNull null
    when (value) {
        is JsonPrimitive -> value.contentOrNull
        else -> null
    }
}

private fun JsonObject.int(vararg keys: String): Int? = keys.firstNotNullOfOrNull { key ->
    val value = this[key] ?: return@firstNotNullOfOrNull null
    when (value) {
        is JsonPrimitive -> value.intOrNull ?: value.contentOrNull?.toIntOrNull()
        else -> null
    }
}

private fun JsonObject.boolean(vararg keys: String): Boolean? = keys.firstNotNullOfOrNull { key ->
    val value = this[key] ?: return@firstNotNullOfOrNull null
    when (value) {
        is JsonPrimitive -> value.booleanOrNull ?: value.contentOrNull?.let { it == "1" || it.equals("true", true) }
        else -> null
    }
}
