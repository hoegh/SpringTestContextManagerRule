package bhn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;

@ContextConfiguration(locations = "classpath:testcontext.xml")
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleTest {
    public static final Log log = LogFactory.getLog(SimpleTest.class);

    @Autowired DataSource datasource;

    private JdbcTemplate jdbcTemplate;


    @Before public void setup() throws SQLException {
        jdbcTemplate = new JdbcTemplate(datasource);
    }

    public void insert(int id) throws SQLException {
        log.info("Inserting " + id);
        jdbcTemplate.update("INSERT INTO test( id ) VALUES (?)", id);
    }


    @Test
    public void canInsert() throws SQLException {
        insert(1);
    }

    @Test
    public void canInsertMoreThanOne() throws SQLException {
        insert(2);
        insert(1);
    }

    @Test(expected = DuplicateKeyException.class)
    public void cannotInsertDuplicates() throws SQLException {
        insert(2);
        insert(2);
    }

}
