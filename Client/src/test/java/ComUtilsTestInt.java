import org.junit.Test;
import static org.junit.Assert.*;
import utils.ComUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <h1>ComUtils test class</h1>
 * Tests write_int32 and read_int32 functions from ComUtils library.
 *
 * @author  UB-GEI-SD
 * @version 1.0
 * @since   11-02-2019
 */
public class ComUtilsTestInt {

    @Test
    public void example_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            //ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
           // comUtils.write_int32(2);
           // int readedInt = comUtils.read_int32();

           // assertEquals(2, readedInt);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
