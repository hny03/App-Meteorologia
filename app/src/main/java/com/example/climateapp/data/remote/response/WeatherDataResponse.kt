package com.example.climateapp.data.remote.response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDataResponse(
    @SerialName("data")
    val data: List<WeatherData>,

//    @SerialName("minutely")
//    val minutely: List<MinutelyData> = emptyList(), // substitua pela estrutura real se precisar

    @SerialName("count")
    val count: Int
)

@Serializable
data class WeatherData(
    @SerialName("wind_cdir") val windCdir: String,
    @SerialName("rh") val relativeHumidity: Int,
    @SerialName("pod") val pod: String,
    @SerialName("lon") val longitude: Double,
    @SerialName("pres") val pressure: Double,
    @SerialName("timezone") val timezone: String,
    @SerialName("ob_time") val observationTime: String,
    @SerialName("country_code") val countryCode: String,
    @SerialName("clouds") val clouds: Int,
    @SerialName("vis") val visibility: Int,
    @SerialName("wind_spd") val windSpeed: Double,
    @SerialName("gust") val gust: Double,
    @SerialName("wind_cdir_full") val windCdirFull: String,
    @SerialName("app_temp") val apparentTemperature: Double,
    @SerialName("state_code") val stateCode: String,
    @SerialName("ts") val timestamp: Long,
    @SerialName("h_angle") val hourAngle: Int?,
    @SerialName("dewpt") val dewPoint: Double,
    @SerialName("weather") val weather: WeatherInfo,
    @SerialName("uv") val uvIndex: Int,
    @SerialName("aqi") val airQualityIndex: Int,
    @SerialName("station") val station: String?,
    @SerialName("sources") val sources: List<String>,
    @SerialName("wind_dir") val windDirection: Int,
    @SerialName("elev_angle") val elevationAngle: Double,
    @SerialName("datetime") val datetime: String?,
    @SerialName("precip") val precipitation: Double,
    @SerialName("ghi") val globalHorizontalIrradiance: Double,
    @SerialName("dni") val directNormalIrradiance: Double,
    @SerialName("dhi") val diffuseHorizontalIrradiance: Double,
    @SerialName("solar_rad") val solarRadiation: Double,
    @SerialName("city_name") val cityName: String,
    @SerialName("sunrise") val sunrise: String,
    @SerialName("sunset") val sunset: String,
    @SerialName("temp") val temperature: Double,
    @SerialName("lat") val latitude: Double,
    @SerialName("slp") val seaLevelPressure: Double
)

@Serializable
data class WeatherInfo(
    @SerialName("icon") val icon: String,
    @SerialName("code") val code: Int,
    @SerialName("description") val description: String
)
//
//@Serializable
//data class MinutelyData(
//    // Substitua pelos campos reais da propriedade "minutely" quando disponíveis
//)
