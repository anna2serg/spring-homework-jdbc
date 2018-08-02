package ru.homework.domain;

import java.util.List;

import ru.homework.helper.StringHelper;

public class Book {
    private int id;
    private String name;
    private List<Author> authors;
    private Genre genre;
    
    public Book(String name, List<Author> authors, Genre genre) {
        this(0, name, authors, genre);
    }            
 
    public Book(int id, String name, List<Author> authors, Genre genre) {
        super();
        this.id = id;
        this.name = name;
        this.authors = authors;
        this.genre = genre;
    }        

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
    	this.authors = authors;
    }    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }      
    
    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }    
    
    @Override
    public String toString() {
    	String result = "";
    	String book_name = String.format("[%s] %s", id, name);
    	book_name = StringHelper.ellipsize(book_name, 50);
    	result += String.format("%-50s", book_name);
    	result += "| "; 
    	String book_genre = StringHelper.ellipsize(genre.toString(), 25);
    	result += String.format("%-25s", book_genre);
    	result += "| ";
    	String book_author = authors.get(0).toString();
    	result += String.format("%-35s", book_author);
    	if (authors.size() > 1) result += "\r\n";
    	for (int i = 1; i < authors.size(); i+=1) {
        	result += String.format("%-50s", "");
        	result += "| ";
        	result += String.format("%-25s", "");
        	result += "| ";
        	book_author = authors.get(i).toString();
        	result += String.format("%-35s\r\n", book_author);    		
    	}
        return result; 
    }   
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }  	
        if (!(obj instanceof Book)) {
            return false;
        }       
        Book other = (Book)obj;
        
        return (other.id == this.id) && 
			 (other.name.equals(this.name)) && 
			 (other.genre.equals(this.genre)) &&
			 (other.authors.size() == this.authors.size() &&
			 (other.authors.containsAll(this.authors)) &&
			 (this.authors.containsAll(other.authors)));
        
    }    
    
}
