package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.dto.CodeAuthPayload;
import com.github.alebabai.tg2vk.dto.ImplicitAuthPayload;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.core.UserService;
import com.github.alebabai.tg2vk.service.vk.VkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.net.URISyntaxException;

@RepositoryRestController
@RequestMapping("/users/authorize")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class UserAuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationController.class);

    private final UserService userService;
    private final VkService vkService;
    private final MessageFlowManager messageFlowManager;

    @Autowired
    public UserAuthorizationController(UserService userService, VkService vkService, MessageFlowManager messageFlowManager) {
        this.userService = userService;
        this.vkService = vkService;
        this.messageFlowManager = messageFlowManager;
    }

    @PostMapping(value = "/code")
    public ResponseEntity<Resource<User>> authorize(@RequestBody CodeAuthPayload payload, Authentication auth, PersistentEntityResourceAssembler asm) {
        return vkService.authorize(payload.code())
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken(), asm))
                .orElseThrow(() -> new IllegalArgumentException("Wrong vk authorization code!"));
    }

    @PostMapping(value = "/implicit")
    public ResponseEntity<Resource<User>> authorize(@RequestBody ImplicitAuthPayload payload, Authentication auth, PersistentEntityResourceAssembler asm) {
        return vkService.authorize(payload.vkId(), payload.vkToken())
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken(), asm))
                .orElseThrow(() -> new IllegalArgumentException("Wrong userId or token!"));
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Resource<User>> processAuthorization(Integer tgId, Integer vkId, String vkToken, PersistentEntityResourceAssembler asm) {
        try {
            final User user = userService.createOrUpdate(tgId, vkId, vkToken);
            LOGGER.debug("User successfully created {}", user);
            messageFlowManager.stop(user);
            final PersistentEntityResource resource = asm.toFullResource(user);
            return ResponseEntity
                    .created(new URI(resource.getLink("self").getHref()))
                    .body((Resource) resource);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Can't resolve user URI", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error happened during user authorization", e);
        }
    }
}
