package com.example.hamro_barber.email.token;

import com.example.hamro_barber.model.User;
import com.example.hamro_barber.repository.UserRepository;
import com.example.hamro_barber.service.serviceImpl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;


    public void saveConfirmationToken(ConfirmationToken token) {

        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now()
        );
    }

    public String confirmToken(String token) {
        ConfirmationToken confirmationToken =
                getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        setConfirmedAt(token);
//        appUserService.enableAppUser(
//                confirmationToken.getAppUser().getEmail()
//        );

        User user = confirmationToken.getAppUser();
        user.setEnabled(true);
        userRepository.save(user);
        return "USER ENABLED";
    }
}
