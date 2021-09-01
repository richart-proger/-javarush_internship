package com.game.service;

import com.game.dto.FilterDto;
import com.game.dto.PlayerDto;
import com.game.entity.Player;
import com.game.exception.NoSuchPlayerException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerValidationService playerValidationService;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PlayerValidationService playerValidationService) {
        this.playerRepository = playerRepository;
        this.playerValidationService = playerValidationService;
    }

    //  == Get players count ==
    public Long playersCount() {
        return playerRepository.count();
    }

    // == Get player ==
    public Player findById(Long id) {
        return playerRepository.findById(id).orElseThrow(NoSuchPlayerException::new);
    }

    // == Create player ==
    public Player createPlayer(PlayerDto playerDto) {
        Player player = new Player();
        return playerRepository.save(mapDtoToEntity(player, playerDto));
    }

    // == Get players page ==
    public Page<Player> getListPlayers(FilterDto filterDto) {
        Pageable pageable = PageRequest.of(filterDto.getPageNumber(), filterDto.getPageSize());

        Page<Player> page = playerRepository.findAll((Specification<Player>) (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (filterDto.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filterDto.getName().toLowerCase() + "%"));
            }
            if (filterDto.getTitle() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + filterDto.getTitle().toLowerCase() + "%"));
            }
            if (filterDto.getRace() != null) {
                predicates.add(criteriaBuilder.equal(root.get("race"), filterDto.getRace()));
            }
            if (filterDto.getProfession() != null) {
                predicates.add(criteriaBuilder.equal(root.get("profession"), filterDto.getProfession()));
            }
            if (filterDto.getMinExperience() < filterDto.getMaxExperience()) {
                predicates.add(criteriaBuilder.between(root.get("experience"), filterDto.getMinExperience(), filterDto.getMaxExperience()));
            }

            Date before = new Date(filterDto.getBefore());
            Date after = new Date(filterDto.getAfter());
            if (filterDto.getAfter() != 0 && filterDto.getBefore() != 0 && filterDto.getAfter() < filterDto.getBefore()) {
                predicates.add(criteriaBuilder.between(root.get("birthday"), after, before));
            } else if (filterDto.getAfter() != 0 && filterDto.getBefore() == 0 ){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), after));

            } else if (filterDto.getAfter() == 0 && filterDto.getBefore() != 0 ){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), before));
            }

            if (filterDto.getBanned() != null) {
                predicates.add(criteriaBuilder.equal(root.get("banned"), filterDto.getBanned()));
            }

            if (filterDto.getMinLevel() >= 0 && filterDto.getMaxLevel() > 0 && filterDto.getMinLevel() < filterDto.getMaxLevel()) {
                predicates.add(criteriaBuilder.between(root.get("level"), filterDto.getMinLevel(), filterDto.getMaxLevel()));
            }

            orderBy(filterDto, root, criteriaBuilder, query);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);

        page.getTotalElements();
        page.getTotalPages();
        return page;
    }

    // == Delete player ==
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    // == Update player ==
    public Player updatePlayer(Long playerId, PlayerDto playerDto) {
        Player player = findById(playerId);

        if (playerDto.getName() != null && playerValidationService.isNameLenValid(playerDto)) {
            player.setName(playerDto.getName());
        }
        if (playerDto.getTitle() != null && playerValidationService.isTitleLenValid(playerDto)) {
            player.setTitle(playerDto.getTitle());
        }
        if (playerDto.getRace() != null) {
            player.setRace(playerDto.getRace());
        }
        if (playerDto.getProfession() != null) {
            player.setProfession(playerDto.getProfession());
        }
        if (playerDto.getBirthday() != null && playerValidationService.isBirthdayValid(playerDto)) {
            player.setBirthday(playerDto.getBirthday());
        }
        if (playerDto.getBanned() != null) {
            player.setBanned(playerDto.getBanned());
        }
        if (playerDto.getExperience() != null && playerValidationService.isExperienceValid(playerDto)) {
            player.setExperience(playerDto.getExperience());
        }
        int level = levelCalculations(playerDto);
        player.setLevel(level);
        int untilNextLevel = untilNextLevelCalculations(playerDto, level);
        player.setUntilNextLevel(untilNextLevel);

        return playerRepository.saveAndFlush(player);
    }

    private Player mapDtoToEntity(Player player, PlayerDto playerDto) {
        player.setName(playerDto.getName());
        player.setTitle(playerDto.getTitle());
        player.setRace(playerDto.getRace());
        player.setProfession(playerDto.getProfession());
        player.setBirthday(playerDto.getBirthday());
        player.setBanned(playerDto.getBanned());
        player.setExperience(playerDto.getExperience());

        int level = levelCalculations(playerDto);
        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevelCalculations(playerDto, level));

        return player;
    }

    private PlayerDto mapEntityToDto(Player player) {
        PlayerDto playerDto = new PlayerDto();

        playerDto.setName(player.getName());
        playerDto.setTitle(player.getTitle());
        playerDto.setRace(player.getRace());
        playerDto.setProfession(player.getProfession());
        playerDto.setBirthday(player.getBirthday());
        playerDto.setBanned(player.getBanned());
        playerDto.setExperience(player.getExperience());
        playerDto.setLevel(player.getLevel());
        playerDto.setUntilNextLevel(player.getUntilNextLevel());

        return playerDto;
    }

    public boolean isIdValid(Long id) {
        return (id > 0 || id <= playersCount());
    }

    private Integer levelCalculations(PlayerDto playerDto) {
        int exp = playerDto.getExperience();
        return (((int) (Math.sqrt(2500 + 200 * exp)) - 50) / 100);
    }

    private Integer untilNextLevelCalculations(PlayerDto playerDto, Integer lvl) {
        int exp = playerDto.getExperience();
        return (50 * (lvl + 1) * (lvl + 2) - exp);
    }

    private void orderBy(FilterDto filterDto, Root<Player> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query) {
        Order order = null;
        switch (filterDto.getOrder()) {
            case ID:
                order = criteriaBuilder.asc(root.get("id"));
                break;
            case NAME:
                order = criteriaBuilder.asc(root.get("name"));
                break;
            case EXPERIENCE:
                order = criteriaBuilder.asc(root.get("experience"));
                break;
            case BIRTHDAY:
                order = criteriaBuilder.asc(root.get("birthday"));
                break;
            case LEVEL:
                order = criteriaBuilder.asc(root.get("level"));
                break;
        }
        query.orderBy(order);
    }
}

