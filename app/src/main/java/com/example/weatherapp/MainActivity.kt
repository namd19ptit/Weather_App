package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// 3de8cac6078d44bd4686022743bc95c5
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Ha Noi")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"3de8cac6078d44bd4686022743bc95c5","metric")

        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responeBody = response.body()
                if(response.isSuccessful && responeBody!=null){
                    val temperature = responeBody.main.temp.toString()
                    val humidity = responeBody.main.humidity
                    val windSpeed = responeBody.wind.speed
                    val sunRise = responeBody.sys.sunrise.toLong()
                    val sunSet = responeBody.sys.sunset.toLong()
                    val seaLevel = responeBody.main.pressure
                    val condition = responeBody.weather.firstOrNull()?.main?:"unknow"
                    val maxTemp= responeBody.main.temp_max
                    val minTemp=responeBody.main.temp_min
                    binding.temp.text="$temperature°C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max: $maxTemp°C"
                    binding.minTemp.text="Min: $minTemp°C"
                    binding.humidity.text="$humidity%"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunRise.text="${time(sunRise)}"
                    binding.sunSet.text="${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text="$condition"
                    binding.day.text= dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityName"

                    //Log.d("TAG","onresponse: $temperature")
                    changeImageAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })

      }

    private fun changeImageAccordingToWeatherCondition(condition:String) {
        when(condition){
            "Clear Sky","Sunny","Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
            }
            "Partly Clouds", "Clouds","Overcast","Mist","Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp:Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}
fun dayName(timestamp:Long):String{
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    return sdf.format((Date()))

}