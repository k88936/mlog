import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

//todo translation 
// todo single operation 



public class compiler {
    static final HashMap<String, String[]> logicDictionary = new HashMap<>();
    final static String TYPE = "type";
    final static String VALUE = "value";
    final static String  TOKENS="tokens";
    final static String PAREN="paren";
    final static String PROGRAM= "program";
    final static String BODY= "body";
    final static String NUMBER= "number";
    final static String NUMBER_LITERAL= "NumberLiteral";
    final static String STRING= "string";
    final static String STRING_LITERAL= "StringLiteral";
    final static String NAME= "name";
    final static String KEY_WORD= "key word";
    final static String FUNCTION= "function";
    final static String VARIATION= "variation";

    final static String CODE_BLOCK = "code block";
    final static String CONTROL = "control";
    final static String OPERATION = "operation";
    final static String OBJECT= "object";

    final static String VOID= "void";

    static {
        logicDictionary.put("add_2", new String[]{"add result a b", "a", "b"});
        logicDictionary.put("add_0", new String[]{"add result "});
    }

    int current;

    /*
     * @param
     * @return paren opt number string name
     */




    Tree tokenizer(String input) throws Exception {
        int current = 0;


        Tree tokens=new Tree();
        while (current < input.length()) {

            char character = input.charAt(current);

            if (Pattern.compile("[(){}]").matcher(String.valueOf(character)).find()) {


                tokens.addNode(new  Tree.Node(TYPE,PAREN, VALUE,character));
                current++;
                continue;
            }
            if (Pattern.compile("[+\\-*/=<>]").matcher(String.valueOf(character)).find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[+\\-*/=<>]").matcher(String.valueOf(character)).find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new Tree.Node(TYPE,OPERATION,VALUE,value.toString()));
                continue;
//                tokens.addNode(new  Tree.Node(TYPE,OPERATION, VALUE,character));
//                current++;
//                continue;
            }

            if (Pattern.compile("[\s,\n]").matcher(character + "").find()) {
                current++;
                continue;
            }


            if (Pattern.compile("[0-9]").matcher(String.valueOf(character)).find()) {

                StringBuilder value = new StringBuilder();

                while (Pattern.compile("[0-9.]").matcher(String.valueOf(character)).find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new Tree.Node(TYPE,NUMBER,VALUE,value.toString()));
                continue;
            }


            if (character == '"') {
                character = input.charAt(++current);
                StringBuilder value = new StringBuilder();

                while (character != '"') {
                    value.append(character);
                    character = input.charAt(++current);
                }

                tokens.addNode(new Tree.Node(TYPE,STRING, VALUE, value));
                continue;
            }


            if (Pattern.compile("[a-z]").matcher(character + "").find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[a-z.]").matcher(character + "").find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new Tree.Node(TYPE,NAME,VALUE,value.toString()));
                continue;
            }


            throw new Exception("I don't know what this character is: " + character);

        }


        return tokens;
    }
    final static int maxPriority;
    final static HashMap<String, String[]> OperationSet;

    static {
        maxPriority = 10;


        OperationSet = new HashMap<>();
        OperationSet.put("*", new String[]{"10","mul"});
        OperationSet.put("+", new String[]{"9","add"});
        OperationSet.put("=", new String[]{"3","set"});

    }

    /*
    * @param
    * @return
    */
    int currentOperationClass= maxPriority;


    //Tree newParser(Tree)

    Tree parser(Tree tokens) throws Exception {
        current = 0;
        Tree ast = new Tree();

       // ast.root.putData(TYPE, PROGRAM).putData(TYPE, BODY);



        while (current < tokens.root.children.size()) {
            ast.addNode( walk(tokens));
        }




        //here no current use
        //OOperation Great




        while ( currentOperationClass > 0 ) {
            current=0;

            new Tree.visitor() {

                @Override
                public Object doWithSelf(Tree.Node node,ArrayList<Object> dataFromChildren) {

                    return null;
                }

                @Override
                public Object doWithChild(Tree.Node node, Tree.Node parent) {

                    switch (node.getStringData(TYPE)) {
//
                        case OPERATION -> {


                            String name = node.getStringData(VALUE);


                            if (currentOperationClass == Integer.valueOf(OperationSet.get(name)[0])) {
                                node.left.setParent(node);
                                node.right.setParent(node);
                                node.putData(TYPE, EXPRESSION, VALUE, OperationSet.get(name)[1]);
                            }



                        }

                    }

                    return null;
                }
            }.walk(ast);
//
       //     currentOperationClass--;

				currentOperationClass=8;
        }


        new Tree.visitor(){


            @Override
            public Object doWithSelf(Tree.Node node, ArrayList<Object> dataFromChildren) {

                switch (node.getStringData(TYPE)) {
                    case EXPRESSION -> {
                        if (node.singleChild()){


                            node.replacedBy(node.getLastChild());


                        }
                       // node.putData(TYPE, FUNCTION);
                    }

                }
                return null;
            }
            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {
                return null;
            }
        }.walk(ast);



        return ast;


    }




    final static String EXPRESSION = "expression";// used by compiler

/*
* @param tokens
* @return numberLiteral StringLiteral FUNCTION Variation ?optMark control
 */
    private Tree.Node walk(Tree tokens) throws Exception {

        //an awful design to use parems here

        Tree.Node tokenLeft = tokens.getNode(current);
        String type= tokenLeft.getStringData(TYPE);
        String value= tokenLeft.getStringData(VALUE);

        switch (type) {
            case NUMBER,STRING -> {
                current++;
                return new Tree.Node(TYPE,NUMBER_LITERAL,VALUE,value);
            }
            case OPERATION -> {
                current++;
                return new Tree.Node(TYPE,OPERATION,VALUE,value);

            }

            case PAREN -> {
                Tree.Node token = tokenLeft;
                tokenLeft=tokens.getNode(current-1);
                //because name comes before parn    (tokenLeft.getStringData(VALUE).matches("if|else|while")) &
                //just check if it is a block
                if ( Objects.equals(token.getStringData(VALUE), "(")) {

                    Tree.Node node = new Tree.Node(TYPE, EXPRESSION, VALUE,getExpressionName(current) );
                    tokenLeft = tokens.getNode(++current);//now is right ...

                    while ((!Objects.equals(tokenLeft.getStringData(TYPE), PAREN)) || (
                            Objects.equals(tokenLeft.getStringData(TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(VALUE), ")")) {
                        node.addChild(walk(tokens));
                        tokenLeft = tokens.getNode(current);
                    }
                    current++;
                    return node;
                }else {
                    //deal with code block may use it
                    return null;
                }


            }

            case  NAME-> {

                Tree.Node tokenWithName = tokenLeft;
                tokenLeft = tokens.getNode(++current);
                //detect if this is a function or variable

                Tree.Node node;
                if (tokenWithName.getStringData(VALUE).matches("if|else|while")) {

                    node = new Tree.Node(TYPE, CONTROL, VALUE, tokenWithName.getStringData(VALUE));


                    node.addChild(walk(tokens));

                    tokenLeft = tokens.getNode(current);
                    if (Objects.equals(tokenLeft.getStringData(TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(VALUE), "{")) {


                        tokenLeft = tokens.getNode(++current);

                        while ((!Objects.equals(tokenLeft.getStringData(TYPE), PAREN)) || (
                                Objects.equals(tokenLeft.getStringData(TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(VALUE), "}")) {
                            node.addChild(walk(tokens));
                            tokenLeft = tokens.getNode(current);
                        }
                        current++;

                    }
                    return node;


                } else {

                    if (Objects.equals(tokenLeft.getStringData(TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(VALUE), "(")) {
                        node = new Tree.Node(TYPE, FUNCTION, VALUE, tokenWithName.getStringData(VALUE));

                        tokenLeft = tokens.getNode(++current);

                        while ((!Objects.equals(tokenLeft.getStringData(TYPE), PAREN)) || (
                                Objects.equals(tokenLeft.getStringData(TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(VALUE), ")")) {
                            node.addChild(walk(tokens));
                            tokenLeft = tokens.getNode(current);
                        }
                        current++;
                        return node;
                    } else {
                        return new Tree.Node(TYPE, VARIATION, VALUE, tokenWithName.getStringData(VALUE));
                    }
                }


            }


            default -> {
                throw new Exception(type);

            }



        }




    }


    private String getExpressionName(int index) {
        return "EXPRESSION_" + index + "_ignore";
    }


//    private void traverser(Tree ast) throws Exception {
//        traverseNode(ast, null);
//
//    }
//
//    private void traverseArray(ArrayList array, HashMap<String, Object> parent) throws Exception {
//        for (Object child : array) {
//            traverseNode((HashMap<String, Object>) child, parent);
//
//        }
//    }
//
//    private void traverseNode(Tree.node node, Tree parent) throws Exception {
//        // String methods= (String) node.get("name");
//        String type = (String) node.get("type");
//        visitor methods = new visitor(type);
//        if (methods.enter) {
//            methods.enter(node, parent);
//        }
//        switch (type) {
//            case "Program":
//                traverseArray((ArrayList) node.get("body"), node);
//                break;
//
//
//            case "CallExpression":
//                traverseArray((ArrayList) node.get("parems"), node);
//                break;
//
//
//            case "NumberLiteral":
//            case "StringLiteral":
//            case "CallVariation":
//                break;
//
//
//            default:
//                throw new Exception(type);
//        }
//        if (methods.exit) {
//            methods.exit(node, parent);
//        }
//    }
//
//
//
//   Tree transformer(Tree ast) throws Exception {
//
//        Tree newAst = new Tree();
//        "_context", newAst.get("body");
//        ast.addNode();
//        traverser(ast);
//        return newAst;
//
//    }
//
//    String argumentDistribute(HashMap<String, Object> node, ArrayList<HashMap<String, Object>> array, int Index) throws Exception {
//
//
//        HashMap<String, Object> unit = new HashMap<>();
//        int index = Index;
//        String result = getArgument(Index);
//        //unit.put()
//        switch (node.get("type").toString()) {
//            case "Program":
//                ArrayList<Object> body = ((ArrayList<Object>) node.get("body"));
//                for (Object child : body) {
//
//
//                    argumentDistribute(((HashMap<String, Object>) child), array, ++index);
//
//
//                }
//
//                break;
//
//
//            //return codeOutput.toString();
//
//            case "ExpressionStatement":
//                //return codeGenerator((HashMap<String, Object>) node.get("expression"),codeOutput,parentIndex) + ";"
//
//                argumentDistribute((HashMap<String, Object>) node.get("expression"), array, index);
//
//                break;
//            //return codeGenerator(,codeOutput,Index) + ";";
//            case "CallExpression":
//
//                //code = new StringBuilder(codeGenerator((HashMap<String, Object>) node.get("callee"),codeOutput,parentIndex) + "(");
//
//                ArrayList<Object> arguments = ((ArrayList<Object>) node.get("arguments"));
//                ArrayList<String> argumentList = new ArrayList<>();
//                if (arguments != null) {
//                    for (Object child : arguments) {
//                        argumentList.add(argumentDistribute(((HashMap<String, Object>) child), array, ++index));
//
//                        //code.append(codeGenerator(((HashMap<String, Object>) child),codeOutput,parentIndex)).append(" ");
//
//                    }
//                }
//
//                if (node.get("callee") != null) {
//                    unit.put("name", ((HashMap<String, Object>) node.get("callee")).get("name"));
//                } else {
//                    unit.put("name", node.get("name"));
//                }
//
//                unit.put("arguments", argumentList);
//                unit.put("result", result);
//                array.add(unit);
//                return result;
//
//
//            case "CallVariation":
//
//
//            case "NumberLiteral":
//
//
//            case "StringLiteral":
//
//
//                return (String) node.get("value");
//
//            case "Identifier":
//
//                break;
//            default:
//                throw new Exception(node.get("type").toString());
//
//
//        }
//        return null;
//
//
//    }
//
//
//    String codeGenerator(Tree ast) {
//        StringBuilder codeBuffer = new StringBuilder();
//        for (Tree.Node codeSetting : ast.root.children
//        ) {
//
//            ArrayList<String> arguments = ((ArrayList<String>) codeSetting.getData("arguments"));
//
//
//            String code = getCode(codeSetting.getStringData("name"), arguments);
//            code = code.replaceAll("result", codeSetting.getStringData("result"));
//
//            codeBuffer.append(code).append('\n');
//
//
//        }
//        return codeBuffer.toString();
//    }
//
//    private String getCode(String name, ArrayList<String> arguments) {
//
//        String[] codeSetting = logicDictionary.get(name + '_' + arguments.size());
//        String code = codeSetting[0];
//
//        int i = 0;
//        for (String argument :
//                arguments) {
//
//
//            code = code.replaceAll("\\s"+codeSetting[++i]+"\\s", "\\s"+argument+"\\s");
//
//        }
//
//
//        return code;
//    }
//

    private String getArgument(int index) {
        return "Argument_" + index + "_ignore";
    }

//
//
//    private static class visitor {
//        final String type;
//        final boolean enter;
//
//        boolean exit;
//
//        visitor(String type) {
//            this.type = type;
//            enter = true;
//            exit = false;
//        }
//
//
//        public void enter(HashMap<String, Object> node, Tree parent) {
//            switch (type) {
//                case "CallExpression" : {
//                    HashMap<String, Object> expression = new HashMap<>();
//                    expression.put("type", "CallExpression");
//                    HashMap<String, Object> callee = new HashMap<>();
//                    callee.put("type", "Identifier");
//                    callee.put("name", node.get("value"));
//                    expression.put("callee", callee);
//                    expression.put("arguments", new ArrayList<>());
//                    node.put("_context", expression.get("arguments"));
//                    if (parent.get("type") != "CallExpression") {
//
//                        HashMap<String, Object> expression2 = new HashMap<>();
//                        expression2.put("type", "ExpressionStatement");
//                        expression2.put("expression", expression);
//
//                        ((ArrayList<Object>) parent.get("_context")).add(expression2);
//                    } else {
//
//                        ((ArrayList<Object>) parent.get("_context")).add(expression);
//                    }
//                }
//                case "NumberLiteral":
//								case"StringLiteral":
//								case"CallVariation" :
//                        ((ArrayList) parent.get("_context")).add(compiler.dic(type, (String) node.get("value"), null, null));
//            }
//        }
//
//        @SuppressWarnings("EmptyMethod")
//        public void exit(HashMap<String, Object> node, HashMap<String, Object> parent) {
//        }
//    }
}
