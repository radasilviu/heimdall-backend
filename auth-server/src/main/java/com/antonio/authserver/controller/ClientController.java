package com.antonio.authserver.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.ClientService;

@RestController
@RequestMapping("api/client")
@CrossOrigin
public class ClientController {

	private ClientService clientService;

	@Autowired
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@GetMapping
	public List<ClientDto> getAllClients() {
		List<ClientDto> clients = clientService.getAllClients();
		return clients;
	}

	@GetMapping("/{clientName}")
	public ResponseEntity<ClientDto> getClientByName(@PathVariable String clientName) {
		ClientDto client = clientService.getClientByName(clientName);
		return ResponseEntity.ok().body(client);
	}

	@PostMapping
	public ResponseEntity<ResponseMessage> saveClient(@RequestBody ClientDto client) {
		clientService.saveClient(client);
		final ResponseMessage responseMessage = new ResponseMessage("Client successfully saved");
		return ResponseEntity.ok().body(responseMessage);
	}

	@PutMapping
	public ResponseEntity<ResponseMessage> updateClientByName(@RequestBody ClientDto clientDto) {
		clientService.updateClientByName(clientDto);
		final ResponseMessage responseMessage = new ResponseMessage("Client successfully updated");
		return ResponseEntity.ok().body(responseMessage);
	}

	@DeleteMapping("/{clientName}")
	public ResponseEntity<ResponseMessage> deleteClient(@PathVariable String clientName) {
		clientService.deleteClientByName(clientName);
		final ResponseMessage responseMessage = new ResponseMessage("Client successfully deleted");
		return ResponseEntity.ok().body(responseMessage);
	}
}
