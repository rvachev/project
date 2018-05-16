package com.example.romanpc.rosyama;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterItemImpl implements ClusterItem {
    private final LatLng mPosition;
    private String pitId;

    public ClusterItemImpl(String pitid, double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        this.pitId = pitid;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getPitId(){
        return pitId;
    }
}
