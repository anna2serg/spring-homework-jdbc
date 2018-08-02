package ru.homework.shell;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ru.homework.domain.Book;
import ru.homework.exception.EntityNotFoundException;
import ru.homework.exception.InvalidOperationException;
import ru.homework.exception.NotUniqueEntityFoundException;
import ru.homework.service.BookcardService;
import ru.homework.service.FetchDataService;

@ShellComponent
public class BookCommands {
	
	private final BookcardService service;
	private final FetchDataService fetcher;
	
	@Autowired
    public BookCommands(BookcardService service, FetchDataService fetcher) {
		this.service = service;		
		this.fetcher = fetcher;
	}

    @ShellMethod(value = "показать информацию обо всех книгах")
    public void getBookAll(
    		@ShellOption(help="фильтр по наименованию", defaultValue="") String name,
    		@ShellOption(help="фильтр по автору", defaultValue="") String author,
    		@ShellOption(help="фильтр по жанру", defaultValue="") String genre,
    		@ShellOption(help="фильтр по ИД автора", defaultValue="") String authorId,
    		@ShellOption(help="фильтр по ИД жанра", defaultValue="") String genreId) {
    	HashMap<String, String> filters = new HashMap<>();
    	if (name != null && !name.isEmpty()) 
    		filters.put("name", name);
    	if (author != null && !author.isEmpty()) 
    		filters.put("author", author);    	
    	if (genre != null && !genre.isEmpty()) 
    		filters.put("genre", genre); 
    	if (authorId != null && !authorId.isEmpty()) 
    		filters.put("authorId", authorId);
    	if (genreId != null && !genreId.isEmpty()) 
    		filters.put("genreId", genreId);   
    	fetcher.output(service.getBookAll(filters));
    }
    
    @ShellMethod(value = "показать информацию о книге")
    public void getBook(
    		@ShellOption(help="ИД или наименование") String book) {   	
        try {
    		for (Book b : service.getBook(book, false)) {
    			System.out.println(b);
    		}        	
		} catch (EntityNotFoundException | NotUniqueEntityFoundException e) {
			System.out.println(e.getMessage());
		}
    }    
    
    @ShellMethod(value = "добавить информацию о книге")
    public void addBook(
    		@ShellOption(help="наименование") String name, 
    		@ShellOption(help="жанр (ИД или наименование)") String genre, 
    		@ShellOption(help="автор (ИД или ФИО автора в формате \"Салтыков-Щедрин, Михаил, Евграфович\")\r\nдля добавления дополнительных авторов см. edit-book") String author) { 
        try {
        	System.out.println(service.addBook(name, genre, author));
		} catch (EntityNotFoundException | NotUniqueEntityFoundException e) {
			System.out.println(e.getMessage());
		}
    } 
    
    @ShellMethod(value = "отредактировать информацию о книге")
    public void editBook(
    		@ShellOption(help="ИД или наименование (если по наименованию будет найдено несколько книг, то будет ошибка)") String book, 
    		@ShellOption(help="новое наименование книги", defaultValue=ShellOption.NULL) String name, 
    		@ShellOption(help="ИД или наименование нового жанра", defaultValue=ShellOption.NULL) String genre, 
    		@ShellOption(help="ИД или ФИО добавляемого к книге автора в формате \"Салтыков-Щедрин, Михаил, Евграфович\"", defaultValue=ShellOption.NULL) String author, 
    		@ShellOption(help="ИД или ФИО удаляемого для книги автора в том же формате", defaultValue=ShellOption.NULL) String exAuthor) {   	
    	try {
        	HashMap<String, String> values = new HashMap<>();
        	if (name != null) 
        		values.put("name", name);        	
        	if (genre != null) 
        		values.put("genre", genre);
        	if (author != null) 
        		values.put("author", author);  
        	if (exAuthor != null) {
            	values.put("exAuthor", exAuthor);         		
        	}
        	if (values.size() == 0) System.out.println("Не заданы обновляемые значения");
        	else System.out.println(service.editBook(book, values));
		} catch (EntityNotFoundException | InvalidOperationException | NotUniqueEntityFoundException e) {
			System.out.println(e.getMessage());
		} 
    }   
    
    @ShellMethod(value = "удалить информацию о книге")
    public void deleteBook(
    		@ShellOption(help="ИД или наименование") String book) {   
        try {
        	if (service.deleteBook(book)) System.out.println("OK");   	
		} catch (EntityNotFoundException | NotUniqueEntityFoundException e) {
			System.out.println(e.getMessage());
		}
    }       
    
}
