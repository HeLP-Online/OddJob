package no.helponline.Utils;

import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.Optional;

public class TMarker {
    public String label;
    public String world;
    public double x;
    public double y;
    public double z;
    public String iconName;
    public String description;

    public Optional<Marker> create(MarkerAPI markerApi, MarkerSet markerSet, String markerId) {
        Marker marker = markerSet.createMarker(markerId, label, world, x, y, z, markerApi.getMarkerIcon(iconName), false);
        if (marker == null) return Optional.empty();
        marker.setDescription(this.description);
        return Optional.of(marker);
    }

    public void update(MarkerAPI markerAPI, MarkerSet markerSet, Marker marker) {
        if (!this.equals(marker)) marker.setLocation(world, x, y, z);
        if (!marker.getLabel().equalsIgnoreCase(label)) marker.setLabel(label);
        if (!marker.getMarkerIcon().equals(markerAPI.getMarkerIcon(iconName)))
            marker.setMarkerIcon(markerAPI.getMarkerIcon(iconName));
        if (!marker.getDescription().equals(description)) marker.setDescription(description);
    }

    public boolean equals(Marker marker) {
        return marker.getWorld().equals(world) && marker.getX() == x && marker.getY() == y && marker.getZ() == z;
    }
}
