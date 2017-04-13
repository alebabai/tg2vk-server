package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.service.VkService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.net.URISyntaxException;

@RepositoryRestController
@RequestMapping("/users/authorize")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class UserAuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationController.class);

    private final VkService vkService;
    private final UserService userService;

    @Autowired
    public UserAuthorizationController(VkService vkService, UserService userService) {
        this.vkService = vkService;
        this.userService = userService;
    }

    @PostMapping(value = "/code")
    public ResponseEntity<Resource<User>> authorize(@RequestParam String code, Authentication auth, PersistentEntityResourceAssembler asm) {
        return vkService.authorize(code)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken(), asm))
                .orElseThrow(() -> new IllegalArgumentException("Wrong vk authorization code!"));
    }

    @PostMapping(value = "/implicit")
    public ResponseEntity<Resource<User>> authorize(@RequestParam Integer vkId, @RequestParam String vkToken, Authentication auth, PersistentEntityResourceAssembler asm) {
        return vkService.authorize(vkId, vkToken)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken(), asm))
                .orElseThrow(() -> new IllegalArgumentException("Wrong userId or token!"));
    }

    private ResponseEntity<Resource<User>> processAuthorization(Integer tgId, Integer vkId, String vkToken, PersistentEntityResourceAssembler asm) {
        try {
            final User user = userService.createOrUpdate(tgId, vkId, vkToken);
            LOGGER.debug("User successfully created {}", user);
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
