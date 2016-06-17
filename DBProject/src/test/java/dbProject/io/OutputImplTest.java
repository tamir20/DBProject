package dbProject.io;

import org.junit.Test;

public class OutputImplTest {

    @Test
    public void writeToLogTest() throws Exception {

        Output output = new OutputImpl();

        output.writeAction(1,1,1);
        output.writeAction(1,2,1);
        output.writeWait(1,3,1);
        output.writeTransactionRestart(1,1);
        output.writeAction(1,1,2);
        output.writeFreeText("stam text");

        output.finish();
    }
}