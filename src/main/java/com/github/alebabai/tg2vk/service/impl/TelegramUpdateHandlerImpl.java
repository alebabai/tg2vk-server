package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.PathResolver;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.VkMessagesProcessor;
import com.github.alebabai.tg2vk.service.VkService;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.alebabai.tg2vk.util.CommandUtils.parseCommand;
import static com.github.alebabai.tg2vk.util.constants.CommandConstants.*;

@Service
public class TelegramUpdateHandlerImpl extends AbstractTelegramUpdateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramUpdateHandlerImpl.class);

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final VkService vkService;
    private final VkMessagesProcessor vkMessageProcessor;
    private final PathResolver pathResolver;
    private final JwtTokenFactoryService tokenFactory;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramUpdateHandlerImpl(UserRepository userRepository,
                                     PathResolver pathResolver,
                                     TelegramService tgService,
                                     VkService vkService,
                                     VkMessagesProcessor vkMessageProcessor,
                                     JwtTokenFactoryService tokenFactory,
                                     MessageSource messageSource) {
        this.userRepository = userRepository;
        this.pathResolver = pathResolver;
        this.tgService = tgService;
        this.vkService = vkService;
        this.vkMessageProcessor = vkMessageProcessor;
        this.tokenFactory = tokenFactory;
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    protected void onInlineQueryReceived(InlineQuery query) {
        LOGGER.debug("Inline query received: {}", query);
        processVkChatsInlineQuery(query);
    }

    @Override
    protected void onChosenInlineResultReceived(ChosenInlineResult queryResult) {
        LOGGER.debug("Chosen inline result received: {}", queryResult);
    }

    @Override
    protected void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        LOGGER.debug("Callback query received: {}", callbackQuery);
        processChatLinkingCallbackQuery(callbackQuery);
    }

    @Override
    protected void onChanelPostReceived(Message post) {
        LOGGER.debug("Chanel post received: {}", post);

    }

    @Override
    protected void onEditedChanelPostReceived(Message post) {
        LOGGER.debug("Edited chanel post received: {}", post);

    }

    @Override
    protected void onEditedMessageReceived(Message message) {
        LOGGER.debug("Edited message received: {}", message);

    }

    @Override
    protected void onMessageReceived(Message message) {
        LOGGER.debug("Message received: {}", message);
        if (message.text().startsWith("/")) {
            parseCommand(message.text(), (command, args) -> processCommand(command, args, message));
        } else {
            /**
             * Disable echo
             */
            //SendMessage anyMessage = new SendMessage(message.chat().id(), message.text());
            //tgService.send(anyMessage);
        }
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkupForSelectedChat(User user, Integer chatId) {
        final boolean isLinkFlow = Objects.nonNull(user.getTempTgChatId());
        final InlineKeyboardButton linkButton = new InlineKeyboardButton(messages.getMessage("tg.inline.chats.link.label.button"))
                .callbackData(chatId.toString());
        return isLinkFlow
                ? new InlineKeyboardMarkup(new InlineKeyboardButton[]{linkButton})
                : new InlineKeyboardMarkup();
    }

    private void processVkChatsInlineQuery(InlineQuery query) {
        final AnswerInlineQuery answerInlineQuery = userRepository.findOneByTgId(query.from().id())
                .map(user -> vkService.findChats(user, query.query()).parallelStream()
                        .map(chat -> new InlineQueryResultArticle(chat.getId().toString(), chat.getTitle(), chat.getTitle())
                                .thumbUrl(chat.getThumbUrl())
                                .description(messages.getMessage("tg.inline.chats." + StringUtils.lowerCase(chat.getType().toString())))
                                .replyMarkup(getInlineKeyboardMarkupForSelectedChat(user, chat.getId()))
                                .inputMessageContent(new InputTextMessageContent(String.format("*%s*%n%s", chat.getTitle(), messages.getMessage("tg.inline.chats.link.msg.confirm")))
                                        .parseMode(ParseMode.Markdown)))
                        .collect(Collectors.toList()))
                .map(queryResults -> new AnswerInlineQuery(query.id(), queryResults.toArray(new InlineQueryResult[0]))
                        .isPersonal(true))
                .orElseGet(() -> new AnswerInlineQuery(query.id()).isPersonal(true));
        tgService.send(answerInlineQuery);
    }

    private void processChatLinkingCallbackQuery(CallbackQuery callbackQuery) {
        final String messageText = userRepository.findOneByTgId(callbackQuery.from().id())
                .filter(user -> Objects.nonNull(user.getTempTgChatId()))
                .map(user -> {
                    final Integer tgChatId = user.getTempTgChatId();
                    final Integer vkChatId = NumberUtils.createInteger(callbackQuery.data());
                    final boolean alreadyExists = user.getChatsSettings().parallelStream()
                            .anyMatch(it -> it.getTgChatId().equals(tgChatId) && it.getVkChatId().equals(vkChatId));
                    if (!alreadyExists) {
                        user.setTempTgChatId(null);
                        user.getChatsSettings().add(new ChatSettings(tgChatId, vkChatId).setStarted(true));
                        userRepository.save(user);
                        return messages.getMessage("tg.callback.chats.link.msg.success");
                    }
                    return messages.getMessage("tg.callback.chats.link.msg.already_exists");
                })
                .orElse(messages.getMessage("tg.callback.chats.link.msg.denied"));
        final AnswerCallbackQuery linkMessage = new AnswerCallbackQuery(callbackQuery.id())
                .text(messageText)
                .showAlert(true);
        tgService.send(linkMessage);
    }

    private void processCommand(String command, List<String> args, Message context) {
        switch (command) {
            case COMMAND_LOGIN:
                processLoginCommand(context);
                break;
            case COMMAND_START:
                processStartCommand(context);
                break;
            case COMMAND_STOP:
                processStopCommand(context);
                break;
            case COMMAND_LINK:
                processLinkCommand(context, args);
                break;
            case COMMAND_SETTINGS:
                processSettingsCommand(context);
                break;
            default:
                processUnknownCommand(context);
                break;
        }
    }

    private void processLoginCommand(Message context) {
        final SendMessage loginMessage = Optional.of(context.chat().type())
                .filter(Chat.Type.Private::equals)
                .map(type -> {
                    final Optional<User> userOptional = userRepository.findOneByTgId(context.from().id());
                    final String loginText = userOptional
                            .map(user -> String.join(
                                    "\n\n",
                                    messages.getMessage("tg.command.login.msg.warning"),
                                    messages.getMessage("tg.command.login.msg.instructions")
                            ))
                            .orElse(messages.getMessage("tg.command.login.msg.instructions"));
                    final Role[] roles = userOptional
                            .map(user -> user.getRoles().toArray(new Role[0]))
                            .orElse(new Role[]{Role.USER});

                    return new SendMessage(context.chat().id(), loginText)
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                    new InlineKeyboardButton(messages.getMessage("tg.command.login.label.button.get_token"))
                                            .url(pathResolver.resolveServerUrl("/api/redirect/vk-login")),
                                    new InlineKeyboardButton(messages.getMessage("tg.command.login.label.button.send_token"))
                                            .url(getClientRedirectUrl(tokenFactory.generate(context.from().id(), roles), "revoke-token")),
                            }));
                })
                .orElseGet(() -> new SendMessage(context.chat().id(), messages.getMessage("tg.command.login.msg.denied")));
        tgService.send(loginMessage);
    }

    private void processStartCommand(Message context) {
        final Function<Optional<User>, String> messageCodeHandler = getMessageCodeHandler(
                "tg.command.start.msg.already_started",
                "tg.command.start.msg.success",
                "tg.command.start.msg.anonymous");
        processUserInitCommand(context, vkMessageProcessor::start, messageCodeHandler);
    }

    private void processStopCommand(Message context) {
        final Function<Optional<User>, String> messageCodeHandler = getMessageCodeHandler(
                "tg.command.stop.msg.success",
                "tg.command.stop.msg.already_stopped",
                "tg.command.stop.msg.anonymous");
        processUserInitCommand(context, vkMessageProcessor::stop, messageCodeHandler);
    }

    private Function<Optional<User>, String> getMessageCodeHandler(String startedCode, String stoppedCode, String anonymousCode) {
        return userOptional -> userOptional
                .map(user -> user.getSettings().isStarted() ? startedCode : stoppedCode)
                .orElse(anonymousCode);
    }

    private void processUserInitCommand(Message context, Consumer<User> userSpecificAction, Function<Optional<User>, String> messageCodeHandler) {
        final Optional<User> userOptional = userRepository.findOneByTgId(context.from().id());
        final String messageCode = messageCodeHandler.apply(userOptional);
        userOptional.ifPresent(userSpecificAction);
        final SendMessage message = new SendMessage(context.chat().id(), messages.getMessage(messageCode))
                .parseMode(ParseMode.Markdown);
        tgService.send(message);
    }

    private void processLinkCommand(Message context, List<String> args) {
        final String query = StringUtils.join(args, StringUtils.SPACE);
        final Long chatId = context.chat().id();
        final SendMessage linkMessage = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    user.setTempTgChatId(Math.toIntExact(chatId));
                    userRepository.save(user);
                    return new SendMessage(chatId, messages.getMessage("tg.command.link.msg.info"))
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                    new InlineKeyboardButton(messages.getMessage("tg.command.link.label.button"))
                                            .switchInlineQueryCurrentChat(query),
                            }));
                })
                .orElseGet(() -> new SendMessage(chatId, messages.getMessage("tg.command.link.msg.denied")));
        tgService.send(linkMessage);
    }

    private void processSettingsCommand(Message context) {
        final SendMessage message = new ClientRedirectSendMessageBuilder()
                .context(context)
                .clientRoute("settings")
                .normalMessageCode("tg.command.settings.msg.info")
                .anonymousMessageCode("tg.command.settings.msg.denied")
                .buttonLabelCode("tg.command.settings.label.button.open")
                .build();
        tgService.send(message);
    }

    private void processUnknownCommand(Message context) {
        SendMessage anyMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.unknown.msg"));
        tgService.send(anyMessage);
    }

    private String getClientRedirectUrl(String token, String clientRoute) {
        return UriComponentsBuilder
                .fromUriString(pathResolver.resolveServerUrl("/api/redirect/client"))
                .pathSegment(clientRoute)
                .queryParam("token", token)
                .toUriString();
    }

    @Data
    @NoArgsConstructor
    @Accessors(fluent = true)
    protected class ClientRedirectSendMessageBuilder {
        private Message context;
        private String clientRoute;
        private String normalMessageCode;
        private String anonymousMessageCode;
        private String buttonLabelCode;

        SendMessage build() {
            return userRepository.findOneByTgId(context.from().id())
                    .map(user -> {
                        final Role[] roles = user.getRoles().toArray(new Role[0]);
                        final String text = messages.getMessage(normalMessageCode);
                        return new SendMessage(context.chat().id(), text)
                                .parseMode(ParseMode.Markdown)
                                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                        new InlineKeyboardButton(messages.getMessage(buttonLabelCode))
                                                .url(getClientRedirectUrl(tokenFactory.generate(context.from().id(), roles), clientRoute)),
                                }));
                    })
                    .orElseGet(() -> new SendMessage(context.chat().id(), messages.getMessage(anonymousMessageCode)));
        }
    }
}
