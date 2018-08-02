package ru.homework.dao;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.homework.domain.Author;

@Repository
public class AuthorDaoJdbc implements AuthorDao {

	private final NamedParameterJdbcOperations jdbc;
	
	public AuthorDaoJdbc(NamedParameterJdbcOperations jdbc) {
		this.jdbc = jdbc;
	}

    private static class AuthorMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("author_id");
            String surname = resultSet.getString("surname");
            String firstname = resultSet.getString("firstname");
            String middlename = resultSet.getString("middlename");
            return new Author(id, surname, firstname, middlename);
        }
    }	
	
	@Override
	public int count() {
		final HashMap<String, Object> params = new HashMap<>(0);
		return jdbc.queryForObject("select count(*) from authors", params, int.class);
	}

	@Override
	public int insert(Author author) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
    	final HashMap<String, Object> params = new HashMap<>(3);
    	params.put("surname", author.getSurname().trim());  
    	params.put("firstname", author.getFirstname().trim());
    	if (author.getMiddlename() == null) params.put("middlename", author.getMiddlename());
    	else params.put("middlename", author.getMiddlename().trim());
    	jdbc.update("insert into authors(surname, firstname, middlename) values(:surname, :firstname, :middlename)", new MapSqlParameterSource(params), keyHolder);
    	Number key = keyHolder.getKey(); 
    	author.setId(key.intValue());
    	return author.getId();
	}
	
	@Override
	public void update(Author author) {
    	final HashMap<String, Object> params = new HashMap<>(4);
    	params.put("id", author.getId());  
    	params.put("surname", author.getSurname().trim()); 
    	params.put("firstname", author.getFirstname().trim());
    	if (author.getMiddlename() == null) params.put("middlename", author.getMiddlename());
    	else params.put("middlename", author.getMiddlename().trim());
    	String sql = "update authors "
    			   + "set surname = :surname, "
    			   + "firstname = :firstname, "
    			   + "middlename = :middlename "
    			   + "where author_id=:id ";
    	jdbc.update(sql, new MapSqlParameterSource(params)); 	
	}
		

	@Override
	public Author getById(int id) {
		try {
	    	final HashMap<String, Object> params = new HashMap<>(1);
	    	params.put("id", id); 
			return jdbc.queryForObject("select * from authors where author_id = :id", params, new AuthorMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}			
	}

	@Override
	public List<Author> getByNames(String surname, String firstname, String middlename) { 
		try {
	    	final HashMap<String, Object> params = new HashMap<>(3);
	    	params.put("surname", surname.trim().toLowerCase()); 	
	    	params.put("firstname", firstname.trim().toLowerCase());
	    	if (middlename!=null) params.put("middlename", middlename.trim().toLowerCase()); 
	    	else params.put("middlename", middlename);
	    	
	    	String sql = "select * from authors "
					 + "where lower(surname) = :surname "
					 + "and lower(firstname) = :firstname ";
	    	sql += (middlename==null) ? " and middlename is null " : " and lower(middlename) = :middlename ";	    	
	    	return jdbc.query(sql, params, new AuthorMapper());			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}			
	}
	
	@Override
	public List<Author> getAll(HashMap<String, String> filters) {
		String sqlAllAuthor = "select author_id, surname, firstname, middlename from authors ";
		String sqlWhere = "";
		String sqlOrder = " order by surname, firstname, middlename ";
		final HashMap<String, Object> params = new HashMap<>(0);
		if (filters.get("name") != null && !filters.get("name").toString().isEmpty()) {
			sqlWhere = " where (lower(surname) like '%" + filters.get("name").trim().toLowerCase() + "%') " +
					   " or (lower(firstname) like '%" + filters.get("name").trim().toLowerCase() + "%') " +
					   " or (lower(middlename) like '%" + filters.get("name").trim().toLowerCase() + "%') ";
		} 	 
		return jdbc.query(sqlAllAuthor + sqlWhere + sqlOrder, params, new AuthorMapper());
	}

	@Override
	public void deleteById(int id) {
    	final HashMap<String, Object> params = new HashMap<>(1);
    	params.put("id", id);    	
    	jdbc.update("delete from authors where author_id = :id", params);
	}

}
