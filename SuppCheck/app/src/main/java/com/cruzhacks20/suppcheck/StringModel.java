package com.cruzhacks20.suppcheck;

public class StringModel {
    Long id;
    String name;

    public StringModel(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId(){
        return id;
    }

    public String getName(){return name;}
}
