package com.github.alebabai.tg2vk.service.telegram.update.command.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.core.PathResolver;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommandProcessor;
import com.github.alebabai.tg2vk.service.vk.VkService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
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

import static com.github.alebabai.tg2vk.util.constants.CommandConstants.*;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Service
public class TelegramCommandProcessorImpl implements TelegramCommandProcessor {

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final VkService vkService;
    private final MessageFlowManager vkMessageProcessor;
    private final PathResolver pathResolver;
    private final JwtTokenFactoryService tokenFactory;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramCommandProcessorImpl(UserRepository userRepository,
                                        PathResolver pathResolver,
                                        TelegramService tgService,
                                        VkService vkService,
                                        MessageFlowManager vkMessageProcessor,
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
    public void process(TelegramCommand command) {
        switch (command.name()) {
            case COMMAND_LOGIN:
                processLoginCommand(command.context());
                break;
            case COMMAND_START:
                processStartCommand(command.context());
                break;
            case COMMAND_STOP:
                processStopCommand(command.context());
                break;
            case COMMAND_LINK:
                processLinkCommand(command.context(), command.args());
                break;
            case COMMAND_UNLINK:
                processUnlinkCommand(command.context());
                break;
            case COMMAND_SETTINGS:
                processSettingsCommand(command.context());
                break;
            default:
                processUnknownCommand(command.context());
                break;
        }
    }

    private void processLoginCommand(Message context) {
        final SendMessage loginMessage = of(context.chat().type())
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
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
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
        tgService.send(message);
    }

    private void processUnlinkCommand(Message context) {
        final Integer tgChatId = Math.toIntExact(context.chat().id());
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    user.setTempTgChatId(Math.toIntExact(tgChatId));
                    final List<Integer> vkChatIds = user.getChatsSettings().parallelStream()
                            .filter(chatSettings -> Objects.equals(chatSettings.getTgChatId(), tgChatId))
                            .map(ChatSettings::getVkChatId)
                            .collect(toList());
                    final InlineKeyboardButton[] buttons = of(vkChatIds)
                            .filter(ids -> !ids.isEmpty())
                            .map(ids -> vkService.resolveChats(user).parallelStream()
                                    .filter(chat -> ids.contains(chat.getId()))
                                    .map(chat -> {
                                        final String data = String.join("|", "unlink", chat.getId().toString(), tgChatId.toString());
                                        return new InlineKeyboardButton(chat.getTitle()).callbackData(data);
                                    })
                                    .collect(toList())
                                    .toArray(new InlineKeyboardButton[0]))
                            .orElse(new InlineKeyboardButton[0]);
                    final String code = vkChatIds.isEmpty() ? "tg.command.unlink.msg.no_links" : "tg.command.unlink.msg.info";
                    return new SendMessage(tgChatId, messages.getMessage(code))
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(new InlineKeyboardMarkup(buttons));
                })
                .orElseGet(() -> new SendMessage(tgChatId, messages.getMessage("tg.command.unlink.msg.denied")));
        tgService.send(message);
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
