import org.junit.*;

import play.mvc.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
 *
 * Simple (JUnit) tests that can call all parts of a Play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class MainControllerTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

//    @Test
//    public void indexTemplateShouldContainTheStringThatIsPassedToIt() {
//        running(fakeApplication(), new Runnable() {
//            public void run() {
//                Content html = views.html.index.render("Your new application is ready.");
//                assertThat(contentType(html)).isEqualTo("text/html");
//                assertThat(contentAsString(html)).contains("Your new application is ready.");
//            }
//        });
//    }

//    @Test
//    public void indexShouldContainTheCorrectString() {
//        running(fakeApplication(), new Runnable() {
//            public void run() {
//
//            }
//        });
//    }

}
