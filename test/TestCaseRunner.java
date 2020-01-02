import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestCaseRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestClient.class,TestStoreOperations.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful()?"Unit testcases completed with 0 failures":"Unit testcases contain failures");
    }
}
