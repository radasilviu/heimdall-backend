package com.antonio.authserver.service;

import com.antonio.authserver.configuration.constants.ErrorMessage;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.mapper.ClientMapper;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.ClientRepository;
import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.utils.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private ClientRepository clientRepository;
    private JwtService jwtService;
    private ClientMapper clientMapper;
    private RealmRepository realmRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, JwtService jwtService, ClientMapper clientMapper, RealmRepository realmRepository) {
        this.clientRepository = clientRepository;
        this.jwtService = jwtService;
        this.clientMapper = clientMapper;
        this.realmRepository = realmRepository;
    }

    public List<ClientDto> getAllClients(String realmName) {
        return clientMapper.toClientDtoList(clientRepository.findAllByRealmName(realmName));
    }

    public ClientDto getClientBySecretAndNameWithRealm(String realmName, String clientName, String clientSecret) {
        Client client = clientRepository.findByClientNameAndClientSecretAndRealmName(clientName, clientSecret, realmName).orElseThrow(() -> new CustomException(
                ErrorMessage.INVALID_CLIENT.getMessage(), HttpStatus.NOT_FOUND));
        return clientMapper.toClientDto(client);
    }

    public ClientDto getClientByName(String realmName, String clientName) throws CustomException {
        Client client = clientRepository.findByClientNameAndRealmName(clientName, realmName).orElseThrow(() -> new CustomException(
                "Client with the name [ " + clientName + " ] could not be found!", HttpStatus.NOT_FOUND));
        return clientMapper.toClientDto(client);

    }

    public void saveClient(String realmName, ClientDto client) throws CustomException {
        client.setClientName(client.getClientName().replaceAll("\\s+", ""));
        Optional<Client> byClientName = clientRepository.findByClientNameAndRealmName(client.getClientName(), realmName);
        if (byClientName.isPresent())
            throw new CustomException(
                    "Client with the name [ " + byClientName.get().getClientName() + " ] already exists!",
                    HttpStatus.CONFLICT);
        else if (client.getClientName().equals("")) {
            throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
        } else {
            client.setRealm(realmRepository.findByName(realmName).get());
            clientRepository.save(clientMapper.toClientDao(client));
            corsConfigurationSource();
        }
    }

    public void updateClientByName(String realmName, String name, ClientDto clientDto) throws CustomException {
        clientDto.setClientName(clientDto.getClientName().replaceAll("\\s+", ""));
        Client client = clientRepository.findByClientNameAndRealmName(name, realmName)
                .orElseThrow(() -> new CustomException("Client with the name [ " + name + " ] could not be found!",
                        HttpStatus.NOT_FOUND));
        if (clientDto.getClientName().equals(""))
            throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
        client.setClientName(clientDto.getClientName());
        client.setFrontendUrl(clientDto.getFrontendUrl());
        client.setBackendUrl(clientDto.getBackendUrl());
        clientRepository.save(client);
        corsConfigurationSource();
    }

    public void deleteClientByName(String realmName, String clientName) throws CustomException {
        Client client = clientRepository.findByClientNameAndRealmName(clientName, realmName).orElseThrow(() -> new CustomException(
                "Client with the name [ " + clientName + " ] could not be found!", HttpStatus.NOT_FOUND));
        clientRepository.delete(client);
    }

    public Code generateCode(AppUserDto user) {

        final Code code = createOauthCode(user);

        return code;
    }


    private Code createOauthCode(AppUserDto user) {
        String jwtCode = generateTokenCode(user);

        if (!jwtCode.equals("")) {
            final Code code = new Code(jwtCode);
            return code;
        }
        return null;
    }

    private String generateTokenCode(AppUserDto userDto) {

        long expirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;

        String token = jwtService.createAccessToken(userDto, expirationTime, new ArrayList<>(),
                SecurityConstants.TOKEN_SECRET);

        return token;
    }

    public List<String> setCorsUrls() {
        List<Client> clients = clientRepository.findAll();
        List<String> urls = new ArrayList<>();

        for (Client client : clients) {
            urls.add(client.getBackendUrl());
            urls.add(client.getFrontendUrl());
        }
        urls.add("http://localhost:4201");

        return urls;
    }


    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(setCorsUrls());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "whitelist", "X-Requested-With", "Origin", "Authorization", "Accept-Encoding", "X-Auth-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }




}
