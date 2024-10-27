package git.arithbandara.pavathma.dashbord.weather.item.tomorrow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import git.arithbandara.pavathma.R;

import java.util.List;

public class WeatherItemAdapterTomorrow extends RecyclerView.Adapter<WeatherItemAdapterTomorrow.ItemViewHolder>{

    private List<WeatherItemTomorrow> itemList;

    public WeatherItemAdapterTomorrow(List<WeatherItemTomorrow> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item_tomorrow, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        WeatherItemTomorrow item = itemList.get(position);
        holder.dayText.setText(item.getDayPhrase());

        holder.dayView.setImageResource(item.dayIcon);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        ImageView dayView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText=itemView.findViewById(R.id.day_phrase);
            dayView=itemView.findViewById(R.id.day_icon);

        }
    }

}



