package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

import static algorithms.Utility.*;

class LR1 {
    private static final String table = "LR1ParseTable";
    private static final String output = "LR1ParseOutput";
    private static final String augmentedSuffix = ".augmented";
    private static final String grammar = "grammar";
    private static final String collection = "LR1Collection";

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

            TreeMap<String, DOT> structures = getStructures(sentence, grammar);
            createDOTFiles(outputPrefix, structures);

        } catch (Exception e) {
            System.out.println("ERROR: Invalid grammar");
            throw e;
        }
    }

    @NotNull
    private static TreeMap<String, DOT> getStructures(String sentence, Grammar grammar) throws Exception {
        System.out.println("Printing out LR(1) canonical collection, Action table, and Goto table");
        TreeMap<String, DOT> structures = new TreeMap<>();

        populateStructures(structures, grammar);
        LR1ParseTable parseTable = (LR1ParseTable) structures.get(table);

        boolean canParseWithNoConflicts = grammar.isLR1(parseTable);

        if (sentence != null && canParseWithNoConflicts) {
            System.out.println("Printing sentence parse with LR(1) grammar");
            parseTable = (LR1ParseTable) structures.get(table);
            LR1ParseOutput parseOutput = grammar.parseSentence(parseTable, sentence);
            structures.put(output, parseOutput);

        } else {
            String message = "Grammar is not LR(1)";
            if (sentence != null) {
                message += ", cannot parse sentence";
            }
            System.out.println(message);
        }

        return structures;
    }

    private static void populateStructures(TreeMap<String, DOT> structures, Grammar grammar) {
        LR1Collection collection = grammar.computeLR1Collection();
        LR1ParseTable table = grammar.generateLR1ParseTable(collection);

        structures.put(LR1.grammar, grammar);
        structures.put(LR1.grammar + augmentedSuffix, grammar.augment());
        structures.put(LR1.table, table);
        structures.put(LR1.collection, collection);
    }
}
