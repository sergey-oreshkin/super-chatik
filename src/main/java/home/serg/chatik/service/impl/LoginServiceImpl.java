package home.serg.chatik.service.impl;

import home.serg.chatik.context.DaoContext;
import home.serg.chatik.context.TokenContext;
import home.serg.chatik.dao.user.Role;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.dao.user.UserRepository;
import home.serg.chatik.dto.LoginDto;
import home.serg.chatik.dto.TokenDto;
import home.serg.chatik.exception.AuthorizationException;
import home.serg.chatik.service.LoginService;
import home.serg.chatik.service.TokenService;
import home.serg.chatik.util.HTMLFilter;

import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private static final String ADMIN_NAME_PREFIX = "A";

    private UserRepository userRepository = (UserRepository) DaoContext.USER_REPOSITORY.getInstance();
    private TokenService tokenService = TokenContext.TOKEN_SERVICE.getInstance();

    @Override
    public TokenDto login(LoginDto loginDto) {
        String username = HTMLFilter.filter(loginDto.getUsername());
        Optional<User> result = userRepository.findByUsername(username);
        if (result.isEmpty()) {
            Role role = username.startsWith(ADMIN_NAME_PREFIX) ? Role.ADMIN : Role.USER;
            User user = new User(username, loginDto.getPassword(), role, false);
            return tokenService.registerToken(userRepository.save(user));
        } else if (result.get().getPassword().equals(loginDto.getPassword())) {
            return tokenService.registerToken(result.get());
        }
        throw new AuthorizationException("Authorization failed");
    }
}
