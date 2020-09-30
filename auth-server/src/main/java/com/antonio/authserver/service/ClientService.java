package com.antonio.authserver.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.mapper.ClientMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.ClientRepository;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	public List<ClientDto> getAllClients() {
		System.out.println(clientRepository.findAll());
		return ClientMapper.INSTANCE.toClientDtoList(clientRepository.findAll());
	}

	public ClientDto getClientByName(String clientName) throws CustomException {
		Client client = clientRepository.findByClientName(clientName).orElseThrow(() -> new CustomException(
				"Client with the name [ " + clientName + " ] could not be found!", HttpStatus.NOT_FOUND));
		return ClientMapper.INSTANCE.toClientDto(client);

	}

	public void saveClient(ClientDto client) throws CustomException {
		client.setClientName(client.getClientName().replaceAll("\\s+", ""));
		Optional<Client> byClientName = clientRepository.findByClientName(client.getClientName());
		if (byClientName.isPresent())
			throw new CustomException(
					"Client with the name [ " + byClientName.get().getClientName() + " ] already exists!",
					HttpStatus.CONFLICT);
		else if (client.getClientName().equals("")) {
			throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
		} else {

			clientRepository.save(ClientMapper.INSTANCE.toClientDao(client));
		}
	}

	public void updateClientByName(String name, ClientDto clientDto) throws CustomException {
		clientDto.setClientName(clientDto.getClientName().replaceAll("\\s+", ""));
		Client client = clientRepository.findByClientName(name)
				.orElseThrow(() -> new CustomException("Client with the name [ " + name + " ] could not be found!",
						HttpStatus.NOT_FOUND));
		if (clientDto.getClientName().equals(""))
			throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
		client.setClientName(clientDto.getClientName());
		clientRepository.save(client);
	}

	public void deleteClientByName(String clientName) throws CustomException {
		Client client = clientRepository.findByClientName(clientName).orElseThrow(() -> new CustomException(
				"Client with the name [ " + clientName + " ] could not be found!", HttpStatus.NOT_FOUND));
		clientRepository.delete(client);
	}

}
