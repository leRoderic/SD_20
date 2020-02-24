import org.junit.Test;
import utils.ComUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

/**
 * <h1>ComUtils test class</h1>
 * Tests functions read_char and write_char from the ComUtils library-
 * <p>
 *
 * @author  leRoderic
 * @version 1.0
 * @since   12-02-2020
 */
public class ComUtilsTestChar {

    @Test
    public void example_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_char('~');

            assertEquals("~", comUtils.read_char());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
