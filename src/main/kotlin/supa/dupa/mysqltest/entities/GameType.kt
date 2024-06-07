package supa.dupa.mysqltest.entities

enum class GameType(val typeName : String, val typeCode : Int) {
    CASUAL("캐주얼", 0),
    RANK("랭크", 1);

    companion object {
        fun typeCodeOf(typeCode : Int) = entries.find { it.typeCode == typeCode } ?: CASUAL
    }
}