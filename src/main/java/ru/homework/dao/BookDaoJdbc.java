package ru.homework.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.homework.domain.Author;
import ru.homework.domain.Book;
import ru.homework.domain.Genre;

@Repository
public class BookDaoJdbc implements BookDao {

	private final NamedParameterJdbcOperations jdbc;
	
	private final String sqlAllBooks = 
			   "select b.book_id, b.name as book_name, "
  			 + "a.author_id, a.surname, a.firstname, a.middlename, "
  			 + "g.genre_id, g.name as genre_name "
  			 + "from books b "
  			 + "left join books_authors ba on ba.book_id = b.book_id "
  			 + "left join authors a on ba.author_id = a.author_id "
  			 + "left join genres g on g.genre_id = b.genre_id ";
	
	private final String sqlOrder = "order by b.book_id, a.author_id";
	
	public BookDaoJdbc(NamedParameterJdbcOperations jdbc) {
		this.jdbc = jdbc;
	}
	
	private static final class BookMapExtractor implements ResultSetExtractor<List<Book>> {

		@Override
		public List<Book> extractData(ResultSet rs) throws SQLException {
			List<Book> books = new ArrayList<>();
			Book book = null;
			while (rs.next()) {
				int book_id = rs.getInt("book_id");
				String book_name = rs.getString("book_name");
				int author_id = rs.getInt("author_id");
				String author_surname = rs.getString("surname");
				String author_firstname = rs.getString("firstname");
				String author_middlename = rs.getString("middlename");
				int genre_id = rs.getInt("genre_id");
				String genre_name = rs.getString("genre_name");
				if ((book == null) || (book.getId() != book_id )) {
					List<Author> authors = new ArrayList<>();
					authors.add(new Author(author_id, author_surname, author_firstname, author_middlename));
					Genre genre = new Genre(genre_id, genre_name);
					book = new Book(book_id, book_name, authors, genre);
					books.add(book);					
				} else {
					List<Author> authors = book.getAuthors();
					authors.add(new Author(author_id, author_surname, author_firstname, author_middlename));
				}
	        }
	        return books;
		}		
	}	
	
	@Override
	public int count() {
		final HashMap<String, Object> params = new HashMap<>(0);
		return jdbc.queryForObject("select count(*) from books", params, int.class);
	}

	private void fillAuthors(Book book, boolean isRefill) {
		final HashMap<String, Object> params = new HashMap<>(2);
		if (isRefill) {
        	params.clear();
        	params.put("book_id", book.getId());			
	    	jdbc.update("delete from books_authors where book_id = :book_id ", new MapSqlParameterSource(params));			
		}
    	for (Author author : book.getAuthors()) {
        	params.clear();
        	params.put("book_id", book.getId());
        	params.put("author_id", author.getId());    		
        	jdbc.update("insert into books_authors(book_id, author_id) values(:book_id, :author_id)", new MapSqlParameterSource(params));
    	}
	}
	
	@Override
	public int insert(Book book) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
    	final HashMap<String, Object> params = new HashMap<>(2);
    	params.put("name", book.getName());  
    	params.put("genre_id", book.getGenre().getId());
    	jdbc.update("insert into books(name, genre_id) "
    			  + "values(:name, :genre_id)", 
    			  new MapSqlParameterSource(params), keyHolder);
    	Number key = keyHolder.getKey(); 
    	book.setId(key.intValue());
    	fillAuthors(book, false);
    	return book.getId();
	}
	
	@Override
	public void update(Book book) {
    	final HashMap<String, Object> params = new HashMap<>(4);
    	params.put("id", book.getId());  
    	params.put("name", book.getName()); 
    	params.put("genre_id", book.getGenre().getId());  	
    	String sql = "update books "
	    		   + "set name = :name, genre_id = :genre_id "
	    		   + "where book_id = :id ";
    	jdbc.update(sql, new MapSqlParameterSource(params)); 
    	fillAuthors(book, true);
	}

	@Override
	public Book getById(int id) {
		try {
	    	String s = sqlAllBooks + " where b.book_id = :id";
	    	final HashMap<String, Object> params = new HashMap<>(1);
	    	params.put("id", id);         	
	    	List<Book> books = jdbc.query(s, params, new BookMapExtractor());
	    	return (books.size()>0) ? books.get(0) : null;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Book> getAll(HashMap<String, String> filters) {
		List<Book> result = null; 
		String sqlWhere = "";
		if (filters.get("name") != null && !filters.get("name").toString().isEmpty()) {
			sqlWhere = sqlWhere.equals("") ? " where " : " and ";
			sqlWhere += " lower(b.name) like '%" + filters.get("name").trim().toLowerCase() + "%' ";
		}
		if (filters.get("author") != null && !filters.get("author").toString().isEmpty()) {
			sqlWhere = sqlWhere.equals("") ? " where " : sqlWhere + " and ";
			sqlWhere += " (lower(a.surname) like '%" + filters.get("author").trim().toLowerCase() + "%' ";
			sqlWhere += "or lower(a.firstname) like '%" + filters.get("author").trim().toLowerCase() + "%' ";
			sqlWhere += "or lower(a.middlename) like '%" + filters.get("author").trim().toLowerCase() + "%') ";
		}	
		if (filters.get("genre") != null && !filters.get("genre").toString().isEmpty()) {
			sqlWhere = sqlWhere.equals("") ? " where " : sqlWhere + " and ";
			sqlWhere += " lower(g.name) like '%" + filters.get("genre").trim().toLowerCase() + "%' ";
		}		
		if (filters.get("authorId") != null && !filters.get("authorId").toString().isEmpty()) {
		     try {  
		    	 int author_id = Integer.parseInt(filters.get("authorId"));  
		         sqlWhere = sqlWhere.equals("") ? " where " : sqlWhere + " and "; 
		         sqlWhere += " ba.author_id = " + author_id + " ";
		      } catch (NumberFormatException e) {  
		         //игнорируем параметр поиска 
		      }  			
		}
		if (filters.get("genreId") != null && !filters.get("genreId").toString().isEmpty()) {
		     try {  
		    	 int genre_id = Integer.parseInt(filters.get("genreId"));  
		         sqlWhere = sqlWhere.equals("") ? " where " : sqlWhere + " and "; 
		         sqlWhere += " g.genre_id = " + genre_id + " ";
		      } catch (NumberFormatException e) {  
		         //игнорируем параметр поиска 
		      }  			
		}					
		String s = sqlAllBooks + sqlWhere + sqlOrder;
		result = jdbc.query(s, new BookMapExtractor());
		return result;
	}
	
	@Override
	public List<Book> getByName(String name) {
		try {
	    	final HashMap<String, Object> params = new HashMap<>(1);
	    	params.put("name", name.trim().toLowerCase());      	
			String sqlWhere = "where lower(b.name) = :name ";
			String s = sqlAllBooks + sqlWhere + sqlOrder;
			return jdbc.query(s, params, new BookMapExtractor());		
		} catch (EmptyResultDataAccessException e) {
			return null;
		}	
	}

	@Override
	public void deleteById(int id) {
    	final HashMap<String, Object> params = new HashMap<>(1);
    	params.put("id", id);  
    	jdbc.update("delete from books_authors where book_id = :id", params);
    	jdbc.update("delete from books where book_id = :id", params);
	}

}
