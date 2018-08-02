package ru.homework.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import ru.homework.domain.Genre;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
	InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
	ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})	
public class GenreDaoJdbcTest {
	
	@Autowired
	private GenreDaoJdbc genreDao;	

	@Test
    @Transactional
    @Rollback(true)	
	public void test() {
		int count = genreDao.count();
		Genre testGenre = new Genre("Изобразительное искусство");
		int testGenreId = genreDao.insert(testGenre);
		assertTrue(genreDao.count() == (count + 1));
		Genre dbGenre = genreDao.getById(testGenreId);
		assertEquals(testGenre, dbGenre);
		testGenre.setName("Изобразительное искусство и фотография");
		genreDao.update(testGenre);
		dbGenre = genreDao.getById(testGenre.getId());
		assertEquals(testGenre.getName(), dbGenre.getName());
		dbGenre = genreDao.getByName("Изобразительное искусство и фотография");
		assertEquals(testGenre, dbGenre);
		testGenre = new Genre("Биографии и мемуары"); 
		testGenreId = genreDao.insert(testGenre);
		HashMap<String, String> filters = new HashMap<>();
		filters.put("name", "граф");
		List<Genre> genres = genreDao.getAll(filters);
		assertTrue(genres.contains(testGenre));
		assertTrue(genres.contains(dbGenre));
		genreDao.deleteById(testGenre.getId());
		dbGenre = genreDao.getById(testGenreId);
		assertNull(dbGenre);
	}

}
