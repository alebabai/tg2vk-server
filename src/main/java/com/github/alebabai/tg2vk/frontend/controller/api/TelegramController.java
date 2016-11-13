package com.github.alebabai.tg2vk.frontend.controller.api;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
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

    private final TelegramService tgService;
    private final PathResolverService pathResolver;

    @Autowired
    private TelegramController(TelegramService tgService, PathResolverService pathResolver) {
        this.tgService = tgService;
        this.pathResolver = pathResolver;
    }

    @PostMapping(PathConstants.API_TELEGRAM_FETCH_UPDATES)
    public ResponseEntity<String> fetchUpdates(HttpServletRequest request) {
        ResponseEntity<String> response = new ResponseEntity<>("Update has been successfully handled!", HttpStatus.OK);
        try {
            final Update update = BotUtils.parseUpdate(request.getReader());
            final Message message = update.message();
            if (message != null) {
                String msgText = "Any message";
                SendMessage sendMessage = new SendMessage(update.message().chat().id(), msgText);
                if ("/login".equals(message.text())) {
                    sendMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                            new InlineKeyboardButton("Login").url(pathResolver.getServerUrl() + PathConstants.API_LOGIN)
                    }));
                }
                tgService.send(sendMessage);
            }
        } catch (Exception e) {
            LOGGER.error("Error during webhook update handling: ", e);
            response = new ResponseEntity<>("Some error happened", HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
