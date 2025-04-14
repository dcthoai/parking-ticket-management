package com.dct.parkingticket.security.service;

import com.dct.parkingticket.constants.ExceptionConstants;
import com.dct.parkingticket.dto.mapping.IAuthenticationDTO;
import com.dct.parkingticket.entity.Account;
import com.dct.parkingticket.repositories.AccountRepository;
import com.dct.parkingticket.repositories.PermissionRepository;
import com.dct.parkingticket.security.model.CustomUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final AccountRepository accountRepository;
    private final PermissionRepository permissionRepository;

    public CustomUserDetailsService(AccountRepository accountRepository,
                                    PermissionRepository permissionRepository) {
        this.accountRepository = accountRepository;
        this.permissionRepository = permissionRepository;
        log.debug("UserDetailsService 'CustomUserDetailsService' is configured for load user credentials info");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Load user by username: " + username);
        Optional<IAuthenticationDTO> authentication = accountRepository.findAuthenticationByUsername(username);

        if (authentication.isEmpty())
            throw new UsernameNotFoundException(ExceptionConstants.ACCOUNT_NOT_FOUND);

        Account account = new Account();
        BeanUtils.copyProperties(authentication.get(), account);
        Set<String> userPermissions = permissionRepository.findAllByAccountId(account.getId());

        Collection<SimpleGrantedAuthority> userAuthorities = userPermissions
            .stream()
            .filter(StringUtils::hasText)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());

        return CustomUserDetails.customBuilder().account(account).authorities(userAuthorities).build();
    }
}
