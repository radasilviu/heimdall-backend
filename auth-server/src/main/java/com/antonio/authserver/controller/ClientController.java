package com.antonio.authserver.controller;

import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
@CrossOrigin
public class ClientController {


    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> saveClient(@RequestBody ClientDto client) {

        clientService.saveClient(client);
        final ResponseMessage responseMessage = new ResponseMessage("Client successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }
    @DeleteMapping("/{clientName}")
    public ResponseEntity<ResponseMessage> deleteClient(@PathVariable String clientName) {
        clientService.deleteClientByName(clientName);
        final ResponseMessage responseMessage = new ResponseMessage("Client successfully deleted");
        return ResponseEntity.ok().body(responseMessage);
    }

    @GetMapping
    public List<ClientDto> getAllClients() {
//        return ResponseEntity.ok().body(clients);
        return clientService.getAllClients();
    }

    @GetMapping("/{clientName}")
    public ResponseEntity<ClientDto> getClientByName(@PathVariable String clientName) {
        return ResponseEntity.ok().body(clientService.getClientByName(clientName));

}
}
