package com.game.service;

import com.game.dto.PlayerDto;
import com.game.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
// TODO @Component ??? - what the difference
public class PlayerValidationService {
    /**
     * @param playerDTO
     * @return true if dto is valid
     */
    private boolean isPlayerDTOValid(PlayerDto playerDTO) {
        if (isNullInDtoParams(playerDTO)) {
            return false;
        }
        return (isNameLenValid(playerDTO) && isTitleLenValid(playerDTO) && isExperienceValid(playerDTO) && isBirthdayValid(playerDTO));
    }

    public void validatePlayer(PlayerDto playerDto) {
        if (!isPlayerDTOValid(playerDto)){
            throw new ValidationException();
        }
    }


    /**
     * @param playerDTO
     * @return true if null exists
     */
    private boolean isNullInDtoParams(PlayerDto playerDTO) {
        boolean isNullInDtoParams =
                playerDTO.getName() == null ||
                        playerDTO.getTitle() == null ||
                        playerDTO.getRace() == null ||
                        playerDTO.getProfession() == null ||
                        playerDTO.getBirthday() == null ||
                        playerDTO.getExperience() == null;

        return isNullInDtoParams;
    }


    /**
     * @param playerDTO
     * @return returns true if name is valid
     */
    private boolean isNameLenValid(PlayerDto playerDTO) {
        int nameLen = playerDTO.getName().trim().length();
        return nameLen > 0 && nameLen <= 12;
    }

    /**
     * @param playerDTO
     * @return returns true if title is valid
     */
    private boolean isTitleLenValid(PlayerDto playerDTO) {
        int titleLen = playerDTO.getTitle().trim().length();
        return titleLen > 0 && titleLen <= 30;
    }

    /**
     * @param playerDTO
     * @return returns true if experience is valid
     */
    private boolean isExperienceValid(PlayerDto playerDTO) {
        return playerDTO.getExperience() >= 0 && playerDTO.getExperience() <= 10000000;
    }

    /**
     * @param playerDTO
     * @return returns true if birthday is valid
     */
    private boolean isBirthdayValid(PlayerDto playerDTO) {
        Date date = playerDTO.getBirthday();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();

        return playerDTO.getBirthday().getTime() >= 0 && (year >= 2000 && year <= 3000);
    }


}
