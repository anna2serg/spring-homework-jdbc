package ru.homework.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import ru.homework.dao.AuthorDao;
import ru.homework.dao.BookDao;
import ru.homework.dao.GenreDao;
import ru.homework.domain.Author;
import ru.homework.domain.Book;
import ru.homework.domain.Genre;
import ru.homework.exception.EntityNotFoundException;
import ru.homework.exception.InvalidOperationException;
import ru.homework.exception.InvalidValueFormatException;
import ru.homework.exception.NotUniqueEntityFoundException;

@Service
public class BookcardService {
	
	private final BookDao bookDao;
	private final GenreDao genreDao;
	private final AuthorDao authorDao;

	public BookcardService(AuthorDao authorDao, BookDao bookDao, GenreDao genreDao) {
		this.authorDao = authorDao;
		this.bookDao = bookDao;
		this.genreDao = genreDao;
	}

	private int getId(String id) {
		int result = 0;
		try {  
			result = Integer.parseInt(id);
	    } catch (NumberFormatException e) {  
	    	// 
	    } 
		return result;
	}
	
	public Genre getGenre(String genre) throws EntityNotFoundException {
		Genre result = null;
		int genre_id = getId(genre);		
		if (genre_id == 0) {
			//genre - строка
			result = genreDao.getByName(genre);			
		} else {
			//genre - число 
			result = genreDao.getById(genre_id); 
		}		
		if (result == null) 
			throw new EntityNotFoundException(String.format("Жанр [%s] не найден", genre));		
		return result;
	}	

	public List<Genre> getGenreAll(HashMap<String, String> filters) {
		return genreDao.getAll(filters);
	}	
	
	public Genre addGenre(String name) {
		Genre result = null;
		result = new Genre(name);
		genreDao.insert(result);
		return result;
	}
	
	public Genre editGenre(String genre, String name) throws EntityNotFoundException {
		Genre result = getGenre(genre);
		result.setName(name);
		genreDao.update(result);
		return result;
	}	
	
	private List<String> getAuthorNames(String str) throws InvalidValueFormatException {
		List<String> result = Arrays.asList(str.split(",")); 
		if (result.size() < 2)  
			throw new InvalidValueFormatException(String.format("Неправильно задан автор [%s]", str));
		else return result;
	}
	
	public List<Author> getAuthor(String author) throws EntityNotFoundException, NotUniqueEntityFoundException {
		return getAuthor(author, true);
	}
	
	public List<Author> getAuthor(String author, boolean isSingleValued) throws EntityNotFoundException, NotUniqueEntityFoundException {
		List<Author> result = null;
		int author_id = getId(author);
		if (author_id == 0) {
			//author - строка
			List<String> names;
			try {
				names = getAuthorNames(author);
			} catch (InvalidValueFormatException e) {
				throw new EntityNotFoundException(String.format("Автор [%s] не найден: %s", author, e.getMessage()));
			}
			result = authorDao.getByNames(names.get(0), names.get(1), (names.size() > 2) ? names.get(2) : null);			
		} else {
			//author - число 
			Author authorById = authorDao.getById(author_id);
			if (authorById!=null) {
				result = new ArrayList<Author>();
				result.add(authorById);				
			}			
		}
		if ((result == null) || (result.size() == 0)) 
			throw new EntityNotFoundException(String.format("Автор [%s] не найден", author));
		if (isSingleValued && result.size()>1) 
			throw new NotUniqueEntityFoundException(String.format("Найдено более одного автора [%s]", author));
		return result;
	}		
	
	public List<Author> getAuthorAll(HashMap<String, String> filters) {
		return authorDao.getAll(filters);
	}		
	
	public Author addAuthor(String surname, String firstname, String middlename) {
		Author result = null;
		result = new Author(surname, firstname, middlename);
		authorDao.insert(result);
		return result;		
	}	
	
	public Author editAuthor(String author, HashMap<String, String> values) throws EntityNotFoundException, NotUniqueEntityFoundException {
		Author result = getAuthor(author).get(0);	
		if (values.get("surname")!=null) 
			result.setSurname(values.get("surname"));
		if (values.get("firstname")!=null)
			result.setFirstname(values.get("firstname"));
		if (values.get("middlename")!=null)
			if (values.get("middlename").equals("null")) result.setMiddlename(null); 
			else result.setMiddlename(values.get("middlename"));		
		authorDao.update(result);
		return result;		
	}		
	
	public List<Book> getBookAll(HashMap<String, String> filters) {
		return bookDao.getAll(filters);
	}	
	
	public List<Book> getBook(String book) throws EntityNotFoundException, NotUniqueEntityFoundException {
		return getBook(book, true);
	}	
	
	public List<Book> getBook(String book, boolean isSingleValued) throws EntityNotFoundException, NotUniqueEntityFoundException {
		List<Book> result = null;
		int book_id = getId(book);
		if (book_id == 0) {
			//book - строка
			result = bookDao.getByName(book);
		} else {
			//book - число 
			Book bookById = bookDao.getById(book_id); 
			if (bookById!=null) {
				result = new ArrayList<Book>();
				result.add(bookById);				
			}
		}			
		if ((result == null) || (result.size() == 0)) 
			throw new EntityNotFoundException(String.format("Книга [%s] не найдена", book));
		if (isSingleValued && result.size()>1) 
			throw new NotUniqueEntityFoundException(String.format("Найдено более одной книги [%s]", book));
		return result;
	}		
	
	//обработка жанра
	private Genre getOrAddGenre(String genre) throws EntityNotFoundException {
		Genre result = null;
		try {
			result = getGenre(genre);
		} catch (EntityNotFoundException e) {
			if (getId(genre)!=0) throw e;
			else result = addGenre(genre);
		}	
		return result;
	}
	
	//обработка автора
	private Author getOrAddAuthor(String author) throws EntityNotFoundException, NotUniqueEntityFoundException {
		Author result = null;
		try {
			result = getAuthor(author).get(0);	
		} catch (EntityNotFoundException e) {
			if (getId(author)!=0) throw e;
			else {
				List<String> names;
				try {
					names = getAuthorNames(author);
				} catch (InvalidValueFormatException e1) {
					throw new EntityNotFoundException(String.format("Автор [%s] не найден: %s", author, e.getMessage()));
				}
				result = addAuthor(names.get(0), names.get(1), (names.size() > 2) ? names.get(2) : null);				
			}
		}	
		return result;
	}
	
	public Book addBook(String name, String genre, String author) throws EntityNotFoundException, NotUniqueEntityFoundException {	
		Book result = null;
		
		Genre book_genre = getOrAddGenre(genre);
		Author book_author = getOrAddAuthor(author); 	

		List<Author> authors = new ArrayList<Author>();
		authors.add(book_author);
		
		result = new Book(name, authors, book_genre);
		bookDao.insert(result);
		return result;
	}	
	
	public Book editBook(String book, HashMap<String, String> values) throws EntityNotFoundException, InvalidOperationException, NotUniqueEntityFoundException {
		Book result = getBook(book).get(0);
		if (values.get("name")!=null) 
			result.setName(values.get("name"));	
		if (values.get("genre")!=null) {
			Genre book_genre = getOrAddGenre(values.get("genre"));
			result.setGenre(book_genre);
		}
		if (values.get("author")!=null) {
			Author book_author = getOrAddAuthor(values.get("author"));
			List<Author> authors = result.getAuthors();
			int iAuthor = authors.indexOf(book_author);
			if (iAuthor < 0) {
				authors.add(book_author);
				result.setAuthors(authors);				
			}
		}
		
		if (values.get("exAuthor")!=null) {
			Author exAuthor = getAuthor(values.get("exAuthor")).get(0);
			List<Author> authors = result.getAuthors();
			int iExAuthor = authors.indexOf(exAuthor);
			if (iExAuthor>=0) {
				if (authors.size() == 1) 
					throw new InvalidOperationException("Недопустимая операция: у книги не может быть ни одного автора");
				authors.remove(iExAuthor);	
				result.setAuthors(authors);
			}				
		}

		bookDao.update(result);		
		
		return result;
	}
	
	public boolean deleteBook(String book) throws EntityNotFoundException, NotUniqueEntityFoundException {
		boolean result = false;
		Book bookToDelete = getBook(book).get(0);
		bookDao.deleteById(bookToDelete.getId());
		result = true;
		return result;
	}
	
	public boolean deleteGenre(String genre) throws EntityNotFoundException, InvalidOperationException {
		boolean result = false;
		Genre exGenre = getGenre(genre);
    	HashMap<String, String> filters = new HashMap<>();
    	filters.put("genreId", String.valueOf(exGenre.getId()));  
		List<Book> bookByGenre = getBookAll(filters);
		if ((bookByGenre != null) && (bookByGenre.size()>0)) 
			throw new InvalidOperationException("Недопустимая операция: жанр используется");
		genreDao.deleteById(exGenre.getId());
		result = true;
		return result;
	}	
	
	public boolean deleteAuthor(String author) throws EntityNotFoundException, InvalidOperationException, NotUniqueEntityFoundException {
		boolean result = false;
		Author exAuthor = getAuthor(author).get(0);
    	HashMap<String, String> filters = new HashMap<>();
    	filters.put("authorId", String.valueOf(exAuthor.getId()));  
		List<Book> bookByAuthor = getBookAll(filters);
		if ((bookByAuthor != null) && (bookByAuthor.size()>0)) 
			throw new InvalidOperationException("Недопустимая операция: автор используется");
		genreDao.deleteById(exAuthor.getId());
		result = true;
		return result;
	}		
	
}