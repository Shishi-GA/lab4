package ru.mephi;

import ru.mephi.Reactors.ReactorDB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BurnupMaker {
    HashMap<String,Double> reactorsType;
    Map<String, List<ReactorDB>> reactorsDB;
    public BurnupMaker(HashMap<String,Double> reactorsType, Map<String, List<ReactorDB>> reactorsDB){
        this.reactorsType=reactorsType;
        this.reactorsDB=reactorsDB;
        match();
    }
    public void match() {
        for (Map.Entry<String, List<ReactorDB>> entry : reactorsDB.entrySet()) {
            for (ReactorDB reactorDB:entry.getValue()) {
                for (Map.Entry<String, Double> entryType : reactorsType.entrySet()) {
                    if (reactorDB.getReactorType().equals(entryType.getKey())) {
                        reactorDB.setBurnup(entryType.getValue());
                    }
                }
            }
        }
    }
}