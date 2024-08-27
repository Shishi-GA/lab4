package ru.mephi.Handling;

import ru.mephi.Reactors.ReactorDB;
import ru.mephi.Regions;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBReader {
    public static Map<String,List<ReactorDB>> importReactors(File file) throws SQLException {
        String DB_URL = "jdbc:sqlite:" + file.getAbsolutePath();
        Map<String,List<ReactorDB>> reactorsDB =new HashMap<>();
        try (Connection connection= DriverManager.getConnection(DB_URL)){
            if (connection!=null){
                String operatorQuery = "SELECT DISTINCT operator FROM ReactorsFromPRIS";
                try (Statement statement=connection.createStatement(); ResultSet resultSet=statement.executeQuery(operatorQuery)){
                    while (resultSet.next()){
                        String operator = resultSet.getString("operator");
                        List<ReactorDB> operatorReactors = new ArrayList<>();
                        String reactorQuery= "SELECT * FROM ReactorsFromPRIS WHERE operator = ?";
                        try (PreparedStatement reactorSt = connection.prepareStatement(reactorQuery)){
                            reactorSt.setString(1,operator);
                            try (ResultSet reactorRS = reactorSt.executeQuery()){
                                while (reactorRS.next()){
                                    String name = reactorRS.getString("name");
                                    String country = reactorRS.getString("country");
                                    String reactorType = reactorRS.getString("type");
                                    String owner = reactorRS.getString("owner");
                                    String status = reactorRS.getString("status");
                                    Integer thermalCapacity = reactorRS.getInt("thermalCapacity");
                                    Integer firstGridConnection = reactorRS.getInt("firstGridConnection");
                                    Integer loadFactor = reactorRS.getInt("loadFactor");
                                    Integer suspendedDate = reactorRS.getInt("suspendedDate");
                                    Integer permanentShutdownDate = reactorRS.getInt("permanentShutdownDate");
                                    ReactorDB reactorDB = new ReactorDB(name,country,reactorType,owner,operator,status,thermalCapacity,firstGridConnection,suspendedDate,permanentShutdownDate);
                                    operatorReactors.add(reactorDB);


                                }

                            }
                        }
                        reactorsDB.put(operator,operatorReactors);
                    }
                }
                String loadFactorQuery = "SELECT * FROM LoadFactor";
                try (Statement statement=connection.createStatement();ResultSet resultSet=statement.executeQuery(loadFactorQuery)){
                    while (resultSet.next()){
                        String name = resultSet.getString("reactor");
                        Integer year = resultSet.getInt("year");
                        Double loadFactor = resultSet.getDouble("loadfactor");
                        reactorsDB.values().stream().flatMap(List::stream).filter(reactorDB -> reactorDB.getName().equals(name)).findFirst().ifPresent(reactorDB -> reactorDB.addLoadFactor(year,loadFactor));
                    }
                }
            }
            reactorsDB.values().stream().flatMap(List::stream).forEach(ReactorDB::fixLoadFactors);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
        return reactorsDB;
    }
    public static Regions importRegions(File file) throws SQLException {
        Regions regions = new Regions();
        String DB_URL = "jdbc:sqlite:" + file.getAbsolutePath();
        try (Connection connection=DriverManager.getConnection(DB_URL)){
            if(connection!=null){
                String regionsQuery="SELECT * FROM Countries";
                try (Statement statement=connection.createStatement();ResultSet resultSet=statement.executeQuery(regionsQuery)){
                    while (resultSet.next()){
                        String country=resultSet.getString("country");
                        String region=resultSet.getString("region");
                        regions.addCountry(region,country);
                    }
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
        return regions;
    }
}