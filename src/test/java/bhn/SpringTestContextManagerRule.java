package bhn;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.test.context.TestContextManager;

/**
 * JUnit rule to enable spring test support including transactional support.
 *
 * Usage:
 *      @Rule public SpringTestContextManagerRule springTestContextManagerRule = new SpringTestContextManagerRule();
 *
 * You will still have to declare your test @Transactional
 */
public class SpringTestContextManagerRule implements MethodRule{
    /*
     * IMPLEMENTATION NOTE: The implementation is based on org.junit.rules.MethodRule, although it is deprecated. The new
     * org.junit.rules.TestRule apply-method is given a Description object which contains a method-NAME rather than a
     * method-INSTANCE. The Spring TextContextManager needs a method reference.
     */

    @Override
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object testInstance) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final TestContextManager tcm = new TestContextManager(testInstance.getClass());
                tcm.prepareTestInstance(testInstance);
                tcm.beforeTestMethod(testInstance, frameworkMethod.getMethod());
                try {
                    statement.evaluate();
                    tcm.afterTestMethod(testInstance, frameworkMethod.getMethod(), null);
                } catch (Throwable t ) {
                    tcm.afterTestMethod(testInstance, frameworkMethod.getMethod(), t);
                    throw t;
                }
            }
        };

    }
}
