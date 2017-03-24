package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.service.TelegramUpdateHandler;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController(PathConstants.API_TELEGRAM)
public class TelegramController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramController.class);

    private final TelegramUpdateHandler updateHandler;

    @Autowired
    public TelegramController(TelegramUpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @PostMapping(PathConstants.API_TELEGRAM_FETCH_UPDATES)
    public ResponseEntity<String> fetchUpdates(HttpServletRequest request) {
        ResponseEntity<String> response = new ResponseEntity<>("Update has been successfully handled!", HttpStatus.OK);
        try {
            final Update update = BotUtils.parseUpdate(request.getReader());
            updateHandler.handleAsync(update);
        } catch (Exception e) {
            LOGGER.error("Error during webhook update handling: ", e);
            response = new ResponseEntity<>("Some error happened", HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
