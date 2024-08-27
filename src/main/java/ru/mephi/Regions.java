package ru.mephi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Regions {
    private Map<String, List<String>> regions;

    public Regions() {
        regions = new HashMap<>();
    }

    public void addCountry(String region, String country) {
        if (!regions.containsKey(region)) {
            List<String> countryList = new ArrayList<>();
            countryList.add(country);
            regions.put(region, countryList);
        } else {
            regions.get(region).add(country);
        }
    }

    public String getRegion(String country) {
        for (String region : regions.keySet()) {
            if (regions.get(region).contains(country)) {
                return region;
            }
        }
        return "Oceania";
    }

}
