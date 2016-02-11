package com.example.tacademy.sampleokhttp1;

import com.begentgroup.xmlparser.SerializedName;

import java.util.ArrayList;

/**
 * Created by dongja94 on 2016-02-05.
 */
public class NaverMovies {
    String title;
    String description;
    int total;
    int start;
    int display;
    @SerializedName("item")
    ArrayList<MovieItem> items;
}
