package com.game.controller;

import com.game.dto.FilterDto;
import com.game.dto.PlayerDto;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.InvalidIdException;
import com.game.service.PlayerService;
import com.game.service.PlayerValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerValidationService playerValidationService;

    @Autowired
    public PlayerController(PlayerService playerService, PlayerValidationService playerValidationService) {
        this.playerService = playerService;
        this.playerValidationService = playerValidationService;
    }

    // == Get players count ==
    @GetMapping("/count")
    public int getPlayersCount() {
        return Math.toIntExact(playerService.playersCount());
    }

    // == Get player ==
    @GetMapping("/{id}")
    public Player getByID(@PathVariable Long id) {
        if (!playerService.isIdValid(id)) {
            throw new InvalidIdException();
        }
        Player player = playerService.findById(id);

        return player;
    }

    // == Delete player ==
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        Player player = getByID(id);
        if (player != null) {
            playerService.deletePlayer(id);
        }
    }

    // == Create player ==
    @PostMapping
    public Player createPlayer(@RequestBody PlayerDto playerDto) {
        playerValidationService.validatePlayerForCreate(playerDto);
        return playerService.createPlayer(playerDto);
    }

    //     == Update player ==
    @PostMapping("/{id}")
    public Player updatePlayer(@RequestBody PlayerDto playerDto, @PathVariable(required = false) Long id) {

        if (!playerService.isIdValid(id)) {
            throw new InvalidIdException();
        }
        return playerService.updatePlayer(id, playerDto);
    }

    // == Get players list ==
    @GetMapping
    public List<Player> getPlayersList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false, defaultValue = "0") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "0") Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false, defaultValue = "0") Integer minExperience,
            @RequestParam(value = "maxExperience", required = false, defaultValue = "0") Integer maxExperience,
            @RequestParam(value = "minLevel", required = false, defaultValue = "0") Integer minLevel,
            @RequestParam(value = "maxLevel", required = false, defaultValue = "0") Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {
        FilterDto filterDto = new FilterDto();
        filterDto.setName(name);
        filterDto.setTitle(title);
        filterDto.setRace(race);
        filterDto.setProfession(profession);
        filterDto.setAfter(after);
        filterDto.setBefore(before);
        filterDto.setBanned(banned);
        filterDto.setMinExperience(minExperience);
        filterDto.setMaxExperience(maxExperience);
        filterDto.setMinLevel(minLevel);
        filterDto.setMaxLevel(maxLevel);
        filterDto.setOrder(order);
        filterDto.setPageSize(pageSize);
        filterDto.setPageNumber(pageNumber);

        return playerService.getListPlayers(filterDto).getContent();
    }
}