package com.openclassrooms.etudiant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtEncoder);
    }

    @Test
    void test_generate_token_success() {
        // GIVEN
        UserDetails userDetails = User.builder()
                .username("john")
                .password("password")
                .authorities("USER")
                .build();

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("fake-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockJwt);

        // WHEN
        String token = jwtService.generateToken(userDetails);

        // THEN
        assertThat(token).isEqualTo("fake-token");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }
}