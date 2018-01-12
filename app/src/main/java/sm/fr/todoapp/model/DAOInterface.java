package sm.fr.todoapp.model;

import android.database.sqlite.SQLiteException;

import java.util.List;

/**
 * Interface générique pour tous les DAO
 */

interface DAOInterface <DTO>{
    DTO findOneById(int id) throws SQLiteException;

    List<DTO> findAll() throws SQLiteException;

    void deleteOneById(Long id) throws SQLiteException;

    void persist(DTO entity);
}
