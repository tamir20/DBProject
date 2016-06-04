package dbProject.io;

import dbProject.model.Command;
import dbProject.model.Operation;
import dbProject.model.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParserImpl implements Parser {

    private static final String FILE_NAME = "transactions.dat";
    private int operationCount = 0;
    private int transactionCount = 0;
    private boolean newTransaction = true;

    @Override
    public List<Transaction> parse() {

        BufferedReader br = null;
        List<Transaction> list = new ArrayList<>();
        newTransaction = true;

        try {

            String currentLine;

            br = new BufferedReader(new FileReader(FILE_NAME));

            while ((currentLine = br.readLine()) != null) {
                if (!currentLine.isEmpty()) {
                    handleLine(list, currentLine);
                }
            }

        } catch (IOException e) {
           System.out.println("Error: couldn't find file");
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return list;
    }

    private void handleLine(List<Transaction> list, String line) {
        //todo:omar implement this
        if (newTransaction == true) {
            newTransaction = false;
            Transaction transaction = new Transaction(generateTransactionId());
            list.add(transaction);
        }
        if (line.endsWith(";")){
            newTransaction = true;
        } else {
            Operation operation = parseLine(line);
            list.get(list.size()-1).add(operation);
//            System.out.println(operation);
        }

    }

    private Operation parseLine(String line) {

        Operation operation = new Operation(generateOperationId(), Command.getCommand(line), Command.getParameters(line));
        //todo:omar implement this
        return operation;
    }

    private int generateOperationId() {
        return operationCount++;
    }
    private int generateTransactionId() {
        return transactionCount++;
    }
}
