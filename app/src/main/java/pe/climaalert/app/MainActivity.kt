package pe.climaalert.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private lateinit var weatherText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 48, 40, 40)
            setBackgroundColor(Color.rgb(239, 247, 255))
        }
        scroll.addView(root)

        val title = TextView(this).apply {
            text = "CLIMA ALERT"
            textSize = 30f
            setTextColor(Color.rgb(15, 67, 120))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        root.addView(title, matchWrap())

        val subtitle = TextView(this).apply {
            text = "Información del clima y prevención"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setPadding(0, 12, 0, 28)
        }
        root.addView(subtitle, matchWrap())

        val weatherTitle = TextView(this).apply {
            text = "Clima actual - Lima"
            textSize = 21f
            setTextColor(Color.rgb(15, 67, 120))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        root.addView(weatherTitle, matchWrap())

        weatherText = TextView(this).apply {
            text = "Presiona «Actualizar clima» para consultar los datos."
            textSize = 18f
            setTextColor(Color.DKGRAY)
            setPadding(0, 16, 0, 20)
        }
        root.addView(weatherText, matchWrap())

        val refresh = Button(this).apply {
            text = "Actualizar clima"
            setOnClickListener { loadWeather() }
        }
        root.addView(refresh, matchWrap())

        val alertTitle = TextView(this).apply {
            text = "Recomendaciones de prevención"
            textSize = 21f
            setTextColor(Color.rgb(15, 67, 120))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 30, 0, 12)
        }
        root.addView(alertTitle, matchWrap())

        val tips = TextView(this).apply {
            text = "• Mantente informado por canales oficiales.\n\n" +
                "• Evita cruzar calles o quebradas inundadas.\n\n" +
                "• Prepara una mochila de emergencia.\n\n" +
                "• Si hay lluvias intensas, aléjate de laderas inestables.\n\n" +
                "• Sigue las indicaciones de las autoridades."
            textSize = 16f
            setTextColor(Color.DKGRAY)
        }
        root.addView(tips, matchWrap())

        statusText = TextView(this).apply {
            text = "Prototipo académico: consulta el clima mediante Open-Meteo."
            textSize = 12f
            setTextColor(Color.GRAY)
            setPadding(0, 28, 0, 0)
        }
        root.addView(statusText, matchWrap())

        setContentView(scroll)
        loadWeather()
    }

    private fun matchWrap(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun loadWeather() {
        weatherText.text = "Consultando información del clima..."
        thread {
            try {
                val url = URL("https://api.open-meteo.com/v1/forecast?latitude=-12.0464&longitude=-77.0428&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&timezone=America%2FLima")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                }
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                val current = JSONObject(response).getJSONObject("current")
                val temperature = current.getDouble("temperature_2m")
                val humidity = current.getInt("relative_humidity_2m")
                val rain = current.getDouble("precipitation")
                val wind = current.getDouble("wind_speed_10m")
                val result = "Temperatura: $temperature °C\n" +
                    "Humedad: $humidity %\n" +
                    "Precipitación: $rain mm\n" +
                    "Viento: $wind km/h"
                runOnUiThread {
                    weatherText.text = result
                    statusText.text = "Datos meteorológicos consultados mediante Open-Meteo. " +
                        "Para emergencias, verifica los avisos oficiales de las autoridades."
                }
            } catch (e: Exception) {
                runOnUiThread {
                    weatherText.text = "No se pudo consultar el clima. Revisa tu conexión y vuelve a intentarlo."
                }
            }
        }
    }
}
