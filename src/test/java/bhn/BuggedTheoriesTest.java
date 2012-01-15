package bhn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Ignore("Will fail due to lack of transactions")
@ContextConfiguration(locations = "classpath:testcontext.xml")
@Transactional
@RunWith(Theories.class)
public class BuggedTheoriesTest {
    public static final Log log = LogFactory.getLog(SimpleTest.class);

    @Autowired DataSource datasource;

    private JdbcTemplate jdbcTemplate;


    @Before public void setup() throws Exception {
        //here we hook into the Spring test-support framework
        final TestContextManager tcm = new TestContextManager(getClass());
        tcm.prepareTestInstance(this);
        //this will work for everything but spring transactions as transactions expects to be enabled before @Before

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
        //this will fail due to lack of transactions
        insert(1);
    }

    @Test(expected = DuplicateKeyException.class)
    public void cannotInsertDuplicates() throws SQLException {
        insert(2);
        insert(2);
    }

}
