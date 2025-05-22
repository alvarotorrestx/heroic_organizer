package com.example.heroicorganizer.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.heroicorganizer.model.LibraryComic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentComicsData {
    private static final String PREF_NAME = "recent_comics_pref";
    private static final String KEY_COMICS = "recent_comics";
    public static final List<LibraryComic> comicList = new ArrayList<>();

    public static void addRecentComic(LibraryComic comic, Context context) {
        comicList.add(0, comic);
        if (comicList.size() > 10) {
            comicList.remove(comicList.size() - 1);
        }
        saveComicList(context);
    }

    public static List<LibraryComic> getComicList() {
        return comicList;
    }

    public static void saveComicList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(comicList);
        editor.putString(KEY_COMICS, json);
        editor.apply();
    }

    public static void loadComicList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_COMICS, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<LibraryComic>>() {}.getType();
            List<LibraryComic> savedList = gson.fromJson(json, type);
            comicList.clear();
            comicList.addAll(savedList);
        }
    }
}
