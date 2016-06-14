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

    @Override
    public ParsedCommands parse() {

        BufferedReader br = null;
        ParsedCommands parsedCommands = new ParsedCommands(null, SchedulerType.SERIAL, 345);
        HashMap<Integer ,Transaction> list = new LinkedHashMap<>();

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

        parsedCommands.setTransactions(new ArrayList<>(list.values()));

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

    private void handleLine(HashMap<Integer, Transaction> transactionHashMap, String line) {

        if(!line.isEmpty()) {
            Operation operation = parseLine(line);

            Integer transactionId = getTransactionId(line);
            if (!transactionHashMap.containsKey(transactionId)){
                transactionHashMap.put(transactionId,new Transaction(transactionId));
            }
            transactionHashMap.get(transactionId).add(operation);

        }
    }

    private Integer getTransactionId(String line) {
        return Integer.valueOf(line.split(" ")[0]);
    }

    private Operation parseLine(String line) {

        Operation operation = new Operation(generateOperationId(), Command.getCommand(line), Command.getParameters(line));
        return operation;
    }

    private int generateOperationId() {
        return operationCount++;
    }
}
