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

    private ClientMapper clientMapper;

    public void saveClient(ClientDto client) {
        clientRepository.save(clientMapper.toClientDao(client));
    }

    public void deleteClientById(Long clientId) {
        if (!checkIfClientExist(clientId)) {
            throw new RuntimeException("Client with id [+ " + clientId + "] doesn't exist");
        }
        clientRepository.deleteById(clientId);
    }


    public ClientDto getClientById(Long clientId) {
        if (!checkIfClientExist(clientId)) {
            throw new RuntimeException("Client with id [+ " + clientId + "] doesn't exist");
        }

        return clientMapper.toClientDto(clientRepository.findById(clientId).get());

    }

    public List<ClientDto> getAllClients() {
        System.out.println(clientRepository.findAll());
        return clientMapper.toClientDtoList(clientRepository.findAll());
    }

    private boolean checkIfClientExist(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);

        return clientOptional.isPresent();

    }
}
