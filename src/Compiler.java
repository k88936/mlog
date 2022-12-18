import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

//todo translation 



public class Compiler {
    static final HashMap<String, String[]> logicDictionary = new HashMap<>();
    final static String TREE_TYPE = "type";
    final static String TREE_VALUE = "value";
    final static String TOKENS = "tokens";
    final static String PAREN = "paren";
    final static String NUMBER = "number";
    final static String AST_TYPE_NUMBER_LITERAL = "NumberLiteral";
    final static String STRING = "string";
    final static String AST_TYPE_STRING_LITERAL_ = "StringLiteral";
    final static String TREE_NAME = "name";
    final static String KEY_WORD = "key word";
    final static String AST_TYPE_FUNCTION = "function";
    final static String AST_TYPE_VARIATION = "variation";

   // final static String CODE_BLOCK = "code block";
    final static String AST_TYPE_CONTROL = "control";
    final static String AST_TYPE_OPERATION = "operation";
    //final static String OBJECT = "object";

    //final static String VOID = "void";
    final static int maxPriority;
    final static HashMap<String, String[]> OperationSet;


    final static String AST_TYPE_EXPRESSION = "expression";// used by Compiler
    final static String CONTROL_PATTERN_SIGN = "[+\\-*/=<>]";
    final static String CONTROL_PATTERN = "if|else|while|break|continue";
    final static String AST_FUNCTION_RETURN_TYPE = "returnType";
    final static String AST_VARIATION_DATA_TYPE = "dataType";


     static final DataType DATA_UNDEFINED,DATA_ITEM_TYPE,DATA_UNIT,DATA_BLOCK,DATA_STRING,DATA_NUMBER,DATA_OBJECT;
    static {
         DATA_OBJECT = DataType.OBJECT;
         DATA_NUMBER = DataType.createDataType("Num");
         DATA_STRING = DataType.createDataType("Str");
         DATA_BLOCK = DataType.createDataType("Block");
         DATA_UNIT =DataType.createDataType("Unit");

        DATA_ITEM_TYPE = DataType.createDataType("Item");

        DATA_UNDEFINED = DataType.createDataType("Undefined");
        // final DataType DATA_BOOLEAN = DataType.createDataType();
        //final DataType DATA_NULL = DataType.createDataType();
    }



    static {

        logicDictionary.put("add_2", new String[]{"add result a b", "a", "b"});
        logicDictionary.put("add_0", new String[]{"add result "});
    }

    static {
        maxPriority = 10;


        OperationSet = new HashMap<>();
        OperationSet.put("*", new String[]{"10", "mul", "double"});
        OperationSet.put("+", new String[]{"9", "add", "double"});
        OperationSet.put("=", new String[]{"3", "set", "double"});
        OperationSet.put("++", new String[]{"9", "selfAdd", "opt-left"});

    }

    int current;
    /*
     * @param
     * @return
     */ int currentOperationClass = maxPriority;

    Tree tokenizer(String input) throws Exception {
        int current = 0;


        Tree tokens = new Tree();
        while (current < input.length()) {

            char character = input.charAt(current);

           //System.out.println(character);
            if (Pattern.compile("[(){}]").matcher(String.valueOf(character)).find()) {


                tokens.addNode(new Tree.Node(TREE_TYPE, PAREN, TREE_VALUE, character));
                current++;
                continue;
            }
            if (Pattern.compile(CONTROL_PATTERN_SIGN).matcher(String.valueOf(character)).find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile(CONTROL_PATTERN_SIGN).matcher(String.valueOf(character)).find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value.toString()));
                continue;
            }

            if (Pattern.compile("[\s,\r\n;]").matcher(character + "").find()) {
                current++;
                continue;
            }


            if (Pattern.compile("[0-9]").matcher(String.valueOf(character)).find()) {

                StringBuilder value = new StringBuilder();

                while (Pattern.compile("[0-9.]").matcher(String.valueOf(character)).find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new Tree.Node(TREE_TYPE, NUMBER, TREE_VALUE, value.toString()));
                continue;
            }


            if (character == '"') {
                character = input.charAt(++current);
                StringBuilder value = new StringBuilder();

                while (character != '"') {
                    value.append(character);
                    character = input.charAt(++current);
                }

                tokens.addNode(new Tree.Node(TREE_TYPE, STRING, TREE_VALUE, value));
                continue;
            }


            //TODO upper litter
            if (Pattern.compile("[a-z]").matcher(character + "").find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[a-z.:]").matcher(character + "").find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }

                tokens.addNode(new Tree.Node(TREE_TYPE, TREE_NAME, TREE_VALUE, value.toString()));
                continue;
            }


            //throw new Exception("I don't know what this character is: " + character);

        }


        return tokens;
    }
    final static String IGNORE = "ignore";
    Tree preParser(Tree tokens, String part) {

        Tree partOfTokens= new Tree();
        final boolean[] included = {false};
        new Tree.visitor() {

            @Override
            public Object doWithSelf(Tree.Node self, ArrayList<Object> dataFromChildren) {
                return null;
            }

            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {
                if (TREE_NAME.equals(child.getStringData(TREE_TYPE))&&child.getStringData(TREE_VALUE).endsWith(":")){

                    if (child.getStringData(TREE_VALUE).contains(part)&&!included[0]) {

                        included[0] =true;

                        return null;
                    }else {
                        included[0] =false;
                    }

                }
                if (included[0]) {
                    partOfTokens.addNode(child);

                }

                return null;
            }
        }.visit(tokens);

        return partOfTokens;
    }

    Tree NParser(Tree tokens) throws Exception {
        current=0;
        Tree ast = new Tree();
        while (this.current < tokens.root.children.size()) {
            ast.addNode(this.NWalk(tokens));
        }





        return null;
    }
    private Tree.Node NWalk(Tree tokens) throws Exception {

        //an awful design to use parems here

        Tree.Node tokenLeft = tokens.getNode(this.current);
        String type = tokenLeft.getStringData(TREE_TYPE);
        String value = tokenLeft.getStringData(TREE_VALUE);

        switch (type) {
            case NUMBER, STRING -> {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_NUMBER_LITERAL, TREE_VALUE, value);
            }
            case AST_TYPE_OPERATION -> {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value);

            }

            case PAREN -> {
                Tree.Node token = tokenLeft;
                tokenLeft = tokens.getNode(this.current - 1);
                //because name comes before paren    (tokenLeft.getStringData(VALUE).matches("if|else|while")) &
                //just check if it is a block
                if (Objects.equals(token.getStringData(TREE_VALUE), "(")) {

                    Tree.Node node = new Tree.Node(TREE_TYPE, AST_TYPE_EXPRESSION, TREE_VALUE, this.getExpressionName(this.current));
                    tokenLeft = tokens.getNode(++this.current);//now is right ...

                    while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), ")")) {
                        node.addChild(this.NWalk(tokens));
                        tokenLeft = tokens.getNode(this.current);
                    }
                    this.current++;
                    return node;
                } else {
                    //deal with code block may use it
                    return null;
                }


            }

            case TREE_NAME -> {

                Tree.Node tokenWithName = tokenLeft;
                tokenLeft = tokens.getNode(++this.current);



                Tree.Node node;

                //is a "if" or "else"
                if (tokenWithName.getStringData(TREE_VALUE).matches(CONTROL_PATTERN)) {

                    node = new Tree.Node(TREE_TYPE, AST_TYPE_CONTROL, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));


                    node.addChild(this.NWalk(tokens));

                    tokenLeft = tokens.getNode(this.current);
                    if (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(TREE_VALUE), "{")) {


                        tokenLeft = tokens.getNode(++this.current);

                        while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), "}")) {
                            node.addChild(this.NWalk(tokens));
                            tokenLeft = tokens.getNode(this.current);
                        }
                        this.current++;

                    }
                    return node;


                } else {

                    //is a func or var
                    if (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(TREE_VALUE), "(")) {
                        node = new Tree.Node(TREE_TYPE, AST_TYPE_FUNCTION, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));

                        tokenLeft = tokens.getNode(++this.current);

                        while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), ")")) {
                            node.addChild(this.NWalk(tokens));
                            tokenLeft = tokens.getNode(this.current);
                        }
                        this.current++;
                        return node;
                    } else {
                        return new Tree.Node(TREE_TYPE, AST_TYPE_VARIATION, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));
                    }
                }


            }


            default -> {
                throw new Exception(type);

            }


        }


    }


    Tree parser(Tree tokens) throws Exception {
        this.current = 0;
        Tree ast = new Tree();

        // ast.root.putData(TYPE, PROGRAM).putData(TYPE, BODY);


        while (this.current < tokens.root.children.size()) {
            ast.addNode(this.walk(tokens));
        }


        //here no current use
        //OOperation Great


        while (this.currentOperationClass > 0) {
            this.current = 0;

            new Tree.visitor() {

                @Override
                public Object doWithSelf(Tree.Node node, ArrayList<Object> dataFromChildren) {

                    return null;
                }

                @Override
                public Object doWithChild(Tree.Node node, Tree.Node parent) {

                    //
                    if (AST_TYPE_OPERATION.equals(node.getStringData(TREE_TYPE))) {
                        String name = node.getStringData(TREE_VALUE);


                        if (Compiler.this.currentOperationClass == Integer.valueOf(OperationSet.get(name)[0])) {


                            switch (OperationSet.get(name)[2]) {
                                case "double" -> {
                                    node.left.setParent(node);
                                    node.right.setParent(node);

                                }
                                case "opt-right" -> {

                                    node.right.setParent(node);

                                }
                                case "opt-left" -> {
                                    node.left.setParent(node);

                                }


                            }
                            node.putData(TREE_TYPE, AST_TYPE_EXPRESSION, TREE_VALUE, OperationSet.get(name)[1]);
                        }
                    }

                    return null;
                }
            }.visit(ast);
//
            this.currentOperationClass--;

            //
        }









        //here to clear unused

        new Tree.visitor() {


            @Override
            public Object doWithSelf(Tree.Node node, ArrayList<Object> dataFromChildren) {

                if (AST_TYPE_EXPRESSION.equals(node.getStringData(TREE_TYPE))) {
                    if (node.singleChild() && (node.getStringData(TREE_VALUE).endsWith(IGNORE))) {


                        node.replacedBy(node.getLastChild());


                    }
                    // node.putData(TYPE, FUNCTION);
                }
                return null;
            }

            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {
                return null;
            }
        }.visit(ast);


        return ast;


    }

    /*
     * @param tokens
     * @return numberLiteral StringLiteral FUNCTION Variation ?optMark control
     */
    private Tree.Node walk(Tree tokens) throws Exception {

        //an awful design to use parems here

        Tree.Node tokenLeft = tokens.getNode(this.current);
        String type = tokenLeft.getStringData(TREE_TYPE);
        String value = tokenLeft.getStringData(TREE_VALUE);

        switch (type) {
            case NUMBER, STRING -> {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_NUMBER_LITERAL, TREE_VALUE, value);
            }
            case AST_TYPE_OPERATION -> {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value);

            }

            case PAREN -> {
                Tree.Node token = tokenLeft;
                tokenLeft = tokens.getNode(this.current - 1);
                //because name comes before paren    (tokenLeft.getStringData(VALUE).matches("if|else|while")) &
                //just check if it is a block
                if (Objects.equals(token.getStringData(TREE_VALUE), "(")) {

                    Tree.Node node = new Tree.Node(TREE_TYPE, AST_TYPE_EXPRESSION, TREE_VALUE, this.getExpressionName(this.current));
                    tokenLeft = tokens.getNode(++this.current);//now is right ...

                    while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), ")")) {
                        node.addChild(this.walk(tokens));
                        tokenLeft = tokens.getNode(this.current);
                    }
                    this.current++;
                    return node;
                } else {
                    //deal with code block may use it
                    return null;
                }


            }

            case TREE_NAME -> {

                Tree.Node tokenWithName = tokenLeft;
                tokenLeft = tokens.getNode(++this.current);



                Tree.Node node;

                //is a "if" or "else"
                if (tokenWithName.getStringData(TREE_VALUE).matches(CONTROL_PATTERN)) {

                    node = new Tree.Node(TREE_TYPE, AST_TYPE_CONTROL, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));


                    node.addChild(this.walk(tokens));

                    tokenLeft = tokens.getNode(this.current);
                    if (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(TREE_VALUE), "{")) {


                        tokenLeft = tokens.getNode(++this.current);

                        while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), "}")) {
                            node.addChild(this.walk(tokens));
                            tokenLeft = tokens.getNode(this.current);
                        }
                        this.current++;

                    }
                    return node;


                } else {

                    //is a func or var
                    if (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(TREE_VALUE), "(")) {
                        node = new Tree.Node(TREE_TYPE, AST_TYPE_FUNCTION, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));

                        tokenLeft = tokens.getNode(++this.current);

                        while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), ")")) {
                            node.addChild(this.walk(tokens));
                            tokenLeft = tokens.getNode(this.current);
                        }
                        this.current++;
                        return node;
                    } else {
                        return new Tree.Node(TREE_TYPE, AST_TYPE_VARIATION, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));
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

    //todo语义分析
    public Tree semanticParser(Tree ast) {


        //todo register the functions
        new Tree.visitor() {

            @Override
            public Object doWithSelf(Tree.Node self, ArrayList<Object> dataFromChildren) {
                return null;
            }

            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {

                if (AST_TYPE_FUNCTION.equals(child.getStringData(TREE_TYPE)) && "set".equals(child.getStringData(TREE_VALUE))) {




                }




                    return null;
            }
        }.visit(ast);


        //register variation

        HashMap<String, DataType> variations = new HashMap();
        new Tree.visitor() {

            @Override
            public Object doWithSelf(Tree.Node self, ArrayList<Object> dataFromChildren) {
                return null;
            }

            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {

                if (AST_TYPE_EXPRESSION.equals(child.getStringData(TREE_TYPE)) && "set".equals(child.getStringData(TREE_VALUE))) {
                    DataType dataType = DATA_UNDEFINED;
                    if (AST_TYPE_FUNCTION.equals(child.getLastChild().getStringData(TREE_TYPE))) {
                        //数据类型

                        dataType = (DataType) child.getLastChild().getData(AST_FUNCTION_RETURN_TYPE);
                    } else if (AST_TYPE_VARIATION.equals(child.getLastChild().getStringData(TREE_TYPE))) {
                        dataType = (DataType) child.getLastChild().getData(AST_VARIATION_DATA_TYPE);
                    }
                    variations.put(child.getFirstChild().getStringData(TREE_VALUE), dataType);
                }
                return null;
            }
        }.visit(ast);
        new Tree.visitor() {

            @Override
            public Object doWithSelf(Tree.Node self, ArrayList<Object> dataFromChildren) {
                return null;
            }

            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {

                if (AST_TYPE_VARIATION.equals(child.getStringData(TREE_TYPE))) {
                    //todo what todo
                    variations.containsKey(child.getFirstChild().getStringData(TREE_VALUE));
                }
                return null;
            }
        }.visit(ast);


        return ast;


    }



    public void translator() {

    }



















    //todo far far

    String codeGenerator(Tree ast) {

        StringBuilder codeBuffer = new StringBuilder();


        new Tree.visitor() {

            @Override
            public Object doWithSelf(Tree.Node self, ArrayList<Object> dataFromChildren) {
                return null;
            }

            @Override
            public Object doWithChild(Tree.Node child, Tree.Node parent) {


                switch (child.getStringData(TREE_TYPE)) {

                    case AST_TYPE_CONTROL -> {
                        switch (child.getStringData(TREE_NAME)) {
                            case "if", "while" -> {

                            }
                        }
                    }
                    //todo
                    case AST_TYPE_FUNCTION -> {
                        logicDictionary.get("");

                    }


                }


                return null;
            }
        }.visit(ast);

        for (Tree.Node codeSetting : ast.root.children) {

            ArrayList<String> arguments = ((ArrayList<String>) codeSetting.getData("arguments"));


            String code = this.getCode(codeSetting.getStringData("name"), arguments);
            code = code.replaceAll("result", codeSetting.getStringData("result"));

            codeBuffer.append(code).append('\n');


        }
        return codeBuffer.toString();
    }

    private String getCode(String name, ArrayList<String> arguments) {

        String[] codeSetting = logicDictionary.get(name + '_' + arguments.size());
        String code = codeSetting[0];

        int i = 0;
        for (String argument : arguments) {


            code = code.replaceAll("\\s" + codeSetting[++i] + "\\s", "\\s" + argument + "\\s");

        }


        return code;
    }


    private String getArgument(int index) {
        return "Argument_" + index + "_ignore";
    }

}
