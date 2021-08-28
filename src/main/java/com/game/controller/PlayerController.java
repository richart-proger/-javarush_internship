package com.game.controller;

import com.game.dto.PlayerDto;
import com.game.dto.TestPlayerDto;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.InvalidIdException;
import com.game.exception.NoSuchPlayerException;
import com.game.exception.ValidationException;
import com.game.service.PlayerService;
import com.game.service.PlayerValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
        Player player = playerService.findByID(id);
        if (player == null) {
            throw new NoSuchPlayerException();
        }
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
    public Player createPlayer(@RequestBody PlayerDto playerDTO) {
        playerValidationService.validatePlayer(playerDTO);
        return playerService.createPlayer(playerDTO);
    }

    // == Update player ==
    @PostMapping("/{id}")
    public Player updatePlayer(/*@RequestBody TestPlayerDto playerDTO,*/@PathVariable(required=false) Long id) {
        // TODO validate id!!!
        // TODO validate data

        return playerService.updatePlayer(id, null);
    }

    // == Get players list ==
//    @GetMapping
//    public Page<Player> getAllPlayers(
//            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
//            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
//            @RequestParam(value = "order", required = false, defaultValue = "PlayerOrder.ID") PlayerOrder order,
//
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "title", required = false) String title,
//            @RequestParam(value = "race", required = false) Race race,
//            @RequestParam(value = "profession", required = false) Profession profession,
//            @RequestParam(value = "after", required = false) Long after,
//            @RequestParam(value = "before", required = false) Long before,
//            @RequestParam(value = "banned", required = false) Boolean banned,
//            @RequestParam(value = "minExperience", required = false) Integer minExperience,
//            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
//            @RequestParam(value = "minLevel", required = false) Integer minLevel,
//            @RequestParam(value = "maxLevel", required = false) Integer maxLevel
//
////            http://localhost:8888/rest/players?name="Камираж"&order="PlayerOrder.ID"&pageNumber=1&pageSize=3
//            ) {
//        return playerService.getListPlayers(pageNumber, pageSize, name);
    }

//    public Page<Player> getAllPlayers(Pageable pageable){
//        return playerService.getListPlayers(pageable);
//    }
//}