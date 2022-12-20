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
    final static String TREE_TOKEN_PAREN = "paren";
    final static String TREE_TOKEN_NUMBER = "number";
    final static String AST_TYPE_NUMBER_LITERAL = "NumberLiteral";
    final static String TREE_TOKEN_STRING = "string";
    final static String AST_TYPE_STRING_LITERAL = "StringLiteral";
    final static String TREE_TOKEN_NAME = "name";
    final static String KEY_WORD = "key word";
    final static String AST_TYPE_FUNCTION = "function";
    final static String AST_TYPE_VARIATION = "variation";

    final static String AST_TYPE_CODE_BLOCK = "code block";
    final static String AST_TYPE_CONTROL = "control";
    final static String AST_TYPE_OPERATION = "operation";
    //final static String OBJECT = "object";

    //final static String VOID = "void";
    final static int maxPriority;
    final static HashMap<String, String[]> OperationSet;


    final static String AST_TYPE_EXPRESSION = "expression";// used by Compiler
    final static String CONTROL_PATTERN_SIGN = "[+\\-*/=<>]";
    final static String CONTROL_PATTERN = "if|else|while|break|continue|break|continue|return|switch|";
    final static String AST_FUNCTION_RETURN_TYPE = "returnType";
    final static String AST_VARIATION_DATA_TYPE = "dataType";


    static final DataType DATA_VOID, DATA_ITEM_TYPE, DATA_UNIT, DATA_BLOCK, DATA_STRING, DATA_NUMBER, DATA_OBJECT;
    static final String TYPE_PATTERN;
    final static String IGNORE = "ignore";
    final static String AST_TYPE_TYPE_DECLARATION = "typeDeclaration";
    final static String AST_FUNCTION_DEFINED = "defined";
    final static String AST_FUNCTION_CONTENT = "content";

    static {

        DATA_OBJECT = DataType.OBJECT;
        DATA_NUMBER = DataType.createDataType("num");
        DATA_STRING = DataType.createDataType("str");
        DATA_BLOCK = DataType.createDataType("block");
        DATA_UNIT = DataType.createDataType("unit");

        DATA_ITEM_TYPE = DataType.createDataType("item");

        DATA_VOID = DataType.createDataType("void");
        TYPE_PATTERN = DataType.getAllDataTypesNames();
        // System.out.println();
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

//    Tree NParser(Tree tokens) throws Exception {
//        this.current =0;
//        Tree ast = new Tree();
//        while (this.current < tokens.root.children.size()) {
//            ast.addNode(this.walk()alk(tokens));
//        }
//
//
//
//
//
//        return null;
//    }
    /*
     * @param
     * @return
     */ int currentOperationClass = maxPriority;

    Tree tokenizer(String input) throws Exception {

        //input=input;
        int current = 0;


        Tree tokens = new Tree();
        while (current < input.length()) {

            char character = input.charAt(current);

            //System.out.println(character);
            if (Pattern.compile("[(){}]").matcher(String.valueOf(character)).find()) {


                tokens.addNode(new Tree.Node(TREE_TYPE, TREE_TOKEN_PAREN, TREE_VALUE, character));
                current++;
                continue;
            }
            //+-*/
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
                // current++;
                tokens.addNode(new Tree.Node(TREE_TYPE, TREE_TOKEN_NUMBER, TREE_VALUE, value.toString()));
                continue;
            }


            if (character == '"') {
                character = input.charAt(++current);
                StringBuilder value = new StringBuilder();

                while (character != '"') {
                    value.append(character);
                    character = input.charAt(++current);
                }
                current++;
                tokens.addNode(new Tree.Node(TREE_TYPE, TREE_TOKEN_STRING, TREE_VALUE, value));
                continue;
            }


            //TODO upper litter
            if (Pattern.compile("[a-z]|[A-Z]|[_]").matcher(character + "").find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[a-z]|[A-Z]|[.:_]").matcher(character + "").find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }
                // current++;
                tokens.addNode(new Tree.Node(TREE_TYPE, TREE_TOKEN_NAME, TREE_VALUE, value.toString()));
                continue;
            }


            throw new Exception("I don't know what this character is: " + character);

        }


        return tokens;
    }

    Tree preParser(Tree tokens, String part) {

        Tree partOfTokens = new Tree();
        final boolean[] included = {false};
        new Tree.visitor() {

            // error at add the parent to the new one
            // this is right when use two arguments

            @Override
            public Object execute(Tree.Node child, ArrayList<Object> dataFromChildren) {


                if (TREE_TOKEN_NAME.equals(child.getStringData(TREE_TYPE)) && child.getStringData(TREE_VALUE).endsWith(":")) {

                    if (child.getStringData(TREE_VALUE).contains(part) && !included[0]) {

                        included[0] = true;

                        return null;
                    } else {
                        included[0] = false;
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

    static final String RENTURN_VAR="return var";
    Tree parser(Tree tokens) throws Exception {
        this.current = 0;
        Tree ast = new Tree();

        // ast.root.putData(TYPE, PROGRAM).putData(TYPE, BODY);


        while (this.current < tokens.root.children.size()) {
            ast.addNode(this.walk(tokens));
        }
        // System.out.println(ast);


        //here no current use
        //OOperation Great
        currentOperationClass=maxPriority;
        while (this.currentOperationClass > 0) {
            this.current = 0;


            new Tree.visitor() {

                @Override
                public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                    if (AST_TYPE_OPERATION.equals(node.getStringData(TREE_TYPE))) {
                        String name = node.getStringData(TREE_VALUE);


                        if (Compiler.this.currentOperationClass == Integer.valueOf(OperationSet.get(name)[0])) {


                            switch (OperationSet.get(name)[2]) {
                                case "double" :{
                                    node.left.setParent(node);
                                    node.jump=node.right;
                                    node.right.jump=node;
                                    node.right.setParent(node);
break;
                                }
                                case "opt-right" : {
                                    node.jump=node.right;
                                    node.right.jump=node;
                                    node.right.setParent(node);
                                    break;
                                }
                                case "opt-left" : {
                                    node.left.setParent(node);
                                    break;
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
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                if (AST_TYPE_EXPRESSION.equals(node.getStringData(TREE_TYPE))) {

                    if (node.singleChild() && (node.getStringData(TREE_VALUE).endsWith(IGNORE))) {


                        node.replacedBy(node.getLastChild());


                    }
                    //node.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                }
                if (AST_TYPE_CODE_BLOCK.equals(node.getStringData(TREE_TYPE))) {
                    if (node.singleChild() && (node.getStringData(TREE_VALUE).endsWith(IGNORE))) {


                        node.replacedBy(node.getLastChild());


                    }
                    //node.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                }
                //todo dangerous
                if ("set".equals(node.getStringData(TREE_VALUE))) {
                    if (node.children.size()==2 && (AST_TYPE_FUNCTION.equals(node.getLastChild().getStringData(TREE_TYPE)))) {


                        node.getLastChild().putData(RENTURN_VAR, node.getFirstChild().getStringData(TREE_VALUE));
                        node.replacedBy(node.getLastChild());


                    }
                    //node.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                }
                return null;
            }



        }.visit(ast);

        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {

                if (AST_TYPE_EXPRESSION.equals(node.getStringData(TREE_TYPE))) {


                    if (node.singleChild() && (node.getStringData(TREE_VALUE).endsWith(IGNORE))) {


                        node.replacedBy(node.getLastChild());


                    }
                    node.putData(TREE_TYPE, AST_TYPE_FUNCTION);

                }
                if (AST_TYPE_CODE_BLOCK.equals(node.getStringData(TREE_TYPE))) {
                    if (node.singleChild() && (node.getStringData(TREE_VALUE).endsWith(IGNORE))) {


                        node.replacedBy(node.getLastChild());


                    }

                }



                if (AST_TYPE_TYPE_DECLARATION.equals(node.getStringData(TREE_TYPE))) {

                    // troubled by replaced a right node
                    if (AST_TYPE_VARIATION.equals(node.right.getStringData(TREE_TYPE))) {

                        node.right.putData(AST_VARIATION_DATA_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
                        node.replacedBy(node.right);
                        return null;
                    }
                    if (AST_TYPE_FUNCTION.equals(node.right.getStringData(TREE_TYPE))) {

                        node.right.putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
                        node.right.putData(AST_FUNCTION_DEFINED, false);
                        //here I put a data and give up add to child
                        node.right.putData(AST_FUNCTION_CONTENT, node.right.right.removeFromParent());
                        node.replacedBy(node.right);
                        return null;

                    }
                    if (AST_TYPE_EXPRESSION.equals(node.right.getStringData(TREE_TYPE)) && "set".equals(node.right.getStringData(TREE_TOKEN_NAME))) {
                        node.right.putData(AST_FUNCTION_RETURN_TYPE,DataType.getDataType(node.getStringData(TREE_VALUE)));
                        node.right.getFirstChild().putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
                        node.replacedBy(node.right);
                        return null;

                    }
                }
                return null;
            }



        }.visit(ast);

        //make sure tidy


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


            case TREE_TOKEN_NUMBER : {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_NUMBER_LITERAL, TREE_VALUE, value);
                //break;
            }
            case TREE_TOKEN_STRING : {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_STRING_LITERAL, TREE_VALUE, value);
                //break;
            }
            case AST_TYPE_OPERATION : {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value);
               // break;
            }

            case TREE_TOKEN_PAREN : {
                Tree.Node token = tokenLeft;
                tokenLeft = tokens.getNode(this.current - 1);
                //because name comes before paren    (tokenLeft.getStringData(VALUE).matches("if|else|while")) &
                //just check if it is a block
                if (Objects.equals(token.getStringData(TREE_VALUE), "(")) {

                    Tree.Node node = new Tree.Node(TREE_TYPE, AST_TYPE_EXPRESSION, TREE_VALUE, this.getExpressionName(this.current));
                    tokenLeft = tokens.getNode(++this.current);//now is right ...

                    while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), ")")) {
                        node.addChild(this.walk(tokens));
                        tokenLeft = tokens.getNode(this.current);
                    }
                    this.current++;
                    return node;
                } else if (Objects.equals(token.getStringData(TREE_VALUE), "{")) {
                    Tree.Node node = new Tree.Node(TREE_TYPE, AST_TYPE_CODE_BLOCK, TREE_VALUE, this.getCodeBlockName(this.current));
                    tokenLeft = tokens.getNode(++this.current);//now is right ...

                    while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), "}")) {
                        node.addChild(this.walk(tokens));
                        tokenLeft = tokens.getNode(this.current);
                    }
                    this.current++;
                    return node;

                } else {
                    return null;
                }


               // break;
            }

            case TREE_TOKEN_NAME : {

                Tree.Node tokenWithName = tokenLeft;
                //here already current plus
                tokenLeft = tokens.getNode(++this.current);


                Tree.Node node;
                //is a var type declaration
                if (tokenWithName.getStringData(TREE_VALUE).matches(TYPE_PATTERN)) {
                    node = new Tree.Node(TREE_TYPE, AST_TYPE_TYPE_DECLARATION, TREE_VALUE, value);
                    //this.current++;
                    return node;

                } else if (tokenWithName.getStringData(TREE_VALUE).matches(CONTROL_PATTERN)) {

                    //is an "if" or "else"
                    node = new Tree.Node(TREE_TYPE, AST_TYPE_CONTROL, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));


                    boolean wantExpression = false;
                    boolean wantAction = false;
                    boolean actAsKeyword = false;
                    switch (tokenWithName.getStringData(TREE_VALUE)) {
                        case "if", "for", "while":
                            wantExpression = true;

                            break;
                        case "else":

                            wantAction = true;
                            break;
                        case "return":

                            actAsKeyword = true;
                            break;


                        default:


                    }


                    if (wantExpression) {
                        Tree.Node expressionNode = this.walk(tokens);
                        if (AST_TYPE_EXPRESSION.equals(expressionNode.getStringData(TREE_TYPE))) {
                            node.addChild(expressionNode);
                        } else {
                            throw new Exception("no condition");
                        }
                    }
                    if (wantAction) {
                        Tree.Node actionNode = this.walk(tokens);
                        if (AST_TYPE_CODE_BLOCK.equals(actionNode.getStringData(TREE_TYPE))) {
                            node.addChild(actionNode);
                        } else {
                            throw new Exception("no methods");
                        }
                    }
                    if (actAsKeyword) {
                        //todo lazy to rename
                        Tree.Node actionNode = this.walk(tokens);
                        node.addChild(actionNode);


                    }


//                    tokenLeft = tokens.getNode(this.current);
//                    if (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN) & Objects.equals(tokenLeft.getStringData(TREE_VALUE), "{")) {
//
//
//                        tokenLeft = tokens.getNode(++this.current);
//
//                        while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), "}")) {
//                            node.addChild(this.walk(tokens));
//                            tokenLeft = tokens.getNode(this.current);
//                        }
//                        this.current++;
//
//                    }

                    return node;


                } else {

                    //is a func or var
                    if (Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN) & Objects.equals(tokenLeft.getStringData(TREE_VALUE), "(")) {
                        node = new Tree.Node(TREE_TYPE, AST_TYPE_FUNCTION, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));

                        //func
                        tokenLeft = tokens.getNode(++this.current);

                        while ((!Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) || (Objects.equals(tokenLeft.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) & !Objects.equals(tokenLeft.getStringData(TREE_VALUE), ")")) {
                            node.addChild(this.walk(tokens));
                            tokenLeft = tokens.getNode(this.current);
                        }
                        this.current++;
                        return node;
                    } else {
                        //var
                        return new Tree.Node(TREE_TYPE, AST_TYPE_VARIATION, TREE_VALUE, tokenWithName.getStringData(TREE_VALUE));
                    }
                }

               // break;
            }


            default :{

                throw new Exception(type);
                //break;
            }


        }


    }


    private String getExpressionName(int index) {
        return "EXPRESSION_" + index + "_ignore";
    }

    private String getCodeBlockName(int index) {
        return "CODE_BLOCK_" + index + "_ignore";
    }

   static final String  AST_FUNCTION_ID=Library.FUNC_ID;
    //todo 语义分析 but not a loader
    public Tree semanticParser(Tree ast) {





        //todo register the functions
        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                switch (node.getStringData(TREE_TYPE)) {
                    case AST_TYPE_FUNCTION:


                        if (node.getData(AST_FUNCTION_DEFINED)!= null) {
                            return null;
                        }

                        String nameSpace="native";
                        String name=node.getStringData(Compiler.TREE_VALUE);
                        if (name.contains(".") ){
                            String[] callerSet =name.split("\\.");

                            nameSpace=callerSet[0];
                            name=callerSet[1];
                        }

                        DataType[]  argTypes = new DataType[dataFromChildren.size()];
                        for (int i = 0; i < dataFromChildren.size(); i++) {


                            if (dataFromChildren.get(i) != null) {
                                argTypes[i] = (DataType) dataFromChildren.get(i);
                            }else {
                                argTypes[i] = DataType.OBJECT;
                            }


//                            switch (node.children.get(i).getStringData(Compiler.TREE_TYPE)){
//                                case Compiler.AST_TYPE_FUNCTION:{
//                                    argTypes[i]= (DataType) node.getChild(i).getData(Compiler.AST_FUNCTION_RETURN_TYPE);
//                                    break;
//                                }
//                                case Compiler.AST_TYPE_VARIATION: {
//                                    argTypes[i] = (DataType) node.getChild(i).getData(Compiler.AST_VARIATION_DATA_TYPE);
//                                    break;
//                                }
//                            }
                        }

                        try {

                            Object[] information = Library.getReturnDataType(nameSpace, name, argTypes);
                            node.putData(AST_FUNCTION_RETURN_TYPE, information[0]);
                            node.putData(AST_FUNCTION_ID, information[1]);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                            //e.printStackTrace();
                        }

                        return node.getData(AST_FUNCTION_RETURN_TYPE);




                        //break;
                    case AST_TYPE_VARIATION:
                        if (node.getStringData(AST_VARIATION_DATA_TYPE)!= null){

                        }else {
                            node.putData(AST_VARIATION_DATA_TYPE, DATA_OBJECT);
                        }

                       return node.getData(AST_VARIATION_DATA_TYPE);

                    case AST_TYPE_NUMBER_LITERAL: {
                        node.putData(AST_VARIATION_DATA_TYPE, DATA_NUMBER);
                        return DATA_NUMBER;
                    }
                    case AST_TYPE_STRING_LITERAL: {
                        node.putData(AST_VARIATION_DATA_TYPE, DATA_STRING);
                        return DATA_STRING;
                    }

                }

//


                return null;

            }



        }.visit(ast);



        //todo register variation


        //todo maybe none-useful like this

        HashMap<String, DataType> variations = new HashMap();
        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node child, ArrayList<Object> dataFromChildren) {


                if (AST_TYPE_EXPRESSION.equals(child.getStringData(TREE_TYPE)) && "set".equals(child.getStringData(TREE_VALUE))) {
                    DataType dataType = DATA_VOID;
                    if (AST_TYPE_FUNCTION.equals(child.getLastChild().getStringData(TREE_TYPE))) {

                        //数据类型


                        dataType = (DataType) child.getLastChild().getData(AST_FUNCTION_RETURN_TYPE);
                    } else if (AST_TYPE_VARIATION.equals(child.getLastChild().getStringData(TREE_TYPE))) {
                        dataType = (DataType) child.getLastChild().getData(AST_VARIATION_DATA_TYPE);
                    }
                    //  variations.put(child.getFirstChild().getStringData(TREE_VALUE), dataType);
                }
                return null;
            }



        }.visit(ast);
        new Tree.visitor() {


            @Override
            public Object execute(Tree.Node child, ArrayList<Object> dataFromChildren) {

                if (AST_TYPE_VARIATION.equals(child.getStringData(TREE_TYPE))) {
                    //todo what todo
                    // variations.containsKey(child.getFirstChild().getStringData(TREE_VALUE));





                }
                return null;

            }



        }.visit(ast);


        return ast;


    }



    public Tree translator(Tree ast) {
        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                return null;
            }


        }.visit(ast);

//todo






        return null;

    }


    //todo far far

    String codeGenerator(Tree ast) {

        StringBuilder codeBuffer = new StringBuilder();


        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node child, ArrayList<Object> dataFromChildren) {
                switch (child.getStringData(TREE_TYPE)) {

                    case AST_TYPE_CONTROL -> {
                        switch (child.getStringData(TREE_TOKEN_NAME)) {
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
