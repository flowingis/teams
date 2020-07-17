package it.flowing.repository

import it.flowing.model.Surfer
import it.flowing.model.Team

class Teams {
    private val sheets = Sheets()
    private val surfers = Surfers();

    companion object {
        private const val SPREADSHEET_ID = "1E9YtPFw1VGnkOW3RllXe9Kvhcmgu1xmQU15Y8mSuy04"
        private const val RANGE = "Planning orizzonte!A4:C"
        private val BLOCKLIST = listOf(
            "Altro",
            "Governance",
            "Aggiornamenti WP",
            "Aggiorn. EZ Fiaso"
        )
    }

    private val isValidRow = { l: List<Any> -> l.size == 3 }

    private val getTeamNameFromRow = { l: List<Any> -> (l[0] as String).trim() }

    private val isValidTeamName = { teamName: String -> teamName.isNotEmpty() && !BLOCKLIST.contains(teamName) }

    private fun getPeopleForTeam(team: String, rows: List<List<Any>>): List<Surfer> {
        val surfersList = surfers.list();

        return rows
            .filter(isValidRow)
            .map { l -> Pair((l[0] as String).trim(), l[2] as String) }
            .filter { p -> p.first.equals(team) }
            .map { p -> surfersList.find { surfer -> surfer.nickname.toLowerCase().equals(p.second.toLowerCase()) } }
            .filterNotNull()
    }

    private val teamNameToTeam = { rows: List<List<Any>> ->
        { teamName: String ->
            Team(
                name = teamName,
                surfers = getPeopleForTeam(teamName, rows)
            )
        }
    }

    fun list(): List<Team> {
        val rows = sheets.getValues(
            SPREADSHEET_ID,
            RANGE
        )
        return rows
            .filter(isValidRow)
            .map(getTeamNameFromRow)
            .filter(isValidTeamName)
            .distinct()
            .map(teamNameToTeam(rows))
    }
}