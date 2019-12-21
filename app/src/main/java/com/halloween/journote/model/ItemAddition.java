package com.halloween.journote.model;

public class ItemAddition {
    private ItemAddition itemAddition;

    public enum AdditionType {MOOD,WEATHER,FAVOUR};

    public enum Mood {HAPPY,PEACE,SAD};
    public enum Weather {SUNNY,CLOUDY,RAINY};
    public enum Favour {FAVOUR,NON_FALOUR}

    private AdditionType type;
    private Mood mood;
    private Weather weather;
    private Favour favour;


    public ItemAddition(Mood mood){
        this.mood = mood;
        this.type = AdditionType.MOOD;
    }
    public ItemAddition(Weather weather){
        this.weather = weather;
        this.type = AdditionType.WEATHER;
    }
    public ItemAddition(Favour favour){
        this.favour = favour;
        this.type = AdditionType.FAVOUR;
    }
    public AdditionType getType(){
        return type;
    }
    @Override
    public String toString(){
        String value;
        switch(type){
            case MOOD:
                return type+": "+mood;
            case WEATHER:
                return type+": "+weather;
            case FAVOUR:
                return type+": "+favour;
            default:
                return "error";
        }
    }
}
