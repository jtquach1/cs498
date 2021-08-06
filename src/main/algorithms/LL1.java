package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static algorithms.Label.*;
import static algorithms.Utility.*;

class LL1 {

    public static void main(String[] args) throws Exception {
        TreeMap<String, String> arguments = getArguments(args);

        try {
            String inputFile = arguments.get("inputFile");
            String sentence = arguments.get("sentence");
            String outputPrefix = arguments.get("outputPrefix");

            checkCondition(
                    inputFile == null,
                    "ERROR: Input filename not specified");

            checkCondition(
                    outputPrefix == null,
                    "ERROR: Output filename prefix not specified");

            Grammar grammar = initializeGrammar(inputFile);
            TreeMap<Label, DOT> structures = getStructures(sentence, grammar);
            createDOTFiles(outputPrefix, structures);

        } catch (Exception e) {
            System.out.println("ERROR: Invalid grammar");
            throw e;
        }
    }

    @NotNull
    private static TreeMap<Label, DOT> getStructures(String sentence, Grammar grammar) throws Exception {
        System.out.println("Printing out first sets, follow sets, and LL(1) parse table");
        TreeMap<Label, DOT> structures = new TreeMap<>();

        tryToPopulateStructuresWithLL1Grammar(grammar, structures);
        tryToPopulateStructuresWithParse(sentence, structures);

        return structures;
    }

    private static void tryToPopulateStructuresWithParse(String sentence,
                                                         TreeMap<Label, DOT> structures) throws Exception {
        Grammar oldGrammar = (Grammar) structures.get(grammar);
        Grammar newGrammar = (Grammar) structures.get(leftRecursionRemovedGrammar);
        LL1ParseTable oldTable = (LL1ParseTable) structures.get(ll1ParseTable);
        LL1ParseTable newTable = (LL1ParseTable) structures.get(leftRecursionRemovedTable);

        boolean oldGrammarAlreadyLL1 = oldGrammar.isLL1(oldTable);
        boolean newGrammarAlreadyLL1 = newGrammar.isLL1(newTable);
        boolean canParseWithNoConflicts = oldGrammarAlreadyLL1 || newGrammarAlreadyLL1;

        if (sentence != null && canParseWithNoConflicts) {
            System.out.println("Printing sentence parse with LL(1) grammar");

            if (oldGrammarAlreadyLL1) {
                LL1ParseOutput output = oldGrammar.parseSentence(oldTable, sentence);
                structures.put(ll1ParseOutput, output);
            } else {
                LL1ParseOutput output = newGrammar.parseSentence(newTable, sentence);
                structures.put(leftRecursionRemovedOutput, output);
            }

        } else {
            removeLeftRecursionAttempts(structures);
            String message = "Grammar cannot be converted to LL(1)";
            if (sentence != null) {
                message += ", cannot parse sentence";
            }
            System.out.println(message);
        }
    }

    private static void removeLeftRecursionAttempts(TreeMap<Label, DOT> structures) {
        Set<Label> keys = new TreeSet<>(structures.keySet());
        for (Label entry : keys) {
            if (entry.name().contains(leftRecursionRemoved.name())) {
                structures.remove(entry);
            }
        }
    }

    private static void tryToPopulateStructuresWithLL1Grammar(Grammar grammar,
                                                              TreeMap<Label, DOT> structures) {
        populateStructures(structures, grammar);
        LL1ParseTable table = (LL1ParseTable) structures.get(ll1ParseTable);

        if (!grammar.isLL1(table)) {
            System.out.println("Grammar is not LL(1), attempting to remove left recursion");

            Grammar newGrammar = grammar.removeLeftRecursion();
            populateStructures(structures, newGrammar);
        }
    }

    private static void populateStructures(TreeMap<Label, DOT> structures, Grammar grammar) {
        boolean removedLeftRecursionEarlier = structures.size() >= 4;
        FirstMap firstMap = grammar.first();
        FollowMap followMap = grammar.follow(firstMap);
        LL1ParseTable table = grammar.generateLL1ParseTable(firstMap, followMap);

        if (removedLeftRecursionEarlier) {
            structures.put(leftRecursionRemovedGrammar, grammar);
            structures.put(leftRecursionRemovedFirst, firstMap);
            structures.put(leftRecursionRemovedFollow, followMap);
            structures.put(leftRecursionRemovedTable, table);
        } else {
            structures.put(Label.grammar, grammar);
            structures.put(first, firstMap);
            structures.put(follow, followMap);
            structures.put(ll1ParseTable, table);
        }
    }
}
