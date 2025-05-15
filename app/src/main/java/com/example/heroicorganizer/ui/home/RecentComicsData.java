package com.example.heroicorganizer.ui.home;

import com.example.heroicorganizer.model.LibraryComic;

import java.util.ArrayList;
import java.util.List;

public class RecentComicsData {
    public static final List<LibraryComic> comicList = new ArrayList<>();

    public static void addRecentComic(LibraryComic comic) {
        comicList.add(0, comic);
        if (comicList.size() > 10) {
            comicList.remove(comicList.size() - 1);
        }
    }
    public static List<LibraryComic> getComicList() {
        return comicList;
    }
}
