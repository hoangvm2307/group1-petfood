package com.example.group1_petfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.StoreLocation;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<StoreLocation> locationList;
    private Context context;
    private LocationClickListener clickListener;

    public interface LocationClickListener {
        void onLocationClicked(StoreLocation location);
    }

    public LocationAdapter(List<StoreLocation> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    public LocationAdapter(List<StoreLocation> locationList, Context context, LocationClickListener clickListener) {
        this.locationList = locationList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_improved, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        StoreLocation location = locationList.get(position);
        holder.storeName.setText(location.getName());
        holder.storeAddress.setText(location.getAddress());

        // Open location in Google Maps on navigation button click
        holder.navigateButton.setOnClickListener(v -> openInMaps(location));

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onLocationClicked(location);
            } else {
                openInMaps(location);
            }
        });
    }

    private void openInMaps(StoreLocation location) {
        // Combine store name and address for more accurate search
        String query = Uri.encode(location.getName() + ", " + location.getAddress());
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public void updateList(List<StoreLocation> filteredList) {
        locationList = filteredList;
        notifyDataSetChanged();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        TextView storeAddress;
        ImageButton navigateButton;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.textStoreName);
            storeAddress = itemView.findViewById(R.id.textStoreAddress);
            navigateButton = itemView.findViewById(R.id.btnNavigate);
        }
    }
}