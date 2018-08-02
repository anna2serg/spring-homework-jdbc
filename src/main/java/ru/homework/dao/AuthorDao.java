package ru.homework.dao;

import java.util.HashMap;
import java.util.List;

import ru.homework.domain.Author;

public interface AuthorDao {
	int count();
	int insert(Author author);
	void update(Author author);
	Author getById(int id);
	List<Author> getByNames(String surname, String firstname, String middlename);
	List<Author> getAll(HashMap<String, String> filters);
	void deleteById(int id);
	
}
