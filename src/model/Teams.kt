package it.flowing.model

import model.Sheets

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
            .filter { l -> l[0].equals(team) }
            .map { l -> l[2] as String }
    }

    fun list(): List<Team> {
        val values = sheets.getValues(spreadsheetId, range)
        return values
            .filter { l -> l.size == 3 }
            .map { l -> l[0] as String }
            .filter { teamName -> teamName.isNotEmpty() }
            .filter { teamName -> !BLACKLIST.contains(teamName) }
            .distinct()
            .map { team -> Team(name = team as String, people = getPeople(team, values)) }

        //.map { l -> Team(name = l[0] as String, people = listOf(l[2] as String)) }
        //return map
    }
}