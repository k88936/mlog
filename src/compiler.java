

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;


public class compiler {
    static final HashMap<String, String[]> logicDictionary = new HashMap<>();
   

    static {
        logicDictionary.put("add_2", new String[]{"add result a b", "a", "b"});
        logicDictionary.put("add_0", new String[]{"add result "});
    }

    int current;























    final static String TYPE = "type";
    final static String VALUE = "value";

    final String  TOKENS="tokens";

    tree tokenizer(String input) throws Exception {
        int current = 0;


        tree tokens=new tree();
        while (current < input.length()) {

            char character = input.charAt(current);
            if (character == '(') {


                tokens.addNode(new  tree.Node(TYPE,"paren", VALUE,"("));
                current++;
                continue;
            }
            if (character == ')') {
               tokens.addNode(new tree.Node(TYPE,"paren",VALUE, ")"));

                current++;
                continue;
            }
            if (Pattern.compile("\\s").matcher(character + "").find()) {
                current++;
                continue;
            }


            if (Pattern.compile("[0-9]").matcher(character + "").find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[0-9]").matcher(character + "").find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new tree.Node(TYPE,"number",VALUE,value.toString()));
                continue;
            }


            if (character == '"') {
                character = input.charAt(++current);
                StringBuilder value = new StringBuilder();
                while (character != '"') {
                    value.append(character);
                    character = input.charAt(++current);
                }

                tokens.addNode(new tree.Node(TYPE,"string", VALUE, value));
                continue;
            }

            if (Pattern.compile("[a-z]").matcher(character + "").find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[a-z]").matcher(character + "").find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new tree.Node(TYPE,"name",VALUE,value.toString()));
                continue;
            }


            throw new Exception("I don't know what this character is: " + character);

        }


        return tokens;
    }

    final static String PROGRAM= "program";
    final static String BODY= "body";


    final static String NUMBER= "number";

    final static String NUMBER_LITERAL= "NumberLiteral";

    final static String STRING= "string";

    final static String STRING_LITERAL= "StringLiteral";

    final static String NAME= "name";

    final static String CALL_EXPRESSION= "CallExpression";


    tree parser(tree tokens) throws Exception {
        current = 0;
        tree ast = new tree();

        ast.root.putData(TYPE, PROGRAM);


        while (current < tokens.root.children.size()) {
            ast.addNode( walk(tokens).putData(TYPE, BODY));
        }

        return ast;


    }

    @SuppressWarnings("RawUseOfParameterized")
    private tree.Node walk(tree tokens) throws Exception {


        tree.Node token = tokens.getNode(current);
        String type= token.getStringData("type");
        String value= token.getStringData("value");

        switch (type) {
            case NUMBER -> {
                current++;
                return new tree.Node(TYPE,NUMBER_LITERAL,VALUE,value);
            }
            case STRING -> {
                current++;
                return new tree.Node(TYPE,STRING_LITERAL,VALUE,value);

            }
            case  NAME-> {

                tree.Node tokenWithName = token;
                token=tokens.getNode(++current);
                if (Objects.equals(token.getStringData("type"), "paren") & Objects.equals(token.getStringData("value"), "("))


                    {
                        tree.Node node = new tree.Node(TYPE, CALL_EXPRESSION, VALUE, tokenWithName.getStringData(VALUE));
                        token = tokens.getNode(++current);
                        //TODO handle this weird condition
                        while ((!Objects.equals(token.getStringData("type"), "paren")) || (
                                Objects.equals(token.getStringData("type"), "paren")) & !Objects.equals(token.getStringData("value"), ")")) {
                            node.addChild(walk(tokens));
                            token = tokens.getNode(current);



                        }
                        current++;
                        return node;
                    }else {
                    return new tree.Node(TYPE,CALL_EXPRESSION,VALUE,tokenWithName.getStringData(VALUE));
                }

            }
            default -> {
                throw new Exception(type);
            }



        }






    }

    private void traverser(HashMap<String, Object> ast) throws Exception {
        traverseNode(ast, null);

    }

    private void traverseArray(ArrayList array, HashMap<String, Object> parent) throws Exception {
        for (Object child : array
        ) {
            traverseNode((HashMap<String, Object>) child, parent);

        }
    }

    private void traverseNode(HashMap<String, Object> node, HashMap<String, Object> parent) throws Exception {
        // String methods= (String) node.get("name");
        String type = (String) node.get("type");
        visitor methods = new visitor(type);
        if (methods.enter) {
            methods.enter(node, parent);
        }
        switch (type) {
            case "Program":
                traverseArray((ArrayList) node.get("body"), node);
                break;


            case "CallExpression":
                traverseArray((ArrayList) node.get("parems"), node);
                break;


            case "NumberLiteral":
            case "StringLiteral":
            case "CallVariation":
                break;


            default:
                throw new Exception(type);
        }
        if (methods.exit) {
            methods.exit(node, parent);
        }
    }

    HashMap<String, Object> transformer(HashMap<String, Object> ast) throws Exception {

        HashMap<String, Object> newAst = dic("Program", new ArrayList<>());

        ast.put("_context", newAst.get("body"));
        traverser(ast);
        return newAst;

    }

    String argumentDistribute(HashMap<String, Object> node, ArrayList<HashMap<String, Object>> array, int Index) throws Exception {


        HashMap<String, Object> unit = new HashMap<>();
        int index = Index;
        String result = getArgument(Index);
        //unit.put()
        switch (node.get("type").toString()) {
            case "Program":
                ArrayList<Object> body = ((ArrayList<Object>) node.get("body"));
                for (Object child : body) {


                    argumentDistribute(((HashMap<String, Object>) child), array, ++index);


                }

                break;


            //return codeOutput.toString();

            case "ExpressionStatement":
                //return codeGenerator((HashMap<String, Object>) node.get("expression"),codeOutput,parentIndex) + ";"

                argumentDistribute((HashMap<String, Object>) node.get("expression"), array, index);

                break;
            //return codeGenerator(,codeOutput,Index) + ";";
            case "CallExpression":

                //code = new StringBuilder(codeGenerator((HashMap<String, Object>) node.get("callee"),codeOutput,parentIndex) + "(");

                ArrayList<Object> arguments = ((ArrayList<Object>) node.get("arguments"));
                ArrayList<String> argumentList = new ArrayList<>();
                if (arguments != null) {
                    for (Object child : arguments) {
                        argumentList.add(argumentDistribute(((HashMap<String, Object>) child), array, ++index));

                        //code.append(codeGenerator(((HashMap<String, Object>) child),codeOutput,parentIndex)).append(" ");

                    }
                }

                if (node.get("callee") != null) {
                    unit.put("name", ((HashMap<String, Object>) node.get("callee")).get("name"));
                } else {
                    unit.put("name", node.get("name"));
                }

                unit.put("arguments", argumentList);
                unit.put("result", result);
                array.add(unit);
                return result;


            case "CallVariation":


            case "NumberLiteral":


            case "StringLiteral":


                return (String) node.get("value");

            case "Identifier":

                break;
            default:
                throw new Exception(node.get("type").toString());


        }
        return null;


    }

    String codeGenerator(ArrayList<HashMap<String, Object>> array) {
        StringBuilder codeBuffer = new StringBuilder();
        for (HashMap<String, Object> codeSetting : array
        ) {

            ArrayList<String> arguments = ((ArrayList<String>) codeSetting.get("arguments"));


            String code = getCode((String) codeSetting.get("name"), arguments);
            code = code.replaceAll("result", (String) codeSetting.get("result"));

            codeBuffer.append(code).append('\n');


        }
        return codeBuffer.toString();
    }

    private String getCode(String name, ArrayList<String> arguments) {
        String[] codeSetting = logicDictionary.get(name + '_' + arguments.size());
        String code = codeSetting[0];

        int i = 0;
        for (String argument :
                arguments) {


            code = code.replaceAll("\\s"+codeSetting[++i]+"\\s", "\\s"+argument+"\\s");

        }


        return code;
    }


    private String getArgument(int index) {
        return "Argument_" + index + "_ignore";

    }


    private HashMap<String, Object> dic(String type, String value) {

        return dic(type, value, null, null);
    }

    private static HashMap<String, Object> dic(String type, String value, Object o, Object o1) {
        return null;
    }

    @SuppressWarnings("SameParameterValue")
    private HashMap<String, Object> dic(String type, ArrayList<Object> body) {
        return dic(type, null, null, body);
    }

    @SuppressWarnings("SameParameterValue")
    private HashMap<String, Object> dic(String type, String value, ArrayList<Object> params) {
        return dic(type, value, params, null);


    }

    @SuppressWarnings("unused")
    private static class visitor {
        final String type;
        final boolean enter;
        @SuppressWarnings("CanBeFinal")
        boolean exit;

        visitor(String type) {
            this.type = type;
            enter = true;
            exit = false;
        }


        public void enter(HashMap<String, Object> node, HashMap<String, Object> parent) {
            switch (type) {
                case "CallExpression" : {
                    HashMap<String, Object> expression = new HashMap<>();
                    expression.put("type", "CallExpression");
                    HashMap<String, Object> callee = new HashMap<>();
                    callee.put("type", "Identifier");
                    callee.put("name", node.get("value"));
                    expression.put("callee", callee);
                    expression.put("arguments", new ArrayList<>());
                    node.put("_context", expression.get("arguments"));
                    if (parent.get("type") != "CallExpression") {

                        HashMap<String, Object> expression2 = new HashMap<>();
                        expression2.put("type", "ExpressionStatement");
                        expression2.put("expression", expression);

                        ((ArrayList<Object>) parent.get("_context")).add(expression2);
                    } else {

                        ((ArrayList<Object>) parent.get("_context")).add(expression);
                    }
                }
                case "NumberLiteral":
								case"StringLiteral":
								case"CallVariation" :
                        ((ArrayList) parent.get("_context")).add(compiler.dic(type, (String) node.get("value"), null, null));
            }
        }

        @SuppressWarnings("EmptyMethod")
        public void exit(HashMap<String, Object> node, HashMap<String, Object> parent) {
        }
    }
}
