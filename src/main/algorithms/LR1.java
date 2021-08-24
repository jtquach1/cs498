package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

import static algorithms.Label.lr1ParseOutput;
import static algorithms.Label.lr1ParseTable;
import static algorithms.Utility.*;

class LR1 {

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

            checkCondition(
                    grammar.containsEpsilonProductions(),
                    "ERROR: Grammars containing epsilon productions are not supported");

            TreeMap<Label, DOT> structures = getStructures(sentence, grammar);
            createDOTFiles(outputPrefix, structures);

        } catch (Exception e) {
            System.out.println("ERROR: Invalid grammar");
            throw e;
        }
    }

    @NotNull
    private static TreeMap<Label, DOT> getStructures(String sentence, Grammar grammar) throws Exception {
        System.out.println("Printing out grammar, augmented grammar, LR(1) canonical collection, " +
                "Action table, and Goto table");
        TreeMap<Label, DOT> structures = new TreeMap<>();

        populateStructures(structures, grammar);
        LR1ParseTable table = (LR1ParseTable) structures.get(lr1ParseTable);

        boolean canParseWithNoConflicts = grammar.isLR1(table);

        if (sentence != null && canParseWithNoConflicts) {
            System.out.println("Printing sentence parse with LR(1) grammar");
            table = (LR1ParseTable) structures.get(lr1ParseTable);
            LR1ParseOutput output = grammar.parseSentence(table, sentence);
            structures.put(lr1ParseOutput, output);

        } else if (!canParseWithNoConflicts) {
            String message = "Grammar is not LR(1)";
            if (sentence != null) {
                message += ", cannot parse sentence";
            }
            System.out.println(message);
        }

        return structures;
    }

    private static void populateStructures(TreeMap<Label, DOT> structures, Grammar grammar) {
        Grammar augmented = grammar.augment();
        LR1Collection collection = grammar.computeLR1Collection();
        LR1ParseTable table = grammar.generateLR1ParseTable(collection);

        structures.put(Label.grammar, grammar);
        structures.put(Label.augmented, augmented);
        structures.put(Label.collection, collection);
        structures.put(lr1ParseTable, table);
    }
}
