package com.game.dto;

import com.game.controller.PlayerOrder;
import com.game.entity.Profession;
import com.game.entity.Race;

// for filtering
public class FilterDto {
    // Pattern Builder
    String name;
    String title;
    Race race;
    Profession profession;
    Long after;
    Long before;
    Boolean banned;
    Integer minExperience;
    Integer maxExperience;
    Integer minLevel;
    Integer maxLevel;
    PlayerOrder order;
    Integer pageNumber;
    Integer pageSize;

    public FilterDto() {
    }

    // for pagination
    class Paging{
        Integer pageNumber;
        Integer pageSize;

        public Paging(Integer pageNumber, Integer pageSize) {
            this.pageNumber = pageNumber;
            this.pageNumber = pageSize;
        }
    }
}
