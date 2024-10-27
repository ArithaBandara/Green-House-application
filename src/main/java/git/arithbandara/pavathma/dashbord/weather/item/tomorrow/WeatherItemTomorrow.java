package git.arithbandara.pavathma.dashbord.weather.item.tomorrow;

public class WeatherItemTomorrow {
    String dayPhrase;
    int dayIcon;

    public String getDayPhrase() {
        return dayPhrase;
    }

    public int getDayIcon() {
        return dayIcon;
    }

    public WeatherItemTomorrow(String dayPhrase, int dayIcon) {
        this.dayPhrase = dayPhrase;
        this.dayIcon = dayIcon;
    }



}
