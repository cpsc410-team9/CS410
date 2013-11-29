package preprocessing;

import control.Main;
import org.junit.Test;
import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ParserTest {

    @Test
    public void testScanFolder(){
        Parser test = new Parser();
        File folderPath = new File(Main.GetTargetCodePath());
        assertTrue("Not Reading in directory containing Leviathan", folderPath.toString().contains("Leviathan"));
        try{
            test.parse(folderPath);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        assertEquals("ClassList does not have the expected value.", test.classList.size(), 125);
    }
}
