package ru.csm.api.player;

public enum SkinModel {

    STEVE("steve"),
    ALEX("slim");

    private String name;

    SkinModel(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static SkinModel fromName(String name){
        SkinModel model = SkinModel.STEVE;
        if(name.equalsIgnoreCase("slim")){
            model = SkinModel.ALEX;
        }

        return model;
    }
}
