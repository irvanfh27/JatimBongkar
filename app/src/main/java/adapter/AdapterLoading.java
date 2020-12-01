package adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jatim.bongkar.R;

import java.util.List;

import Model.HistoryData;


public class AdapterLoading extends RecyclerView.Adapter<AdapterLoading.HolderData> {
    private List<HistoryData> mitems;
    private Context context;

    public AdapterLoading(Context context, List<HistoryData> items){
        this.mitems=items;
        this.context=context;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,parent,false);
        HolderData holderData = new HolderData(layout);
        return holderData;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        HistoryData md = mitems.get(position);
        holder.listdata.setText(md.getNoDelivery()+"-"+md.getDriver()+"-"+md.getNoPolisi()+"-"+md.getSendWeight());
    }

    @Override
    public int getItemCount() {
        return mitems.size();
    }

    class HolderData extends RecyclerView.ViewHolder{
        TextView listdata;

        public HolderData(View view){
            super(view);
            listdata= view.findViewById(R.id.layoutrowtext);
        }
    }
}
