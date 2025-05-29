package com.example.heroicorganizer.model;

import java.util.List;

public class CharacterDetail {
    public CharacterResult results;

    public static class CharacterResult {
        public List<Team> teams;
    }

    public static class Team {
        public String name;
    }
}
