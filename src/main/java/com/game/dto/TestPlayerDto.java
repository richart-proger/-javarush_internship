package com.game.dto;

import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.Date;

public class TestPlayerDto {
    private String name;
    private Race race;
    private Profession profession;

    public TestPlayerDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }


}
