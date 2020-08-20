package it.flowing.repository

import it.flowing.model.Surfer
import it.flowing.model.Team
import java.util.*

class Teams {
    private val sheets = Sheets()
    private val surfers = Surfers()
    private val SPREADSHEET_ID = "1E9YtPFw1VGnkOW3RllXe9Kvhcmgu1xmQU15Y8mSuy04"
    private val RANGE = "Planning orizzonte!A4:BF"
    private val BLOCKLIST = listOf(
        "Altro",
        "Governance",
        "Aggiornamenti WP",
        "Aggiorn. EZ Fiaso"
    )

    private val WEEK_CELL_OFFSET = 4;

    private fun isValidRow(l: List<Any>): Boolean {
        if (l.size < 3) {
            return false
        }

        if (l.getOrNull(0) == null) {
            return false
        }

        if (l.getOrNull(2) == null) {
            return false
        }

        return true
    }

    private val getTeamNameFromRow = { l: List<Any> -> (l[0] as String).trim() }

    private val isValidTeamName = { teamName: String -> teamName.isNotEmpty() && !BLOCKLIST.contains(teamName) }

    private fun getPeopleForTeam(team: String, rows: List<List<Any>>, surfersList: List<Surfer>): List<Surfer> {
        return rows
            .asSequence()
            .map { l -> Pair((l[0] as String).trim(), l[2] as String) }
            .filter { it.first == team }
            .mapNotNull { surfersList.find { surfer -> surfer.nickname.toLowerCase().equals(it.second.toLowerCase()) } }
            .sortedBy { it.name }
            .toList()
    }

    private fun getWeeklyTeamEffort(team: String, rows: List<List<Any>>, week: Int): Int {
        return rows
            .asSequence()
            .filter { it[0] == team }
            .mapNotNull { it.getOrElse(week + WEEK_CELL_OFFSET) { "" } }
            .map { it.toString() }
            .filterNot { it.isNullOrBlank() }
            .mapNotNull { it.toIntOrNull() }
            .sum()
    }

    private fun isTeamActive(team: String, rows: List<List<Any>>): Boolean {
        val week = Calendar.getInstance().run {
            get(Calendar.WEEK_OF_YEAR)
        }

        return (week - 2..week + 2)
            .map { getWeeklyTeamEffort(team, rows, it) }
            .sum() > 0
    }

    fun list(): List<Team> {
        val rows = sheets
            .getValues(
                SPREADSHEET_ID,
                RANGE
            )
            .filter { isValidRow(it) }

        val surfersList = surfers.list();

        return rows
            .asSequence()
            .map(getTeamNameFromRow)
            .filter(isValidTeamName)
            .distinct()
            .map { teamName ->
                Team(
                    name = teamName,
                    surfers = getPeopleForTeam(teamName, rows, surfersList),
                    active = isTeamActive(teamName, rows)
                )
            }
            .toList()
    }
}