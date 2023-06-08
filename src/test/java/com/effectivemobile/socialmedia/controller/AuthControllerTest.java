package com.effectivemobile.socialmedia.controller;

import com.effectivemobile.socialmedia.dto.userEntity.UserEntityLoginRequest;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRegistrationRequest;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.security.JWTUtil;
import com.effectivemobile.socialmedia.security.MyUserDetailsService;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserEntityService userEntityService;

    @MockBean
    private UserEntityMapper userEntityMapper;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterHandler() throws Exception {
        UserEntityRegistrationRequest request = new UserEntityRegistrationRequest();
        request.setUserName("Oleg");
        request.setEmail("oleg@gmail.com");
        request.setPassword("testpass");

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setId(1L);
        newUserEntity.setEmail(request.getEmail());

        when(userEntityService.registerUserEntity(any(UserEntityRegistrationRequest.class))).thenReturn(newUserEntity);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"userName\": \"Oleg\",\n" +
                                "    \"email\": \"oleg@gmail.com\",\n" +
                                "    \"password\": \"testpass\"\n" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(status().isCreated());

        verify(userEntityService, times(1)).registerUserEntity(any(UserEntityRegistrationRequest.class));
        verify(userEntityMapper, times(1)).toResponseDTO(newUserEntity);
    }

    @Test
    public void testLoginHandler() throws Exception {
        UserEntityLoginRequest request = new UserEntityLoginRequest();
        request.setEmail("oleg@gmail.com");
        request.setPassword("testpass");

        String expectedToken = jwtUtil.generateToken(request.getEmail());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("oleg@gmail.com", "testpass");

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"oleg@gmail.com\",\"password\":\"testpass\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String actualToken = result.getResponse().getContentAsString();

        assertEquals(expectedToken, actualToken);

        verify(authenticationManager, times(1)).authenticate(authenticationToken);
    }
}

