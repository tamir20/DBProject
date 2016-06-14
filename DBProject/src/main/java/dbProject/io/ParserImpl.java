package dbProject.io;

import dbProject.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static sun.security.krb5.Confounder.longValue;

public class ParserImpl implements Parser {

    private static final String FILE_NAME = "transactions.dat";
    private int operationCount = 0;
    private int transactionCount = 0;
    private boolean newTransaction = true;

    @Override
    public ParsedCommands parse() {

        BufferedReader br = null;
        ParsedCommands parsedCommands = new ParsedCommands(null, SchedulerType.SERIAL, 345);
        List<Transaction> list = new ArrayList<>();
        newTransaction = true;

        try {

            String currentLine;

            br = new BufferedReader(new FileReader(FILE_NAME));

            currentLine  = br.readLine();
            handleSettings(currentLine, parsedCommands);

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

        parsedCommands.setTransactions(list);
        //todo:omar parse sched type and seed
        return parsedCommands;
    }

    /**
     * parse the scheduler type (SERIAL/RR/Random) and the seed.
     */
    private void handleSettings(String line, ParsedCommands parsedCommands) {
        List<String> parameters = Arrays.asList(line.split(" "));
        parsedCommands.setSchedulerType(SchedulerType.values()[Integer.valueOf(parameters.get(0))-1]);

        if (Integer.valueOf(parameters.get(0)).equals(2)){
            Double doubleSeed = Double.valueOf(parameters.get(1)) * 1000;
            parsedCommands.setSeed(doubleSeed.longValue());
        }
    }

    private void handleLine(List<Transaction> list, String line) {

        if(!line.isEmpty()) {
            if (newTransaction == true) {
                newTransaction = false;
                Transaction transaction = new Transaction(generateTransactionId());
                list.add(transaction);
            }
            if (line.endsWith(";")) {
                newTransaction = true;
            }
            Operation operation = parseLine(line);
            list.get(list.size()- 1).add(operation);

        }
    }

    private Operation parseLine(String line) {

        Operation operation = new Operation(generateOperationId(), Command.getCommand(line), Command.getParameters(line));
        return operation;
    }

    private int generateOperationId() {
        return operationCount++;
    }
    private int generateTransactionId() {
        return transactionCount++;
    }
}
