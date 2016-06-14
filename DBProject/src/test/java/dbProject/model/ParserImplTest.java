package dbProject.model;

import dbProject.io.Parser;
import dbProject.io.ParserImpl;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ParserImplTest {
    @Test
    public void parse() throws Exception {

        Parser parser = new ParserImpl();
        ParsedCommands parsedCommands = parser.parse();

        System.out.println(parsedCommands);
    }
}