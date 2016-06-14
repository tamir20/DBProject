package dbProject.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Command {

    ALLOCATE_RECORD("A",16),
    INSERT("I", 7),
    DELETE("D", 7),
    SEARCH("S", 7),
    RANGE_SEARCH("R", 13);

    private final String startsWith;
    private final int paramIndex;

    Command(String startsWith, int paramIndex) {
        this.startsWith = startsWith;
        this.paramIndex = paramIndex;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public static Command getCommand(String line){
        Command result = null;
        for (Command command : Command.values()){
            if (command.getStartsWith().equals(line.substring(2, 2+command.getStartsWith().length()))){
                result = command;
            }
        }
        return result;
    }

    public static List<String> getParameters(String line) {
        List<String> result = new ArrayList<>();
        Command command = Command.getCommand(line);
        String rid = null;

        int endIdx;
        int startIdx = command.getParamIndex()+2;

        if (command.equals(ALLOCATE_RECORD) || command.equals(SEARCH)) {
            result.addAll(extractCSVtoList(line, startIdx, line.lastIndexOf("^")-1));
            endIdx = line.endsWith(";")?line.length()-1:line.length();
            result.add(line.substring(line.lastIndexOf("^")+1, endIdx));
        } else {
            endIdx = line.endsWith(";")?line.length()-2:line.length()-1;
            result.addAll(extractCSVtoList(line, startIdx, endIdx));

        }

        return result;
    }

    private static List<String> extractCSVtoList(String line, int startIdx, int endIdx) {
        List<String> result;
        String parametersCSV = line.substring(startIdx, endIdx);
        result = Arrays.asList(parametersCSV.split(","));
        return result;
    }
}
