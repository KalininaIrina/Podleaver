package com.podlive.tracker.common.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@AllArgsConstructor
public class CrudService<T, ID> {
    CrudRepository<T, ID> repository;
    public List<T> getAll() {return IterableUtils.toList(repository.findAll());}
    public T getById(ID id) {return repository.findById(id).orElseThrow(EntityNotFoundException::new);}
    public T save(T entity) {return repository.save(entity);}
    public void delete(ID id) {repository.deleteById(id);}
}
