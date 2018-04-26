package ru.malyshev.cranescale;

import java.util.ArrayList;

//Created by Developer on 05.02.18.

public class ResultOne {

    private String name;
    private String date;
    private ArrayList<Number> list;
    private int counter;

    private String min;
    private String mid;
    private String max;

    private int sec;
    private Number maxFloat;

    public ResultOne(){
        name = "";
        date = "";
        list = new ArrayList<>();
        min = "0";
        mid = "0";
        max = "0";
        counter = 0;

        maxFloat = 0.0;
        sec = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setList(ArrayList<Number> list) {
        this.list = list;

        if (list.size() > 0) {
            calculate();
        }
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public String getSec() {
        return String.valueOf(sec);
    }

    public ArrayList<Number> getList() {
        return list;
    }

    public String getMin() {
        return min;
    }

    public String getMid() {
        return mid;
    }

    public String getMax() {
        return max;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public Number getMaxFloat() {
        return maxFloat;
    }

    //====
    private void calculate(){
        float average = 0;
        float sum = 0;
        int counter = 0;

        int indexOfMax = 0;
        int indexOfMin = 0;

        for (int i = 1; i < list.size(); i++) {

            if(list.get(i).floatValue() > 0){
                sum += list.get(i).floatValue();
                counter++;
            }

            if (list.get(i).floatValue() > list.get(indexOfMax).floatValue()) {
                indexOfMax = i;
            }
            else if (list.get(i).floatValue() < list.get(indexOfMin).floatValue())
            {
                indexOfMin = i;
            }
        }

        maxFloat = list.get(indexOfMax);

        min = String.valueOf(list.get(indexOfMin));
        max = String.valueOf(list.get(indexOfMax));

        average = sum / counter;

        float roundOff = (float) Math.round(average * 100) / 100;
        mid = String.valueOf(roundOff);

//        DecimalFormat df=new DecimalFormat("0.0");
//        String formate = df.format(average);
//        float finalValue = (float)df.parse(formate) ;
    }
}