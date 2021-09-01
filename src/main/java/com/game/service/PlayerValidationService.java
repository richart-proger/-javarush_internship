package com.game.service;

import com.game.dto.PlayerDto;
import com.game.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service

public class PlayerValidationService {
    /**
     * @param playerDto
     * @return true if dto is valid
     */
    private boolean isPlayerDtoValidForCreate(PlayerDto playerDto) {
        if (isNullInDtoParamsForCreate(playerDto)) {
            return false;
        }
        return (isNameLenValid(playerDto) && isTitleLenValid(playerDto) && isExperienceValid(playerDto) && isBirthdayValid(playerDto));
    }

    public void validatePlayerForCreate(PlayerDto playerDto) {
        if (!isPlayerDtoValidForCreate(playerDto)) {
            throw new ValidationException();
        }
    }

    /**
     * @param playerDto
     * @return true if null exists
     */
    private boolean isNullInDtoParamsForCreate(PlayerDto playerDto) {
        boolean isNullInDtoParams =
                playerDto.getName() == null ||
                        playerDto.getTitle() == null ||
                        playerDto.getRace() == null ||
                        playerDto.getProfession() == null ||
                        playerDto.getBirthday() == null ||
                        playerDto.getExperience() == null;

        return isNullInDtoParams;
    }

    /**
     * @param playerDto
     * @return returns true if name is valid
     */
    boolean isNameLenValid(PlayerDto playerDto) {
        int nameLen = playerDto.getName().trim().length();
        return nameLen > 0 && nameLen <= 12;
    }

    /**
     * @param playerDto
     * @return returns true if title is valid
     */
    boolean isTitleLenValid(PlayerDto playerDto) {
        int titleLen = playerDto.getTitle().trim().length();
        return titleLen > 0 && titleLen <= 30;
    }

    /**
     * @param playerDto
     * @return returns true if experience is valid
     */
    boolean isExperienceValid(PlayerDto playerDto) {
        return playerDto.getExperience() >= 0 && playerDto.getExperience() <= 10000000;
    }

    /**
     * @param playerDto
     * @return returns true if birthday is valid
     */
    boolean isBirthdayValid(PlayerDto playerDto) {
        Date date = playerDto.getBirthday();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();

        return playerDto.getBirthday().getTime() >= 0 && (year >= 2000 && year <= 3000);
    }
}
