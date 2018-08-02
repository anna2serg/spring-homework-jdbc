package ru.homework.domain;

public class Author {
    private int id;
    private String surname;
    private String firstname;
    private String middlename;
    
    public Author() {
        super();
    } 
    
    public Author(String surname, String firstname, String middlename) {
        this(0, surname, firstname, middlename);
    }     
    
    public Author(int id, String surname, String firstname, String middlename) {
        super();
        this.id = id;
        this.surname = surname;
        this.firstname = firstname;
        this.middlename = middlename;
    }        

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }    
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }        
    
    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }       
    
    @Override
    public String toString() {
        return String.format("[%s] %s %s %s", id, surname, firstname, (middlename != null && !middlename.isEmpty())?middlename:"");
    } 
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }  	
        if (!(obj instanceof Author)) {
            return false;
        }       
        Author other = (Author)obj;
         
        return ( (other.id == this.id) && 
        		 (other.firstname.equals(this.firstname)) && 
        		 (other.surname.equals(this.surname)) &&
        		 (other.middlename == null ? this.middlename == null : (this.middlename == null ? false : other.middlename.equals(this.middlename)) )
        	   );
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + id;
        result = 37 * result + (surname == null ? 0 : surname.hashCode());
        result = 37 * result + (firstname == null ? 0 : firstname.hashCode());
        result = 37 * result + (middlename == null ? 0 : middlename.hashCode());
		return result;       
    }    
    
}
