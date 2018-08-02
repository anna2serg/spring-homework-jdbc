package ru.homework.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
public class AppSettings {
	private int fetchsize;
	
	public int getFetchsize() {
		return fetchsize;
	}
	
	public void setFetchsize(int fetchsize) {
		this.fetchsize = fetchsize;
	}
}
