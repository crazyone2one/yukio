package cn.master.yukio.security;

import cn.master.yukio.entity.User;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = QueryChain.of(User.class).where(USER.NAME.eq(username)).oneOpt().orElseThrow(
                () -> new UsernameNotFoundException(Translator.get("user.not.exist"))
        );
        List<GrantedAuthority> authorities = new ArrayList<>();
        CustomUserDetails customUserDetails = new CustomUserDetails(user, authorities);
        try {
            new AccountStatusUserDetailsChecker().check(customUserDetails);
        } catch (AccountStatusException exception) {
            log.error("Could not authenticate user", exception);
            throw new RuntimeException(exception.getMessage());
        }
        return customUserDetails;
    }
}
