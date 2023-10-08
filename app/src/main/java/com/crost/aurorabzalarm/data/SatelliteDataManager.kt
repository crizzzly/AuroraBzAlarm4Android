package com.crost.aurorabzalarm.data

class SatelliteDataManager(
    valuesCount: Int,
    private val url: String,
    private val valueNames: List<String>,
    private val importantValues: Map<String, String>,
) {
    /*  * fun parseSatelliteData:
        * gets Table with Values as String from NASA Websites via SatelliteDataDownloader
        * private fun mapValues
        * mapps values to valueNames, converts importantValues to Float
        * */
    private var valuesTable = mutableListOf<MutableMap<String, String>>()

    private val sdd = SatelliteDataDownloader(valuesCount)
    var value: Float = 0.0f

    fun getDataTable(): Boolean {
        val dataRows = sdd.getLatestDataTable(url)
        return if (dataRows.isNotEmpty()) {
            dataRows.forEach { row ->
                val mappedValues = mapValues(row)
                valuesTable.add(mappedValues)
            }
            true
        } else {
            false
        }
    }

    private fun mapValues(
         stringVals: List<String>,
    ): MutableMap<String, String> {
        val mappedValues = mutableMapOf<String, String>()

        var index = 0
        for (value in stringVals) {
            if (value.isNotEmpty()) {
                mappedValues[valueNames[index]] = value
                index += 1
            }
        }

        for (v in importantValues){
//            Log.d("saveMappedAceValues", "${v.key}: ${mappedValues[v.key].toString()} ${v.value}")
            value = mappedValues[v.key]?.toFloat()!!
        }
        return mappedValues
    }
}
