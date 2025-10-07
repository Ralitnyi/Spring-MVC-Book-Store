package com.epam.rd.autocode.spring.project.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService{

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

	@Override
    @Transactional(readOnly = true)
	public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, ClientDTO.class))
                .toList();
	}

	@Override
    @Transactional(readOnly = true)
	public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            throw new NotFoundException("Client not found: " + email);
        }
        return modelMapper.map(client, ClientDTO.class);
	}

	@Override
    @Transactional
	public ClientDTO updateClientByEmail(String email, ClientDTO client) {
        Client existing = clientRepository.findByEmail(email);
        if (existing == null) {
            throw new NotFoundException("Client not found: " + email);
        }
        existing.setEmail(client.getEmail());
        existing.setPassword(client.getPassword());
        existing.setName(client.getName());
        existing.setBalance(client.getBalance());
        Client saved = clientRepository.save(existing);
        return modelMapper.map(saved, ClientDTO.class);
	}

	@Override
    @Transactional
	public void deleteClientByEmail(String email) {
        if (!clientRepository.existsByEmail(email)) {
            throw new NotFoundException("Client not found: " + email);
        }
        clientRepository.deleteByEmail(email);
	}

	@Override
    @Transactional
	public ClientDTO addClient(ClientDTO client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new AlreadyExistException("Client already exists: " + client.getEmail());
        }
        Client entity = modelMapper.map(client, Client.class);
        entity.setPassword(passwordEncoder.encode(client.getPassword()));
        Client saved = clientRepository.save(entity);
        return modelMapper.map(saved, ClientDTO.class);
	}
}