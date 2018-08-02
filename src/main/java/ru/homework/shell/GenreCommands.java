package ru.homework.shell;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ru.homework.exception.EntityNotFoundException;
import ru.homework.exception.InvalidOperationException;
import ru.homework.service.BookcardService;
import ru.homework.service.FetchDataService;

@ShellComponent
public class GenreCommands {
	
	private final BookcardService service;
	private final FetchDataService fetcher;
	
	@Autowired
    public GenreCommands(BookcardService service, FetchDataService fetcher) {
		this.service = service;
		this.fetcher = fetcher;
	}

    @ShellMethod(value = "показать информацию обо всех жанрах")
    public void getGenreAll(
    		@ShellOption(help="фильтр по наименованию жанра", defaultValue="") String name) {
    	HashMap<String, String> filters = new HashMap<>();
    	if (name != null && !name.isEmpty()) 
    		filters.put("name", name);
    	fetcher.output(service.getGenreAll(filters));
    }
    
    @ShellMethod(value = "показать информацию о жанре")
    public void getGenre(
    		@ShellOption(help="ИД или наименование") String genre) {   	
        try {
        	System.out.println(service.getGenre(genre));
		} catch (EntityNotFoundException e) {
			System.out.println(e.getMessage());
		}
    }
 
    @ShellMethod(value = "добавить информацию о жанре")
    public void addGenre(
    		@ShellOption(help="наименование") String name) {  
    	System.out.println(service.addGenre(name));
    }       
    
    @ShellMethod(value = "отредактировать информацию о жанре")
    public void editGenre(
    		@ShellOption(help="ИД или текущее наименование") String genre, 
    		@ShellOption(help="новое наименование") String name) {  
    	try {
			System.out.println(service.editGenre(genre, name));
		} catch (EntityNotFoundException e) {
			System.out.println(e.getMessage());
		}
    }           

    @ShellMethod(value = "удалить информацию о жанре")
    public void deleteGenre(
    		@ShellOption(help="ИД или текущее наименование")String genre) {  
    	try {
    		if (service.deleteGenre(genre)) System.out.println("OK");
		} catch (EntityNotFoundException | InvalidOperationException e) {
			System.out.println(e.getMessage());
		}
    }       
    
}
