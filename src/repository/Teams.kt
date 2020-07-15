package it.flowing.repository

import it.flowing.model.Team

class Teams {
    private val sheets = Sheets()

    companion object {
        private const val spreadsheetId = "1E9YtPFw1VGnkOW3RllXe9Kvhcmgu1xmQU15Y8mSuy04"
        private const val range = "Planning orizzonte!A4:C"
        private val BLACKLIST = listOf("Altro", "Governance")
    }

    private fun getPeople(team: String, values: List<List<Any>>): List<String> {
        return values
            .filter { l -> l.size == 3 }
            .map { l -> Pair(l[0] as String, l[2] as String) }
            .filter { p -> p.first.equals(team) }
            .map { p -> p.second }
    }

    fun list(): List<Team> {
        val values = sheets.getValues(
            spreadsheetId,
            range
        )
        return values
            .filter { l -> l.size == 3 }
            .map { l -> l[0] as String }
            .filter { teamName -> teamName.isNotEmpty() }
            .filter { teamName -> !BLACKLIST.contains(teamName) }
            .distinct()
            .map { team ->
                Team(
                    name = team,
                    people = getPeople(team, values)
                )
            }
    }
}