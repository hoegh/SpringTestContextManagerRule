package bhn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assume.assumeThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;

@ContextConfiguration(locations = "classpath:testcontext.xml")
@Transactional
@RunWith(Theories.class)
public class FixedTheoriesTest {
    public static final Log log = LogFactory.getLog(SimpleTest.class);

    @Autowired DataSource datasource;

    private JdbcTemplate jdbcTemplate;

    @Rule public SpringTestContextManagerRule springTestContextManagerRule = new SpringTestContextManagerRule();

    @Before public void setup() throws Exception {
        jdbcTemplate = new JdbcTemplate(datasource);
    }

    public void insert(int id) throws SQLException {
        log.info("Inserting " + id);
        jdbcTemplate.update("INSERT INTO test( id ) VALUES (?)", id);
    }

    @DataPoints public static int[] ids() {
        return new int[] {1,2,5,10,20,50,100,200,500};
    }
    
    @Theory
    public void canInsert(int id) throws SQLException {
        insert(id);
    }

    @Theory
    public void canInsertMoreThanOne(int anId, int anotherId) throws SQLException {
        assumeThat(anId, not(equalTo(anotherId)));
        insert(anId);
        insert(anotherId);
    }

    @Theory
    public void cannotInsertDuplicates(int id) throws SQLException {
        insert(id);
        try{
            insert(id);
            Assert.fail("Duplicate key exception expected");
        } catch (DuplicateKeyException dke) {
            //expected
        }
    }

}
