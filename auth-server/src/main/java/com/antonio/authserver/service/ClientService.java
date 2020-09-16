package com.antonio.authserver.service;

import com.antonio.authserver.entity.Client;
import com.antonio.authserver.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    public void deleteClientById(Long clientId) {
        if (!checkIfClientExist(clientId)) {
            throw new RuntimeException("Client with id [+ " + clientId + "] doesn't exist");
        }
        clientRepository.deleteById(clientId);
    }


    public Client getClientById(Long clientId) {
        if (!checkIfClientExist(clientId)) {
            throw new RuntimeException("Client with id [+ " + clientId + "] doesn't exist");
        }

        return clientRepository.findById(clientId).get();

    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    private boolean checkIfClientExist(Long clientId) {
        Optional<Client> clientOptional = clientRepository.findById(clientId);

        return clientOptional.isPresent();

    }
}
