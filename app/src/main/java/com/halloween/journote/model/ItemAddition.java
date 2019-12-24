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

    public ItemAddition(String str){
     /*   String contentStr = str.substring(str.indexOf(":"+1));
        switch (contentStr){
            case "HAPPY":
                this.mood = Mood.HAPPY;this.type = AdditionType.MOOD;return;
            case "PEACE":
                this.mood = Mood.PEACE;this.type = AdditionType.MOOD;return;
            case "SAD":
                this.mood = Mood.SAD;this.type = AdditionType.MOOD;return;
            case "SUNNY":
                this.weather = Weather.SUNNY;this.type = AdditionType.WEATHER;return;
            case "CLOUDY":
                this.weather = Weather.CLOUDY;this.type = AdditionType.WEATHER;return;
            case "RAINY":
                this.weather = Weather.RAINY;this.type = AdditionType.WEATHER;return;
            case "FAVOUR":
                this.favour = Favour.FAVOUR;this.type = AdditionType.FAVOUR;return;
            case "UNFAVOUR":
                this.favour = Favour.NON_FALOUR;this.type = AdditionType.FAVOUR;return;
        }

      */
    }

    @Override
    public String toString(){
        String value;
        if(type != null) {
            switch(type){
                case MOOD:
                    return type+":"+mood;
                case WEATHER:
                    return type+":"+weather;
                case FAVOUR:
                    return type+":"+favour;
                default:
                    return "error";
            }
        }else {return "";}
    }
}
