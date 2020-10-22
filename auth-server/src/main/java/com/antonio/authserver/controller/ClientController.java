package com.antonio.authserver.controller;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/client")
public class ClientController {

	private ClientService clientService;
	@Autowired
	ConfigurableEnvironment environment;

	@Autowired
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@GetMapping("/{realmName}")
	public List<ClientDto> getAllClients(@PathVariable String realmName) {

		List<ClientDto> clients = clientService.getAllClients(realmName);
		return clients;
	}
	@GetMapping("/{realmName}/{clientName}")
	public ResponseEntity<ClientDto> getClientByName(@PathVariable String realmName,@PathVariable String clientName) {
		ClientDto client = clientService.getClientByName(realmName,clientName);
		return ResponseEntity.ok().body(client);
	}
	@PostMapping("/{realmName}")
	public ResponseEntity<ResponseMessage> saveClient(@PathVariable String realmName,@RequestBody ClientDto client) {
		clientService.saveClient(realmName,client);
		final ResponseMessage responseMessage = new ResponseMessage("Client successfully saved");
		return ResponseEntity.ok().body(responseMessage);
	}

	@PutMapping("/{realmName}/{clientName}")
	public ResponseEntity<ResponseMessage> updateClientByName(@PathVariable String realmName,@PathVariable String clientName,
			@RequestBody ClientDto clientDto) {
		clientService.updateClientByName(realmName,clientName, clientDto);
		final ResponseMessage responseMessage = new ResponseMessage("Client successfully updated");
		return ResponseEntity.ok().body(responseMessage);
	}

	@DeleteMapping("/{realmName}/{clientName}")
	public ResponseEntity<ResponseMessage> deleteClient(@PathVariable String realmName,@PathVariable String clientName) {
		clientService.deleteClientByName(realmName,clientName);
		final ResponseMessage responseMessage = new ResponseMessage("Client successfully deleted");
		return ResponseEntity.ok().body(responseMessage);
	}


}
