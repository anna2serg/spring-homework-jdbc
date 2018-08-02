package ru.homework.domain;

public class Genre {
    private int id;
    private String name;
    
    public Genre() {
        super();
    } 

    public Genre(String name) {
        this(0, name);
    }       
    
    public Genre(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }        

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
    
    @Override

    public String toString() {
        return String.format("[%s] %s", id, name);
    }    
 
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }  	
        if (!(obj instanceof Genre)) {
            return false;
        }       
        Genre other = (Genre)obj;
        
        return ( (other.id == this.id) && 
        		 (other.name.equals(this.name)) );
    }   
    
}
