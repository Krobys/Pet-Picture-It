package com.akrivonos.app_standart_java.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhotoMap {

    private Map<String, ArrayList<String>> photoMap = new HashMap<>();
    private ArrayList<String> sections = new ArrayList<>();
    private String activeSection;
    private int valueActiveSection = -1;

    public void addToMap(String key, String value) {
        boolean coincidence = false;
        for (String section : sections) {
            if (section.equals(key)) {
                coincidence = true;
                ArrayList<String> urlsInSection = photoMap.get(key);
                urlsInSection.add(value);
                photoMap.put(key, urlsInSection);
                break;
            }
        }
        if (!coincidence) {
            sections.add(key);
            ArrayList<String> urlsInSection = new ArrayList<>();
            urlsInSection.add(value);
            photoMap.put(key, urlsInSection);
        }
    }

    public boolean nextSection() {
        valueActiveSection++;
        if (valueActiveSection < sections.size()) {
            activeSection = sections.get(valueActiveSection);
            return true;
        }
        return false;
    }

    public String getCurrentSectionName() {
        if (valueActiveSection >= 0 && valueActiveSection < sections.size()) {
            return activeSection;
        }
        return "";
    }

    public ArrayList<String> getValuesInSection() {
        for (Map.Entry<String, ArrayList<String>> photo : photoMap.entrySet()) {
            if (photo.getKey().equals(activeSection)) {
                return photo.getValue();
            }
        }
        return null;
    }

    public int size() {
        return photoMap.size();
    }
}
