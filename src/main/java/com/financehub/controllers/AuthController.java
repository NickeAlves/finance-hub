package com.financehub.controllers;

import com.financehub.dtos.LoginRequestDTO;
import com.financehub.dtos.RegisterRequestDTO;
import com.financehub.dtos.ResponseDTO;
import com.financehub.models.Client;
import com.financehub.repositories.ClientRepository;
import com.financehub.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(ClientRepository clientRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        Optional<Client> optionalUser = clientRepository.findByEmail(body.email());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("User not found", null, null));
        }

        Client client = optionalUser.get();

        if (!passwordEncoder.matches(body.password(), client.getPassword())) {
            return ResponseEntity.status(401).body(new ResponseDTO("Invalid credentials", null, null));
        }

        String token = tokenService.generateToken(client);

        return ResponseEntity.ok(new ResponseDTO(client.getName(), token, client.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequestDTO body) {
        if (clientRepository.findByEmail(body.email()).isPresent()) {
            return ResponseEntity.status(400).body(new ResponseDTO("Email already registered", null, null));
        }
        Client newClient = new Client();
        newClient.setName(body.name());
        newClient.setEmail(body.email());
        newClient.setPassword(passwordEncoder.encode(body.password()));

        clientRepository.save(newClient);

        String token = tokenService.generateToken(newClient);

        return ResponseEntity.ok(new ResponseDTO(newClient.getName(), token, newClient.getId().toString()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getUserDetails(@PathVariable String clientId) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);

        if (optionalClient.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("User not found", null, null));
        }

        Client client = optionalClient.get();

        String token = tokenService.generateToken(client);

        return ResponseEntity.ok(new ResponseDTO(client.getName(), token, client.getId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout (@RequestHeader(name = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body(new ResponseDTO("Invalid token format", null, null));
        }
        String authToken = token.substring(7);
        return ResponseEntity.ok(new ResponseDTO("Logged out successfully", null, null));
    }

}
