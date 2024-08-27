package ru.mephi.Handling;

import ru.mephi.Reactors.Reactor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class JsonFileImporter extends FileHandler{
    @Override
    public boolean checkType(String filePath) {
        return filePath.endsWith(".json");
    }
    @Override
    public HashMap<String, Reactor> loadReactors(String filePath) {
        HashMap<String, Reactor> reactors = new HashMap<>();

        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            JSONObject reactorsObj = (JSONObject) parser.parse(reader);

            for (Object key : reactorsObj.keySet()) {
                String reactorClass = (String) key;
                JSONObject reactorData = (JSONObject) reactorsObj.get(reactorClass);

                Reactor reactor = new Reactor(
                        ((Number) reactorData.get("burnup")).doubleValue(),
                        reactorData.get("class").toString(),
                        ((Number) reactorData.get("electrical_capacity")).doubleValue(),
                        ((Number) reactorData.get("first_load")).doubleValue(),
                        ((Number) reactorData.get("kpd")).doubleValue(),
                        ((Number) reactorData.get("life_time")).doubleValue(),
                        ((Number) reactorData.get("termal_capacity")).doubleValue(),
                        "json"
                );

                reactors.put(reactorClass, reactor);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return reactors;
    }
}
