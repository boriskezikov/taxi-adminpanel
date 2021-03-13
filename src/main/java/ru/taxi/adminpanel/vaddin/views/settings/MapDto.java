package ru.taxi.adminpanel.vaddin.views.settings;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MapDto implements Serializable {

    private double lat;
    private double lng;
    private String fullAddress;
}
