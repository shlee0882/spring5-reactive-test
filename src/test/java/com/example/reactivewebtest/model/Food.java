package com.example.reactivewebtest.model;

import lombok.Data;

import java.util.List;

@Data
public class Food {
    String name;
    Integer price;

    public Food(){

    }
    public Food(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
