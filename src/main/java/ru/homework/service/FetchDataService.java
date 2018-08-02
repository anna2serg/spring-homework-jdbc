package ru.homework.service;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.homework.configuration.AppSettings;

@Service
public class FetchDataService {

	private final AppSettings settings;
	private final Scanner in;
	
	@Autowired
	public FetchDataService(AppSettings settings) {
		this.settings = settings;
		this.in = new Scanner(System.in);
	}
	
	public <T> void output(List<T> data) {
    	int i = 0;
    	String userInput = "";
		for (T item : data) {
			System.out.println(item);
			i++;
			if ((i % settings.getFetchsize() == 0) && (i < data.size())) {
				System.out.println("Для продолжения вывода - нажмите Enter, для прерывания - введите любой символ, затем Enter");
				userInput = in.nextLine();
				if (!userInput.equals("")) break;
			}			 
		}		
	}
	
}
