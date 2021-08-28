package com.game.service;

import com.game.dto.PlayerDto;
import com.game.entity.Player;
import com.game.exception.NoSuchPlayerException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    //  == Get players count ==
    public Long playersCount() {
        return playerRepository.count();
    }

    // == Get player ==
    public Player findByID(Long id) {
        return playerRepository.findById(id).orElseThrow(NoSuchPlayerException::new);
    }

    // == Create player ==
    public Player createPlayer(PlayerDto playerDTO) {
        Player player = new Player();
        return playerRepository.save(mapDTOToEntity(player, playerDTO));
    }

    // == Delete player ==
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    // == Update player ==
    public Player updatePlayer(Long playerId, PlayerDto playerDTO) {
        Player player = findByID(playerId);

        if (playerDTO.getName() != null){
            player.setName(playerDTO.getName());
        }
        if (playerDTO.getTitle() != null) {
            player.setTitle(playerDTO.getTitle());
        }
        if (playerDTO.getRace() != null) {
            player.setRace(playerDTO.getRace());
        }
        if (playerDTO.getProfession() != null) {
            player.setProfession(playerDTO.getProfession());
        }
        if (playerDTO.getBirthday() != null ) {
            player.setBirthday(playerDTO.getBirthday());
        }
        if (playerDTO.getBanned() != null) {
            player.setBanned(playerDTO.getBanned());
        }
        if (playerDTO.getExperience() != null) {
            player.setExperience(playerDTO.getExperience());
        }
        int level = levelCalculations(playerDTO);
        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevelCalculations(playerDTO, level));

        return playerRepository.saveAndFlush(player);
    }

    // == Get players list ==
//    public Page<Player> getListPlayers(Integer pageNumber, Integer pageSize, String field){
//        Sort sort = Sort.by()
//        Pageable page = PageRequest.of(pageNumber, pageSize);
//        Player player = new Player();
//        Example<Player> example = Example.of(player);
//        playerRepository.
//        return playerRepository.findAll(example, page);
//        return playerRepository.findAll(page);
//    }

    private Player mapDTOToEntity(Player player, PlayerDto playerDTO) {
            player.setName(playerDTO.getName());
            player.setTitle(playerDTO.getTitle());
            player.setRace(playerDTO.getRace());
            player.setProfession(playerDTO.getProfession());
            player.setBirthday(playerDTO.getBirthday());
            player.setBanned(playerDTO.getBanned());
            player.setExperience(playerDTO.getExperience());

        int level = levelCalculations(playerDTO);
        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevelCalculations(playerDTO, level));

        return player;
    }

    private PlayerDto mapEntityToDTO(Player player) {
        PlayerDto playerDTO = new PlayerDto();

        playerDTO.setName(player.getName());
        playerDTO.setTitle(player.getTitle());
        playerDTO.setRace(player.getRace());
        playerDTO.setProfession(player.getProfession());
        playerDTO.setBirthday(player.getBirthday());
        playerDTO.setBanned(player.getBanned());
        playerDTO.setExperience(player.getExperience());
        playerDTO.setLevel(player.getLevel());
        playerDTO.setUntilNextLevel(player.getUntilNextLevel());

        return playerDTO;
    }

    public boolean isIdValid(Long id) {
        return (id > 0 || id <= playersCount());
    }

    private Integer levelCalculations(PlayerDto playerDTO) {
        int exp = playerDTO.getExperience();
        return (((int) (Math.sqrt(2500 + 200 * exp)) - 50) / 100);
    }

    private Integer untilNextLevelCalculations(PlayerDto playerDTO, Integer lvl) {
        int exp = playerDTO.getExperience();
        return (50 * (lvl + 1) * (lvl + 2) - exp);
    }


}
