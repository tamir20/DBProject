package dbProject.io;

import dbProject.Operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ParserImpl implements Parser {

    private static final String FILE_NAME = "transactions.dat";

    @Override
    public List<List<Operation>> parse() {

        BufferedReader br = null;
        List<List<Operation>> list = new LinkedList<>();


        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(FILE_NAME));

            while ((sCurrentLine = br.readLine()) != null) {

                Operation operation = parseLine(sCurrentLine);

                handleOperation(list, operation);
                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return list;
    }

    private void handleOperation(List<List<Operation>> list, Operation operation) {
        //todo:omar implement this

//        if (operation.getCommand().equals(Commands.BEGIN)){
//            //create a new transaction
//
//        }
    }

    private Operation parseLine(String sCurrentLine) {
        Operation operation = new Operation();
        //todo:omar implement this
        return operation;
    }
}
