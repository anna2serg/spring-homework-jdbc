package ru.homework;

import java.sql.SQLException;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String[] args) throws SQLException {
		Console.main(args);
		SpringApplication.run(App.class, args);	
	}
}
