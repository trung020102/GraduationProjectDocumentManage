package com.doc.mamagement.security.auth_user;

import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.security.SecurityBeanName;
import com.doc.mamagement.security.UserPrincipal;
import com.doc.mamagement.user.UserRepository;
import com.doc.mamagement.web.exception.NoPermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;


@Service(SecurityBeanName.USER_SECURITY_SERVICE)
@RequiredArgsConstructor
public class AuthUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("message.fail.user.find")))
                .block();

        if (user.getIsDisabled())
            throw new NoPermissionException("error.403");

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getCode())
        );

        return new UserPrincipal(user.getId(), user.getUsername(), user.getPasswordHash(), authorities);
    }
}
