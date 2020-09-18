package com.antonio.authserver.service;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.mapper.ClientMapper;
import com.antonio.authserver.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    public void saveClient(ClientDto client) {
        clientRepository.save(clientMapper.toClientDao(client));
    }

    public void deleteClientByName(String clientName) {
        if (!checkIfClientExist(clientName)) {
            throw new RuntimeException("Client with the name [+ " + clientName + "] doesn't exist");
        }
        clientRepository.delete(clientRepository.findByClientName(clientName).get());
    }


    public ClientDto getClientByName(String clientName) {
        if (!checkIfClientExist(clientName)) {
            throw new RuntimeException("Client with the name [+ " + clientName + "] doesn't exist");
        }

        return clientMapper.toClientDto(clientRepository.findByClientName(clientName).get());

    }

    public List<ClientDto> getAllClients() {
        System.out.println(clientRepository.findAll());
        return clientMapper.toClientDtoList(clientRepository.findAll());
    }

    private boolean checkIfClientExist(String clientName) {
        Optional<Client> clientOptional = clientRepository.findByClientName(clientName);

        return clientOptional.isPresent();

    }
}
