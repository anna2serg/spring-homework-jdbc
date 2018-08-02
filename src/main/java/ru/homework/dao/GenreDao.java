package ru.homework.dao;

import java.util.HashMap;
import java.util.List;

import ru.homework.domain.Genre;

public interface GenreDao {
	
	int count();
	int insert(Genre genre);
	void update(Genre genre);
	Genre getById(int id);
	Genre getByName(String name);
	List<Genre> getAll(HashMap<String, String> filters);
	void deleteById(int id);
	
}
