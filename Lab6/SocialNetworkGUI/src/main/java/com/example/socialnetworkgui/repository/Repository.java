package com.example.socialnetworkgui.repository;

import com.example.socialnetworkgui.exceptions.RepositoryException;

import java.util.List;

public interface Repository<E, ID> {
    int size();
    List<E> getAll();
    void add(E entity) throws RepositoryException;
    void remove(E entity) throws RepositoryException;
    E find(ID id) throws RepositoryException;
}
