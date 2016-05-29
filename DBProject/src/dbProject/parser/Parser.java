package dbProject.parser;

import dbProject.Operation;

import java.util.List;


public interface Parser {

    /**
     * parses the file that contains the transactions
     *
     * @param fileName path to the file to be parsed
     * @return a list of transactions
     */
    List<List<Operation>> parse(String fileName);
}
