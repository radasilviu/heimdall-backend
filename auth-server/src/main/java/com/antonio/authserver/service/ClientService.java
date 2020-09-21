package com.antonio.authserver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.mapper.ClientMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.ClientAlreadyExists;
import com.antonio.authserver.model.exceptions.controllerexceptions.ClientNotFound;
import com.antonio.authserver.model.exceptions.controllerexceptions.NullResource;
import com.antonio.authserver.repository.ClientRepository;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientMapper clientMapper;

	public void saveClient(ClientDto client) throws ClientAlreadyExists, NullResource {
		Optional<Client> byClientName = clientRepository.findByClientName(client.getClientName());
		if (byClientName.isPresent())
			throw new ClientAlreadyExists(client.getClientName());
		else if (client.getClientName().trim().equals("")) {
			throw new NullResource("Client");
		} else {
			clientRepository.save(clientMapper.toClientDao(client));
		}
	}

	public void deleteClientByName(String clientName) throws ClientNotFound {
		Client client = clientRepository.findByClientName(clientName).orElseThrow(() -> new ClientNotFound(clientName));
		clientRepository.delete(client);
	}

	public ClientDto getClientByName(String clientName) throws ClientNotFound {
		Client client = clientRepository.findByClientName(clientName).orElseThrow(() -> new ClientNotFound(clientName));
		return clientMapper.toClientDto(client);

	}

	public List<ClientDto> getAllClients() {
		System.out.println(clientRepository.findAll());
		return clientMapper.toClientDtoList(clientRepository.findAll());
	}

}
