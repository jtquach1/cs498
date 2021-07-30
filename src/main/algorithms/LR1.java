package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

import static algorithms.Utility.*;

class LR1 {
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
        System.out.println("Printing out LR(1) canonical collection, Action table, and Goto table");
        TreeMap<String, Object> structuresToJSON = new TreeMap<>();

        populateStructuresToJSON(structuresToJSON, grammar);
        LR1ParseTable table = (LR1ParseTable) structuresToJSON.get("LR1ParseTable");

        if (!grammar.isLR1(table)) {
            System.out.println("Grammar is not LR(1)");
            return structuresToJSON;
        }

        if (sentence != null) {
            System.out.println("Printing sentence parse with LR(1) grammar");
            table = (LR1ParseTable) structuresToJSON.get("LR1ParseTable");
            LR1ParseOutput output = grammar.parseSentence(table, sentence);
            structuresToJSON.put("LR1ParseOutput", output.toString());
        }
        return structuresToJSON;
    }

    private static void populateStructuresToJSON(TreeMap<String, Object> structuresToJSON,
                                                 Grammar grammar) {
        LR1Collection collection = grammar.computeLR1Collection();
        LR1ParseTable table = grammar.generateLR1ParseTable(collection);

        structuresToJSON.put("LR1ParseTable", table);
        structuresToJSON.put("LR1Collection", collection);
    }

}
