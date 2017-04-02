package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RepositoryRestResource(path = "users", collectionResourceRel = "users", itemResourceRel = "user")
public interface UserRestRepository extends UserRepository {
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER') and #user?.tgId == principal")
    @Override
    <S extends User> S save(@Param("user") S user);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostAuthorize("returnObject?.tgId == principal")
    @Override
    User findOne(Integer id);
}
