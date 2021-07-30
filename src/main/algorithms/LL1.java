package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

import static algorithms.Utility.*;

class LL1 {
    private static final String removalPrefix = "leftRecursionRemoved.";

    public static void main(String[] args) throws Exception {
        TreeMap<String, String> arguments = getArguments(args);

        try {
            String inputFile = arguments.get("inputFile");
            String sentence = arguments.get("sentence");
            String outputPrefix = arguments.get("outputPrefix");

            if (inputFile == null) {
                System.out.println("ERROR: Input filename not specified");
                return;
            }

            if (outputPrefix == null) {
                System.out.println("ERROR: Output filename prefix not specified");
                return;
            }

            Grammar grammar = initializeGrammar(inputFile);
            TreeMap<String, Object> structuresToJSON = getStructuresToJSON(sentence, grammar);
            createJSONFiles(outputPrefix, structuresToJSON);

        } catch (Exception e) {
            System.out.println("ERROR: Invalid grammar");
            throw e;
        }
    }

    @NotNull
    private static TreeMap<String, Object> getStructuresToJSON(String sentence, Grammar grammar) throws Exception {
        System.out.println("Printing out first sets, follow sets, and LL(1) parse table");
        TreeMap<String, Object> structuresToJSON = new TreeMap<>();

        populateStructuresToJSON(structuresToJSON, grammar);
        LL1ParseTable table = (LL1ParseTable) structuresToJSON.get("LL1ParseTable");

        if (!grammar.isLL1(table)) {
            System.out.println("Grammar is not LL(1), removing left recursion");

            grammar = grammar.removeLeftRecursion();
            populateStructuresToJSON(structuresToJSON, grammar);
        }

        if (sentence != null) {
            System.out.println("Printing sentence parse with LL(1) grammar");

            boolean removedLeftRecursionEarlier = structuresToJSON.size() >= 4;
            table = (LL1ParseTable) structuresToJSON.get(removedLeftRecursionEarlier ?
                    removalPrefix + "LL1ParseTable" : "LL1ParseTable");
            LL1ParseOutput output = grammar.parseSentence(table, sentence);
            String filename = removedLeftRecursionEarlier ? removalPrefix + "LL1ParseOutput" :
                    "LL1ParseOutput";

            structuresToJSON.put(filename, output.toString());
        }
        return structuresToJSON;
    }

    private static void populateStructuresToJSON(TreeMap<String, Object> structuresToJSON,
                                                 Grammar grammar) {
        boolean removedLeftRecursionEarlier = structuresToJSON.size() >= 4;
        FirstMap firstMap = grammar.first();
        FollowMap followMap = grammar.follow(firstMap);
        LL1ParseTable table = grammar.generateLL1ParseTable(firstMap, followMap);

        if (removedLeftRecursionEarlier) {
            structuresToJSON.put(removalPrefix + "grammar", grammar);
            structuresToJSON.put(removalPrefix + "firstMap", firstMap);
            structuresToJSON.put(removalPrefix + "followMap", followMap);
            structuresToJSON.put(removalPrefix + "LL1ParseTable", table);
        } else {
            structuresToJSON.put("grammar", grammar);
            structuresToJSON.put("firstMap", firstMap);
            structuresToJSON.put("followMap", followMap);
            structuresToJSON.put("LL1ParseTable", table);
        }
    }
}
