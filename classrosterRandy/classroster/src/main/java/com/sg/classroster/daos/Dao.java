/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.classroster.daos;

import java.util.List;

/**
 *
 * @author rhash
 */
public interface Dao<T> {

    public T Create(T object);

    public T ReadById(int id);

    public List<T> ReadAll();

    public void Update(T object);

    public void Delete(int id);
    
    public List<T> GetAllByForeignId(Class foreignObject, int foreignId);
}
