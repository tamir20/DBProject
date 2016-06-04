package dbProject.model;

import org.junit.Test;

public class CommandTest {
    @Test
    public void getCommand() throws Exception {
        String test1 = "Allocate_record(2, \"BIU\", \"CSE\")\n";
        String test2 = "Insert(\"BIU\", rid2)\n";
        String test3 = "Delete(rid1);\n";
        String test4 = "Search(3)^rid1\n";
        String test5 = "Range_search(1,8)\n";
        String test6 = ";";

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

        if (Command.END_TRANSACTION.equals(Command.getCommand(test6))){
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }

    @Test
    public void getParameters() throws Exception {
        String test1 = "Allocate_record(2, \"BIU\", \"CSE\")";
        String test2 = "Insert(\"BIU\", rid2)";
        String test3 = "Delete(rid1)";
        String test4 = "Search(3)";
        String test5 = "Range_search(1,8)";
        String test6 = ";";

        System.out.println(Command.getParameters(test1));
        System.out.println(Command.getParameters(test2));
        System.out.println(Command.getParameters(test3));
        System.out.println(Command.getParameters(test4));
        System.out.println(Command.getParameters(test5));
        System.out.println(Command.getParameters(test6));

    }

}