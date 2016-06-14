package dbProject.io;

import dbProject.model.ParsedCommands;
import dbProject.model.Transaction;

import java.util.List;

/**
 * this class handles the reading and parsing of the '‫‪transactions.dat‬‬' file
 */
public interface Parser {

    /**
     * parses the file that contains the transactions
     * We assume that the run directory contains a file called '‫‪transactions.dat‬‬'
     *
     * @return a list of transactions
     */
    ParsedCommands parse();

}
