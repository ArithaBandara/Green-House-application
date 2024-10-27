package git.arithbandara.pavathma.dashbord.weather.item.today;

public class WeatherItem {

    private String temperature;
    private int weatherIcon;
    private String rain;

    public WeatherItem(String temperature, int weatherIcon, String rain) {
        this.temperature = temperature;
        this.weatherIcon = weatherIcon;
        this.rain = rain;
    }

    public String getTemperature() {
        return temperature;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public String getRain() {
        return rain;
    }
}
