package ru.mephi;

import ru.mephi.Reactors.ReactorDB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class ConsumptionCalculator {
    private Map<String, List<ReactorDB>> reactors;

    public ConsumptionCalculator(Map<String, List<ReactorDB>> reactors) {
        this.reactors = reactors;
    }
    public Map<Integer, Double> calculateReactorConsumption(ReactorDB reactor) {
        Map<Integer, Double> consumptionPerYear = new TreeMap<>();
        reactor.fixLoadFactors();

        Double burnUp = reactor.getBurnup();

        for (Integer year : reactor.getLoadFactors().keySet()) {

            Double loadFactor = reactor.getLoadFactors().get(year);
            Double consumption = (reactor.getThermalCapacity() / burnUp) * (loadFactor);
            consumptionPerYear.put(year, consumption);
        }

        return consumptionPerYear;
    }



    public Map<String, Map<Integer, Double>> calculateConsumptionByCountries() {
        return calculateConsumption(ReactorDB::getCountry);
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByRegions(Regions regions) {
        return calculateConsumption(reactor -> regions.getRegion(reactor.getCountry()));
    }

    public Map<String, Map<Integer, Double>> calculateConsumptionByOperator() {
        return calculateConsumption(ReactorDB::getOperator);
    }

    private Map<String, Map<Integer, Double>> calculateConsumption(Function<ReactorDB, String> keyExtractor) {
        Map<String, Map<Integer, Double>> consumption = new HashMap<>();

        for (List<ReactorDB> reactorList : reactors.values()) {
            for (ReactorDB reactor : reactorList) {
                String key = keyExtractor.apply(reactor);
                Map<Integer, Double> entityConsumption = consumption.computeIfAbsent(key, k -> new HashMap<>());
                Map<Integer, Double> consumptionPerYear = calculateReactorConsumption(reactor);

                for (Integer year : consumptionPerYear.keySet()) {
                    entityConsumption.merge(year, consumptionPerYear.get(year), Double::sum);
                }
            }
        }

        return consumption;
    }
}