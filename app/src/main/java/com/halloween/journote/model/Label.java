package com.halloween.journote.model;


public class Label {
    private String labelName;
    private String discription;
    private Boolean propertyLabel;
    private Boolean moodAccessibility;
    private Boolean weatherAccessibility;
    private Boolean favourAccessibility;

    public Label(String labelName, String discription){
        this.labelName = labelName;
        this.discription = discription;
        propertyLabel = false;
    }
    public Label(String labelName, String discription,Boolean moodAccessibility,Boolean weatherAccessibility,Boolean favourAccessibility){
        this.labelName = labelName;
        this.discription = discription;
        propertyLabel = true;
        this.moodAccessibility = moodAccessibility;
        this.weatherAccessibility = weatherAccessibility;
        this.favourAccessibility = favourAccessibility;
    }
    //TODO fromString()
    public Label(String string){
        int[] index = new int[7];
        index[0]=1;index[6]=string.length()-1;
        for(int i=1;i<=5;i++){
            index[1]=string.indexOf("|",index[i-1]+1);
        }
        labelName = string.substring(index[0],index[1]);
        discription = string.substring(index[1]+1,index[2]);
        propertyLabel = boolFromString(string.substring(index[2]+1,index[3]));
        moodAccessibility = boolFromString(string.substring(index[3]+1,index[4]));
        weatherAccessibility = boolFromString(string.substring(index[4]+1,index[5]));
        favourAccessibility = boolFromString(string.substring(index[5]+1,index[6]));
    }

    private Boolean boolFromString(String str){
        if(str.equals("true")){return true;}
        else if(str.equals("false")){return false;}
        else{return null;}
    }

    @Override
    public String toString(){
        return "{"+labelName+"|"+discription+"|"+propertyLabel+"|"+moodAccessibility+"|"+weatherAccessibility+"|"+favourAccessibility+"}";
    }
}
