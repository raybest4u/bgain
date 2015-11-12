package elf.com.bagain.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import elf.com.bagain.R;
import elf.com.bagain.data.Source;

/**
 * User：McCluskey Ray on 2015/11/10 15:39
 * email：lr8734@126.com
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>{
    private final List<Source> filters;
    private final Context context;
    public FilterAdapter(@NonNull Context context,
                         @NonNull List<Source> filters) {
        this.context = context.getApplicationContext();
        this.filters = filters;
        setHasStableIds(true);
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public List<Source> getFilters() {
        return filters;
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {

        public TextView filterName;
        public ImageView filterIcon;
        public boolean isSwipeable;

        public FilterViewHolder(View itemView) {
            super(itemView);
            filterName = (TextView) itemView.findViewById(R.id.filter_name);
            filterIcon = (ImageView) itemView.findViewById(R.id.filter_icon);
        }

        public void highlightFilter() {
            itemView.setHasTransientState(true);
        }
    }
}
