package dbProject.model;

import org.junit.Test;

public class CommandTest {
    @Test
    public void getCommand() throws Exception {
        String test1 = "1 Allocate_record(1, ”Technion”, “CS”)^rid1\n";
        String test2 = "1 Insert(1, rid1)";
        String test3 = "1 Delete(1, rid7)";
        String test4 = "1 Search(1)^rid9";
        String test5 = "1 Range_search(1, 3, 2);";

        if (Command.ALLOCATE_RECORD.equals(Command.getCommand(test1))){
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }

        if (Command.INSERT.equals(Command.getCommand(test2))){
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }

        if (Command.DELETE.equals(Command.getCommand(test3))){
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }

        if (Command.SEARCH.equals(Command.getCommand(test4))){
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }

        if (Command.RANGE_SEARCH.equals(Command.getCommand(test5))){
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }

    @Test
    public void getParameters() throws Exception {
        String test1 = "1 Allocate_record(1, ”Technion”, “CS”)^rid1\n";
        String test2 = "1 Insert(1, rid1)";
        String test3 = "1 Delete(1, rid7)";
        String test4 = "1 Search(1)^rid9";
        String test5 = "1 Range_search(1, 3, 2);";

        System.out.println(Command.getParameters(test1));
        System.out.println(Command.getParameters(test2));
        System.out.println(Command.getParameters(test3));
        System.out.println(Command.getParameters(test4));
        System.out.println(Command.getParameters(test5));

    }

}