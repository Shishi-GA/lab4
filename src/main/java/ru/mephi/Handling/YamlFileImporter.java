package ru.mephi.Handling;

import ru.mephi.Reactors.Reactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
public class YamlFileImporter extends FileHandler{
    @Override
    public boolean checkType(String filePath) {
        return filePath.endsWith(".yaml");
    }

    @Override
    public HashMap<String, Reactor> loadReactors(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        HashMap<String, Reactor> reactors = new HashMap<>();
        File file = new File(filePath);

        try {
            Map<String, Map<String, Object>> data = objectMapper.readValue(file, Map.class);

            for (String reactorName : data.keySet()) {
                Map<String, Object> reactorData = data.get(reactorName);
                Reactor reactor = new Reactor(
                        ((Number) reactorData.get("burnup")).doubleValue(),
                        (String) reactorData.get("class"),
                        ((Number) reactorData.get("electrical_capacity")).doubleValue(),
                        ((Number) reactorData.get("first_load")).doubleValue(),
                        ((Number) reactorData.get("kpd")).doubleValue(),
                        ((Number) reactorData.get("life_time")).doubleValue(),
                        ((Number) reactorData.get("termal_capacity")).doubleValue(),
                        "yaml"
                );
                reactors.put(reactor.reactorClass, reactor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reactors;
    }
}
