package com.epam.rd.autocode.spring.project.service;

import java.util.List;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;

public interface ClientService {

    List<ClientDTO> getAllClients();

    ClientDTO getClientByEmail(String email);

    ClientDTO updateClientByEmail(String email, ClientDTO client);

    void deleteClientByEmail(String email);

    ClientDTO addClient(ClientDTO client);
}
