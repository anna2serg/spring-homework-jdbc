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

import ru.homework.domain.Genre;

@Repository
public class GenreDaoJdbc implements GenreDao {

	private final NamedParameterJdbcOperations jdbc;
	
	public GenreDaoJdbc(NamedParameterJdbcOperations jdbc) {
		this.jdbc = jdbc;
	}
	
    private static class GenreMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            int id = resultSet.getInt("genre_id");
            String name = resultSet.getString("name");
            return new Genre(id, name);
        }
    }	
	
	@Override
	public int count() {
		final HashMap<String, Object> params = new HashMap<>(0);
		return jdbc.queryForObject("select count(*) from genres", params, int.class);
	}

	@Override
	public int insert(Genre genre) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
    	final HashMap<String, Object> params = new HashMap<>(1);
    	params.put("name", genre.getName());  
    	jdbc.update("insert into genres(name) values(:name)", new MapSqlParameterSource(params), keyHolder);
    	Number key = keyHolder.getKey(); 
    	genre.setId(key.intValue());
    	return genre.getId();
	}

	@Override
	public void update(Genre genre) {
    	final HashMap<String, Object> params = new HashMap<>(2);
    	params.put("id", genre.getId());  
    	params.put("name", genre.getName()); 
    	jdbc.update("update genres set name = :name where genre_id=:id ", new MapSqlParameterSource(params)); 	
	}
	
	@Override
	public Genre getById(int id) {
		try {
	    	final HashMap<String, Object> params = new HashMap<>(1);
	    	params.put("id", id); 
			return jdbc.queryForObject("select * from genres where genre_id = :id", params, new GenreMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}		
	}
	
	@Override
	public Genre getByName(String name) {	
		try {
	    	final HashMap<String, Object> params = new HashMap<>(1);
	    	params.put("name", name.trim().toLowerCase()); 		
	    	return jdbc.queryForObject("select * from genres where lower(name) = :name", params, new GenreMapper());			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}		
	}

	@Override
	public List<Genre> getAll(HashMap<String, String> filters) {
		String sqlAllGenre = "select genre_id, name from genres ";
		String sqlWhere = "";
		String sqlOrder = " order by name ";
		final HashMap<String, Object> params = new HashMap<>(0);
		if (filters.get("name") != null && !filters.get("name").toString().isEmpty()) {
			sqlWhere = " where lower(name) like '%" + filters.get("name") + "%' ";		
		} 	 
		return jdbc.query(sqlAllGenre + sqlWhere + sqlOrder, params, new GenreMapper());
	}

	@Override
	public void deleteById(int id) {
    	final HashMap<String, Object> params = new HashMap<>(1);
    	params.put("id", id);    	
    	jdbc.update("delete from genres where genre_id = :id", params);
	}

}
