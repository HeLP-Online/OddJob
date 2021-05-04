package com.spillhuset.Utils;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.Optional;

public class TAreaMarker {
    private String label;
    private String world;
    private double[] x;
    private double[] z;
    private String description;
    private int lineWeight;
    private double lineOpacity;
    private int lineColor;
    private double fillOpacity;
    private int fillColor;

    public Optional<AreaMarker> create(MarkerAPI markerAPI, MarkerSet markerSet, String markerId) {
        AreaMarker marker = markerSet.createAreaMarker(markerId, label, false, world, x, z, false);
        if (marker == null) return Optional.empty();
        marker.setDescription(description);
        marker.setLineStyle(lineWeight, lineOpacity, lineColor);
        marker.setFillStyle(fillOpacity, fillColor);
        return Optional.of(marker);
    }

    public void update(MarkerAPI markerAPI, MarkerSet markerSet, AreaMarker marker) {
        marker.setCornerLocations(x, z);
        marker.setDescription(description);

        Integer lineWeight = marker.getLineWeight();
        Double lineOpacity = marker.getLineOpacity();
        Integer lineColor = marker.getLineColor();

        if (!lineWeight.equals(this.lineWeight) || !lineOpacity.equals(this.lineOpacity) || !lineColor.equals(this.lineColor)) {
            marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        }

        Double fillOpacity = marker.getFillOpacity();
        Integer fillColor = marker.getFillColor();

        if (!fillOpacity.equals(this.fillOpacity) || !fillColor.equals(this.fillColor)) {
            marker.setFillStyle(this.fillOpacity, this.fillColor);
        }
    }
}
