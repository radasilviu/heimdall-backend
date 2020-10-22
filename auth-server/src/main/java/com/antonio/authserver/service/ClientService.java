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
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService {

    private ClientRepository clientRepository;
    private JwtService jwtService;
    private ClientMapper clientMapper;
    private RealmRepository realmRepository;
    private ConfigurableEnvironment environment;

    @Autowired
    public ClientService(ClientRepository clientRepository, JwtService jwtService, ClientMapper clientMapper, RealmRepository realmRepository, ConfigurableEnvironment environment) {
        this.clientRepository = clientRepository;
        this.jwtService = jwtService;
        this.clientMapper = clientMapper;
        this.realmRepository = realmRepository;
        this.environment = environment;
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
            setClientFrontedURL(realmName, byClientName.get());
        }

    }

    public void updateClientByName(String realmName, String name, ClientDto clientDto) throws CustomException {
        clientDto.setClientName(clientDto.getClientName().replaceAll("\\s+", ""));
        Client client = clientRepository.findByClientNameAndRealmName(name, realmName)
                .orElseThrow(() -> new CustomException("Client with the name [ " + name + " ] could not be found!",
                        HttpStatus.NOT_FOUND));
        if (clientDto.getClientName().equals(""))
            throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
        client.setClientBackendURL(clientDto.getClientBackendURL());
        client.setAuthorizationServerFrontendURL(clientDto.getAuthorizationServerFrontendURL());
        client.setClientFrontendUrl(clientDto.getClientFrontendUrl());
        clientRepository.save(client);
        setClientFrontedURL(realmName, client);
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

    public void setProperties(Client client, String envKey, String envValue) {
        MutablePropertySources propertySources = environment.getPropertySources();
        System.out.println(environment.getProperty(envKey));
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(envKey, envValue);
        propertySources.addFirst(new MapPropertySource("appFile", myMap));
        System.out.println(environment.getProperty(envKey));
    }

    private void setClientFrontedURL(String realm, Client client) {
        Client newClient = clientRepository.findByClientNameAndRealmName(client.getClientName(), realm)
                .orElseThrow(() -> new CustomException("Client with the name [ " + client.getClientName() + " ] could not be found!",
                        HttpStatus.NOT_FOUND));

        MutablePropertySources propertySources = environment.getPropertySources();


        PropertySource propSource =this.environment.getPropertySources().get("applicationConfig: [classpath:/application.properties]");
        Object objs = propSource.getSource();

        Map<String, Object> myMap = new HashMap<>();

        myMap.put("clientFrontedURL", newClient.getClientFrontendUrl());
        propertySources.addFirst(new MapPropertySource("applicationConfig: [classpath:/application.properties]", myMap));
        System.out.println(environment.getProperty("clientFrontedURL"));

        System.out.println(environment.getProperty("clientBackendURL"));
        clientRepository.save(newClient);


    }


}
