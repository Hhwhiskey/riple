package com.khfire22gmail.riple.fragments;

/**
 * Created by Kevin on 1/5/2016.
 */
public class Dog {

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public int legs;
    public String breed;
    public String size;
    public String color;
    public String hairLength;
    public int weight;

    public int getLegs() {
        return legs;
    }

    public String getHairLength() {
        return hairLength;
    }

    public int getWeight() {
        return weight;
    }


    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getBreed() {
        return breed;
    }


    public Dog() {
        this.legs = 4;

    }

    public Dog (String dogBreed) {
        this.breed = dogBreed;
    }

    public Dog (String dogBreed, String dogSize) {
        this.breed = dogBreed;
        this.size = dogSize;
    }

    public Dog(String dogBreed, String dogSize, String dogColor) {
        this.breed = dogBreed;
        this.size = dogSize;
        this.color = dogColor;
    }
}

//public class Cat extends Dog {
//
//
//    public Cat() {
//        this.legs = 4;
//
//    }
//
//    public Cat (String dogBreed) {
//        this.breed = dogBreed;
//    }
//
//    public Cat (String dogBreed, String dogSize) {
//        this.breed = dogBreed;
//        this.size = dogSize;
//    }
//
//    public Cat(String dogBreed, String dogSize, String dogColor) {
//        this.breed = dogBreed;
//        this.size = dogSize;
//        this.color = dogColor;
//    }
//}
//
