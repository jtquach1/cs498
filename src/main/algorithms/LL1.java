package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

        tryToPopulateStructuresWithLL1Grammar(grammar, structures);
        tryToPopulateStructuresWithParse(sentence, structures);

        return structures;
    }

    private static void tryToPopulateStructuresWithParse(String sentence,
                                                         TreeMap<String, DOT> structures) throws Exception {
        Grammar oldGrammar = (Grammar) structures.get("grammarLL1");
        Grammar newGrammar = (Grammar) structures.get(removalPrefix + "grammarLL1");
        LL1ParseTable oldTable = (LL1ParseTable) structures.get("LL1ParseTable");
        LL1ParseTable newTable = (LL1ParseTable) structures.get(removalPrefix + "LL1ParseTable");

        boolean oldGrammarAlreadyLL1 = oldGrammar.isLL1(oldTable);
        boolean newGrammarAlreadyLL1 = newGrammar.isLL1(newTable);
        boolean canParseWithNoConflicts = oldGrammarAlreadyLL1 || newGrammarAlreadyLL1;

        // What if both LL(1) parse tables have conflicts after attempting left recursion removal?
        if (sentence != null && canParseWithNoConflicts) {
            System.out.println("Printing sentence parse with LL(1) grammar");

            String filename;
            LL1ParseOutput output;

            if (oldGrammarAlreadyLL1) {
                filename = "LL1ParseOutput";
                output = oldGrammar.parseSentence(oldTable, sentence);
            } else {
                filename = removalPrefix + "LL1ParseOutput";
                output = newGrammar.parseSentence(newTable, sentence);
            }
            structures.put(filename, output);

        } else if (!canParseWithNoConflicts) {
            removeLeftRecursionAttempts(structures);
            System.out.println("Grammar cannot be converted to LL(1)");
        }
    }

    private static void removeLeftRecursionAttempts(TreeMap<String, DOT> structures) {
        Set<String> keys = new TreeSet<>(structures.keySet());
        for (String key : keys) {
            if (key.contains(removalPrefix)) {
                structures.remove(key);
            }
        }
    }

    private static void tryToPopulateStructuresWithLL1Grammar(Grammar grammar,
                                                              TreeMap<String, DOT> structures) {
        populateStructures(structures, grammar);
        LL1ParseTable table = (LL1ParseTable) structures.get("LL1ParseTable");

        if (!grammar.isLL1(table)) {
            System.out.println("Grammar is not LL(1), attempting to remove left recursion");

            Grammar newGrammar = grammar.removeLeftRecursion();
            populateStructures(structures, newGrammar);
        }
    }

    private static void populateStructures(TreeMap<String, DOT> structures, Grammar grammar) {
        boolean removedLeftRecursionEarlier = structures.size() >= 4;
        FirstMap firstMap = grammar.first();
        FollowMap followMap = grammar.follow(firstMap);
        LL1ParseTable table = grammar.generateLL1ParseTable(firstMap, followMap);

        if (removedLeftRecursionEarlier) {
            structures.put(removalPrefix + "grammarLL1", grammar);
            structures.put(removalPrefix + "firstMap", firstMap);
            structures.put(removalPrefix + "followMap", followMap);
            structures.put(removalPrefix + "LL1ParseTable", table);
        } else {
            structures.put("grammarLL1", grammar);
            structures.put("firstMap", firstMap);
            structures.put("followMap", followMap);
            structures.put("LL1ParseTable", table);
        }
    }
}
