package com.imc.crudclient.services;

import com.imc.crudclient.dto.ClientDTO;
import com.imc.crudclient.entities.Client;
import com.imc.crudclient.repositories.ClientRepository;
import com.imc.crudclient.services.exceptions.DatabaseException;
import com.imc.crudclient.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;
    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
        Page<Client> list = clientRepository.findAll(pageRequest);
        return list.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Optional<Client> obj = clientRepository.findById(id);
        Client client = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found."));
        return new ClientDTO(client);
    }

    @Transactional
    public ClientDTO create(ClientDTO clientDTO) {
        Client entity = new Client();
        copyDtoToEntity(clientDTO, entity);
        entity = clientRepository.save(entity);
        return new ClientDTO(entity);
    }
    @Transactional
    public ClientDTO update(Long id, ClientDTO clientDTO){
        try {
            Client entity = clientRepository.getReferenceById(id);
            copyDtoToEntity(clientDTO, entity);
            entity = clientRepository.save(entity);
            return new ClientDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id: " + id + " not found to update!");
        }
    }
    public void delete(Long id) {
        try{
            clientRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id: " + id + " not found to delete!");
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation!");
        }
    }
    private void copyDtoToEntity(ClientDTO clientDTO, Client entity) {
        entity.setCpf(clientDTO.getCpf());
        entity.setChildren(clientDTO.getChildren());
        entity.setName(clientDTO.getName());
        entity.setIncome(clientDTO.getIncome());
        entity.setBirthDate(entity.getBirthDate());
    }
}
