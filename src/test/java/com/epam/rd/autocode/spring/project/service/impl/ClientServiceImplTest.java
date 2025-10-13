package com.epam.rd.autocode.spring.project.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail("client@test.com");
        client.setName("Test Client");
        client.setPassword("encodedPassword");
        client.setBalance(BigDecimal.valueOf(100.0));
        client.setRole("CLIENT");
        client.setBlocked(false);

        clientDTO = new ClientDTO("client@test.com", "rawPassword", "Test Client", BigDecimal.valueOf(100.0), false);
    }

    @Test
    void testGetAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        List<ClientDTO> result = clientService.getAllClients();

        assertEquals(1, result.size());
        assertEquals(clientDTO, result.get(0));
    }

    @Test
    void testGetClientByEmail_Success() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.getClientByEmail("client@test.com");

        assertEquals(clientDTO, result);
    }

    @Test
    void testGetClientByEmail_NotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> clientService.getClientByEmail("nonexistent@test.com"));
    }

    @Test
    void testUpdateClientByEmail_Success() {
        Client updatedClient = new Client();
        updatedClient.setEmail("newclient@test.com");
        updatedClient.setName("Updated Client");

        ClientDTO updateDTO = new ClientDTO();
        updateDTO.setEmail("newclient@test.com");
        updateDTO.setName("Updated Client");
        updateDTO.setPassword("newPassword");
        updateDTO.setBalance(BigDecimal.valueOf(150.0));

        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(clientRepository.save(client)).thenReturn(updatedClient);
        when(modelMapper.map(updatedClient, ClientDTO.class)).thenReturn(updateDTO);

        ClientDTO result = clientService.updateClientByEmail("client@test.com", updateDTO);

        assertEquals(updateDTO, result);
        assertEquals("newclient@test.com", client.getEmail());
        assertEquals("encodedNewPassword", client.getPassword());
        assertEquals("Updated Client", client.getName());
        assertEquals(BigDecimal.valueOf(150.0), client.getBalance());
    }

    @Test
    void testUpdateClientByEmail_NotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> clientService.updateClientByEmail("nonexistent@test.com", clientDTO));
    }

    @Test
    void testDeleteClientByEmail_Success() {
        when(clientRepository.existsByEmail("client@test.com")).thenReturn(true);

        clientService.deleteClientByEmail("client@test.com");

        verify(clientRepository).deleteByEmail("client@test.com");
    }

    @Test
    void testDeleteClientByEmail_NotFound() {
        when(clientRepository.existsByEmail("nonexistent@test.com")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> clientService.deleteClientByEmail("nonexistent@test.com"));
    }

    @Test
    void testAddClient_Success() {
        Client savedClient = new Client();
        savedClient.setEmail("newclient@test.com");

        ClientDTO newClientDTO = new ClientDTO();
        newClientDTO.setEmail("newclient@test.com");
        newClientDTO.setPassword("password123");

        when(clientRepository.existsByEmail("newclient@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(modelMapper.map(newClientDTO, Client.class)).thenReturn(savedClient);
        when(clientRepository.save(savedClient)).thenReturn(savedClient);
        when(modelMapper.map(savedClient, ClientDTO.class)).thenReturn(newClientDTO);

        ClientDTO result = clientService.addClient(newClientDTO);

        assertEquals(newClientDTO, result);
        assertEquals(BigDecimal.ZERO, savedClient.getBalance());
        assertEquals("CLIENT", savedClient.getRole());
        assertEquals("encodedPassword", savedClient.getPassword());
        verify(clientRepository).save(savedClient);
    }

    @Test
    void testAddClient_AlreadyExists() {
        when(clientRepository.existsByEmail("client@test.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> clientService.addClient(clientDTO));
    }

    @Test
    void testBlockClient_Success() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        clientService.blockClient("client@test.com");

        assertTrue(client.isBlocked());
        verify(clientRepository).save(client);
    }

    @Test
    void testBlockClient_NotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> clientService.blockClient("nonexistent@test.com"));
    }

    @Test
    void testUnblockClient_Success() {
        client.setBlocked(true);
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        clientService.unblockClient("client@test.com");

        assertFalse(client.isBlocked());
        verify(clientRepository).save(client);
    }

    @Test
    void testUnblockClient_NotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> clientService.unblockClient("nonexistent@test.com"));
    }

    @Test
    void testDeductBalance_Success() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        clientService.deductBalance("client@test.com", BigDecimal.valueOf(50.0));

        assertEquals(BigDecimal.valueOf(50.0), client.getBalance());
        verify(clientRepository).save(client);
    }

    @Test
    void testDeductBalance_InsufficientFunds() {
        client.setBalance(BigDecimal.valueOf(30.0));
        when(clientRepository.findByEmail("client@test.com")).thenReturn(client);

        assertThrows(IllegalArgumentException.class, () -> clientService.deductBalance("client@test.com", BigDecimal.valueOf(50.0)));
    }

    @Test
    void testDeductBalance_NotFound() {
        when(clientRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> clientService.deductBalance("nonexistent@test.com", BigDecimal.valueOf(50.0)));
    }
}
