package ru.mephi.Reactors;

public class Reactor {
    public double burnup;
    public String reactorClass;
    public double electricalCapacity;
    public double firstLoad;
    public double kpd;
    public double lifeTime;
    public double terminalCapacity;
    public String fileType;



    public Reactor(double burnup, String reactorClass, double electricalCapacity, double firstLoad, double kpd, double lifeTime, double terminalCapacity, String fileType) {
        this.burnup = burnup;
        this.reactorClass = reactorClass;
        this.electricalCapacity = electricalCapacity;
        this.firstLoad = firstLoad;
        this.kpd = kpd;
        this.lifeTime = lifeTime;
        this.terminalCapacity = terminalCapacity;
        this.fileType = fileType;
    }
    @Override
    public String toString() {
        return reactorClass;
    }
    public double getBurnup() {
        return burnup;
    }
}
