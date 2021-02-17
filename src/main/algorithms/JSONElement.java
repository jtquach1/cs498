// copied from j--, in JAST.java

import java.util.ArrayList;

/**
 * Representation of an element with a JSON document.
 */
class JSONElement {
    // List of attribute names.
    private ArrayList<String> attrNames;

    // List of attribute values.
    private ArrayList<String> attrValues;

    // List of children names.
    private ArrayList<String> childrenNames;

    // List of children.
    private ArrayList<JSONElement> children;

    // Indentation level.
    private int indentation;

    /**
     * Constructs an empty JSON element.
     */
    public JSONElement() {
        this.attrNames = new ArrayList<String>();
        this.attrValues = new ArrayList<String>();
        this.childrenNames = new ArrayList<String>();
        this.children = new ArrayList<JSONElement>();
        indentation = 0;
    }

    /**
     * Adds an attribute to this JSON element with the given name and value.
     *
     * @param name  name of the attribute.
     * @param value value of the attribute.
     */
    public void addAttribute(String name, String value) {
        attrNames.add(name);
        attrValues.add(value);
    }

    /**
     * Adds an attribute to this JSON element with the given name and value as a list of strings.
     *
     * @param name  name of the attribute.
     * @param value value of the attribute as a list of strings.
     */
    public void addAttribute(String name, ArrayList<String> value) {
        attrNames.add(name);
        attrValues.add(value.toString());
    }

    /**
     * Adds a child to this JSON element with the given name.
     *
     * @param name  name of the child.
     * @param child the child.
     */
    public void addChild(String name, JSONElement child) {
        child.indentation = indentation + 4;
        childrenNames.add(name);
        children.add(child);
    }

    /**
     * Returns a string representation of this JSON element.
     *
     * @return a string representation of this JSON element.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (indentation > 0) {
            sb.append(String.format("%" + indentation + "s", " "));
        }
        sb.append("{\n");
        for (int i = 0; i < attrNames.size(); i++) {
            String name = attrNames.get(i);
            String value = attrValues.get(i);
            sb.append(String.format("%" + (indentation + 4) + "s", " "));
            if (value.startsWith("[") && value.endsWith("]")) {
                sb.append(String.format("\"%s\": %s", name, value));
            } else {
                sb.append(String.format("\"%s\": \"%s\"", name, value));
            }
            if (i < attrNames.size() - 1 || childrenNames.size() > 0) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        for (int i = 0; i < childrenNames.size(); i++) {
            String name = childrenNames.get(i);
            JSONElement child = children.get(i);
            sb.append(String.format("%" + (indentation + 4) + "s", " "));
            sb.append(String.format("\"%s\":\n", name));
            sb.append(child.toString());
            if (i < childrenNames.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        if (indentation > 0) {
            sb.append(String.format("%" + indentation + "s", " "));
        }
        sb.append("}");
        return sb.toString();
    }
}