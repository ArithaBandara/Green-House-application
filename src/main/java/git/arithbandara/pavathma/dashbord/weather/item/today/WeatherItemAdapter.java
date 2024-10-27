package git.arithbandara.pavathma.dashbord.weather.item.today;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import git.arithbandara.pavathma.R;

import java.util.List;

public class WeatherItemAdapter extends RecyclerView.Adapter<WeatherItemAdapter.ItemViewHolder> {

    private List<WeatherItem> itemList;

    public WeatherItemAdapter(List<WeatherItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        WeatherItem item = itemList.get(position);
        holder.temperatureTextView.setText(item.getTemperature());
        holder.weatherIcon.setImageResource(item.getWeatherIcon());
        holder.rainTextView.setText(item.getRain());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView temperatureTextView;
        TextView rainTextView;
        ImageView weatherIcon;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureTextView = itemView.findViewById(R.id.tempratureC);
            rainTextView = itemView.findViewById(R.id.rain);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }
}
