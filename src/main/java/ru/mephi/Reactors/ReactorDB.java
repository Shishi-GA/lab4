package ru.mephi.Reactors;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReactorDB {
    private final String name;
    private final String country;
    private Double burnup;
    private final String reactorType;
    private final String owner;
    private final String operator;
    private final String status;
    private final Integer thermalCapacity;
    private final Map<Integer, Double> loadFactors;
    private final Integer firstGridConnection;
    private final Integer suspendedDate;
    private final Integer permanentShutdownDate;



    public ReactorDB(String name, String country, String reactorType, String owner, String operator, String status, Integer thermalCapacity, Integer firstGridConnection, Integer suspendedDate, Integer permanentShutdownDate) {
        this.name = name;
        this.country = country;
        this.burnup = burnup;
        this.reactorType = reactorType;
        this.owner = owner;
        this.operator = operator;
        this.status = status;
        this.thermalCapacity = thermalCapacity;
        this.loadFactors = new HashMap<>();
        this.firstGridConnection = firstGridConnection;
        this.suspendedDate = suspendedDate;
        this.permanentShutdownDate = permanentShutdownDate;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }


    public String getReactorType() {
        return reactorType;
    }


    public String getOwner() {
        return owner;
    }

    public String getOperator() {
        return operator;
    }


    public String getStatus() {
        return status;
    }

    public Integer getThermalCapacity() {
        return thermalCapacity;
    }

    public void addLoadFactor(Integer year, Double loadFactor) {
        if (Objects.equals(year, this.getFirstGridConnection())) {
            loadFactor *= 3;
        }
        loadFactors.put(year, loadFactor);
    }
    public Double getBurnup() {
        return burnup;
    }

    public void setBurnup(Double burnup) {
        this.burnup = burnup;
    }

    public void fixLoadFactors() {
        for (Integer year : loadFactors.keySet()) {
            if ((year >= this.getSuspendedDate() && this.getSuspendedDate() != 0) || (year >= this.getPermanentShutdownDate() && this.getPermanentShutdownDate() != 0)) {
                loadFactors.put(year, 0.0);
            } else if (loadFactors.get(year) == 0 && year > this.getFirstGridConnection()) {
                loadFactors.put(year, 85.0);
            }
        }
    }

    public Map<Integer, Double> getLoadFactors() {
        return new HashMap<>(loadFactors);
    }

    public Integer getFirstGridConnection() {
        return firstGridConnection;
    }

    public Integer getSuspendedDate() {
        return suspendedDate;
    }


    public Integer getPermanentShutdownDate() {
        return permanentShutdownDate;
    }


    @Override
    public String toString() {
        return this.getName();
    }


}