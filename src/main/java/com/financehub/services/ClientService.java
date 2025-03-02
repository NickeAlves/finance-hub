package com.financehub.services;

import com.financehub.models.Client;
import com.financehub.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllUsers() {
        return clientRepository.findAll();
    }

    public Optional<Client> findClientById(String id) {
        return clientRepository.findById(id);
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> updateClient(String id, Client client) {
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(client.getName());
            existingClient.setEmail(client.getEmail());
            existingClient.setPassword(client.getPassword());
            return clientRepository.save(client);
        });
    }

    public boolean deleteClient(String id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
