package ru.homework.dao;

import static org.junit.Assert.*;

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

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})	
public class AuthorDaoJdbcTest {

	@Autowired
	private AuthorDaoJdbc authorDao;
	
	@Test
    @Transactional
    @Rollback(true)	
	public void test() {
		int count = authorDao.count();
		Author testAuthor = new Author("Достоевский", "Федор", "Михайлович");
		int testAuthorId = authorDao.insert(testAuthor);
		assertTrue(authorDao.count() == (count + 1));
		Author dbAuthor = authorDao.getById(testAuthorId);
		assertEquals(testAuthor, dbAuthor);
		testAuthor.setFirstname("Федя");
		authorDao.update(testAuthor);
		dbAuthor = authorDao.getById(testAuthor.getId());
		assertEquals(testAuthor.getFirstname(), dbAuthor.getFirstname());	
		List<Author> DostoevskyList = authorDao.getByNames("Достоевский", "Федя", "Михайлович");
		assertTrue(DostoevskyList.size()>0);
		dbAuthor = DostoevskyList.get(0);
		assertEquals(testAuthor, dbAuthor);
		testAuthor = new Author("Салтыков-Щедрин", "Михаил", "Евграфович");
		testAuthorId = authorDao.insert(testAuthor);
		HashMap<String, String> filters = new HashMap<>();
		filters.put("name", "миха");
		List<Author> authors = authorDao.getAll(filters);
		assertTrue(authors.contains(testAuthor));
		assertTrue(authors.contains(dbAuthor));	
		authorDao.deleteById(testAuthor.getId());
		dbAuthor = authorDao.getById(testAuthorId);
		assertNull(dbAuthor);		
	}

}
