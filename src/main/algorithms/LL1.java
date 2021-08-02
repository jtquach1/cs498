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
            TreeMap<String, DOT> structures = getStructures(sentence, grammar);
//            createJSONFiles(outputPrefix, structures);
            createDOTFiles(outputPrefix, structures);
        } catch (Exception e) {
            System.out.println("ERROR: Invalid grammar");
            throw e;
        }
    }

    @NotNull
    private static TreeMap<String, DOT> getStructures(String sentence, Grammar grammar) throws Exception {
        System.out.println("Printing out first sets, follow sets, and LL(1) parse table");
        TreeMap<String, DOT> structures = new TreeMap<>();

        populateStructures(structures, grammar);
        LL1ParseTable table = (LL1ParseTable) structures.get("LL1ParseTable");

        if (!grammar.isLL1(table)) {
            System.out.println("Grammar is not LL(1), removing left recursion");

            grammar = grammar.removeLeftRecursion();
            populateStructures(structures, grammar);
        }

        if (sentence != null) {
            System.out.println("Printing sentence parse with LL(1) grammar");

            boolean removedLeftRecursionEarlier = structures.size() >= 4;
            table = (LL1ParseTable) structures.get(removedLeftRecursionEarlier ?
                    removalPrefix + "LL1ParseTable" : "LL1ParseTable");
            LL1ParseOutput output = grammar.parseSentence(table, sentence);
            String filename = removedLeftRecursionEarlier ? removalPrefix + "LL1ParseOutput" :
                    "LL1ParseOutput";

            structures.put(filename, output);
        }
        return structures;
    }

    private static void populateStructures(TreeMap<String, DOT> structures, Grammar grammar) {
        boolean removedLeftRecursionEarlier = structures.size() >= 4;
        FirstMap firstMap = grammar.first();
        FollowMap followMap = grammar.follow(firstMap);
        LL1ParseTable table = grammar.generateLL1ParseTable(firstMap, followMap);

        if (removedLeftRecursionEarlier) {
            structures.put(removalPrefix + "grammar", grammar);
            structures.put(removalPrefix + "firstMap", firstMap);
            structures.put(removalPrefix + "followMap", followMap);
            structures.put(removalPrefix + "LL1ParseTable", table);
        } else {
            structures.put("grammar", grammar);
            structures.put("firstMap", firstMap);
            structures.put("followMap", followMap);
            structures.put("LL1ParseTable", table);
        }
    }


}
