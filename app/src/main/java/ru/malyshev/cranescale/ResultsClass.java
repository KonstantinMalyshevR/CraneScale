package ru.malyshev.cranescale;

//Created by Developer on 05.02.18.

import java.util.ArrayList;

public class ResultsClass {

    private String resultsClassId;
    private ArrayList<ResultOne> list;

    ResultsClass(String id){
        resultsClassId = id;
        list = new ArrayList<>();
    }

    public String getResultsClassId() {
        return resultsClassId;
    }

    public void addToList(ResultOne res){
        list.add(res);
    }

    public String getListItemsCount(){
        return String.valueOf(list.size());
    }

    public ArrayList<ResultOne> getList() {
        return list;
    }
}