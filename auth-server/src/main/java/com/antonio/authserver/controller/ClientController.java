package com.antonio.authserver.controller;

import com.antonio.authserver.entity.Client;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
@CrossOrigin
public class ClientController {

    private ClientService clientService;


    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();

        return ResponseEntity.ok().body(clients);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable Long clientId) {
        Client client = clientService.getClientById(clientId);

        return ResponseEntity.ok().body(client);
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> saveClient(@RequestBody Client client) {

        clientService.saveClient(client);
        final ResponseMessage responseMessage = new ResponseMessage("Client successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<ResponseMessage> deleteClient(@PathVariable Long clientId) {
        clientService.deleteClientById(clientId);
        final ResponseMessage responseMessage = new ResponseMessage("Client successfully deleted");
        return ResponseEntity.ok().body(responseMessage);
    }
}
