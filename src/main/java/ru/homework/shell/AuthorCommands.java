package ru.homework.shell;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ru.homework.domain.Author;
import ru.homework.exception.EntityNotFoundException;
import ru.homework.exception.InvalidOperationException;
import ru.homework.exception.NotUniqueEntityFoundException;
import ru.homework.service.BookcardService;
import ru.homework.service.FetchDataService;

@ShellComponent
public class AuthorCommands {
	
	private final BookcardService service;
	private final FetchDataService fetcher;
	
	@Autowired
    public AuthorCommands(BookcardService service, FetchDataService fetcher) {
		this.service = service;
		this.fetcher = fetcher;
	}
	
    @ShellMethod(value = "показать информацию обо всех авторах")
    public void getAuthorAll(
    		@ShellOption(help="фильтр по фамилии, имени или отчеству", defaultValue="") String name) {
    	HashMap<String, String> filters = new HashMap<>();
    	if (name != null && !name.isEmpty()) 
    		filters.put("name", name);
    	fetcher.output(service.getAuthorAll(filters));
    }
    
    @ShellMethod(value = "показать информацию об авторе")
    public void getAuthor(
    		@ShellOption(help="ИД или ФИО автора в формате \"Салтыков-Щедрин, Михаил, Евграфович\" ") String author) {   	
        try {
    		for (Author a : service.getAuthor(author, false)) {
    			System.out.println(a);
    		}             	
		} catch (EntityNotFoundException | NotUniqueEntityFoundException e) {
			System.out.println(e.getMessage());
		}
    }    
    
    @ShellMethod(value = "добавить информацию об авторе")
    public void addAuthor(
    		@ShellOption(help="фамилия") String surname, 
    		@ShellOption(help="имя") String firstname, 
    		@ShellOption(help="отчество (null для передачи пустого значения)", defaultValue=ShellOption.NULL) String middlename) {   	
    	System.out.println(service.addAuthor(surname, firstname, middlename));
    }   
 
    @ShellMethod(value = "отредактировать информацию об авторе")
    public void editAuthor(
    		@ShellOption(help="ИД или ФИО автора в формате \"Салтыков-Щедрин, Михаил, Евграфович\"") String author, 
    		@ShellOption(help="фамилия", defaultValue=ShellOption.NULL) String surname, 
    		@ShellOption(help="имя", defaultValue=ShellOption.NULL) String firstname, 
    		@ShellOption(help="middlename - отчество (null для передачи пустого значения)", defaultValue=ShellOption.NULL) String middlename) {   	
    	try {
        	HashMap<String, String> values = new HashMap<>();
        	if (surname != null) 
        		values.put("surname", surname);
        	if (firstname != null) 
        		values.put("firstname", firstname);  
        	if (middlename != null) {
            	values.put("middlename", middlename);         		
        	}
        	if (values.size() == 0) System.out.println("Не заданы обновляемые значения");
        	else System.out.println(service.editAuthor(author, values));
		} catch (EntityNotFoundException | NotUniqueEntityFoundException e) {
			System.out.println(e.getMessage());
		} 
    }  
    
    @ShellMethod(value = "удалить информацию об авторе")
    public void deleteAuthor(
    		@ShellOption(help="ИД или ФИО автора в формате \"Салтыков-Щедрин, Михаил, Евграфович\"") String author) {   	
        try {
        	if (service.deleteAuthor(author)) System.out.println("OK");      	
		} catch (EntityNotFoundException | NotUniqueEntityFoundException | InvalidOperationException e) {
			System.out.println(e.getMessage());
		}
    }      
    
}
