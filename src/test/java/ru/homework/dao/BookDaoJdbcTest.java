package ru.homework.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import ru.homework.domain.Author;
import ru.homework.domain.Book;
import ru.homework.domain.Genre;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})	
public class BookDaoJdbcTest {

	@Autowired
	private BookDaoJdbc bookDao;	
	
	@Test
    @Transactional
    @Rollback(true)	
	public void test() {
		int count = bookDao.count();
		List<Author> testBookAuthors = new ArrayList<>();
		testBookAuthors.add(new Author(14, "Агафонов", "А", "В"));
		testBookAuthors.add(new Author(15, "Пожарская", "Светлана", "Георгиевна"));
		Genre testBookGenre = new Genre(3, "Детская литература");
		Book testBook = new Book("Фотобукварь", testBookAuthors, testBookGenre);
		int testBookId = bookDao.insert(testBook);
		assertTrue(bookDao.count() == (count + 1));
		Book dbBook = bookDao.getById(testBookId);
		assertEquals(testBook, dbBook);		
		testBook.setName("ФОТО букварь");
		bookDao.update(testBook);		
		dbBook = bookDao.getById(testBook.getId());
		assertEquals(testBook.getName(), dbBook.getName());		
		List<Book> testBookList = bookDao.getByName("ФОТО букварь");
		assertTrue(testBookList.size()>0);		
		dbBook = testBookList.get(0);
		assertEquals(testBook, dbBook);	
		testBookAuthors.clear();;
		testBookAuthors.add(new Author(16, "Ткаченко", "Наталия", "Александровна"));
		testBookAuthors.add(new Author(17, "Тумановская", "Мария", "Петровна"));
		testBook = new Book("Букварь для малышей", testBookAuthors, testBookGenre);
		testBookId = bookDao.insert(testBook);
		HashMap<String, String> filters = new HashMap<>();
		filters.put("name", "букварь");
		List<Book> books = bookDao.getAll(filters);	
		assertTrue(books.contains(testBook));
		assertTrue(books.contains(dbBook));		
		bookDao.deleteById(testBook.getId());
		dbBook = bookDao.getById(testBookId);
		assertNull(dbBook);			
	}

}
