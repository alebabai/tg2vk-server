package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.service.TelegramUpdateHandler;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramController.class);

    private final TelegramUpdateHandler updateHandler;

    @Autowired
    public TelegramController(TelegramUpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @PostMapping(value = "/updates")
    public ResponseEntity<String> fetchUpdates(HttpServletRequest request) {
        ResponseEntity<String> response = ResponseEntity.ok("Update has been successfully handled!");
        try {
            final Update update = Optional.ofNullable(BotUtils.parseUpdate(request.getReader()))
                    .filter(it -> Objects.nonNull(it.updateId()))
                    .orElseThrow(() -> new IllegalStateException("Incorrect update object"));
            updateHandler.handle(update);
        } catch (IllegalStateException e) {
            LOGGER.error("Error during webhook update handling: ", e);
            response = ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during webhook update handling: ", e);
            response = new ResponseEntity<>("Something goes wrong :(", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
