package dbProject.model;

import dbProject.io.Parser;
import dbProject.io.ParserImpl;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ParserImplTest {
    @Test
    public void parse() throws Exception {

//
//        Process p = Runtime.getRuntime().exec("ls -al");
//        p.waitFor();
//
//        BufferedReader reader =
//                new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//        StringBuffer sb = new StringBuffer();
//        String line = "";
//        while ((line = reader.readLine())!= null) {
//            sb.append(line + "\n");
//        }
//        System.out.print(sb.toString());

        Parser parser = new ParserImpl();
        parser.parse();
    }
}