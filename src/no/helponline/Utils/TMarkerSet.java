package no.helponline.Utils;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.Optional;

public class TMarkerSet {
    String label;

    public Optional<MarkerSet> create(MarkerAPI markerApi, String id) {
        MarkerSet marker = markerApi.createMarkerSet(id, label, null, false);
        if (marker == null) return Optional.empty();
        return Optional.of(marker);
    }

    public void update(MarkerAPI markerAPI, MarkerSet markerSet) {
        if (!markerSet.getMarkerSetLabel().equals(label)) markerSet.setMarkerSetLabel(label);
    }
}
