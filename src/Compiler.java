import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;


public class Compiler {

    final static String TREE_TYPE = "type";
    final static String TREE_VALUE = "value";
    final static String TREE_TOKEN_PAREN = "paren";
    final static String TREE_TOKEN_STRING = "string";
    final static String TREE_TOKEN_NUMBER = "number";
    final static String TREE_TOKEN_NAME = "name";
    final static String AST_TYPE_NUMBER_LITERAL = "NumberLiteral";
    final static String AST_TYPE_STRING_LITERAL = "StringLiteral";

    final static String AST_TYPE_FUNCTION = "function";
    final static String AST_TYPE_EXPRESSION = "expression";// used by Compiler
    final static String AST_TYPE_VARIATION = "variation";

    final static String AST_TYPE_CODE_BLOCK = "code block";
    final static String AST_TYPE_TYPE_DECLARATION = "typeDeclaration";
    final static String AST_TYPE_CONTROL = "control";
    final static String AST_TYPE_OPERATION = "operation";


    final static String AST_FUNCTION_RETURN_TYPE = "returnType";
    static final String AST_FUNCTION_ID = Library.FUNC_ID;
    final static String AST_FUNCTION_DEFINED = "defined";
    final static String AST_FUNCTION_CONTENT = "content";
    final static String AST_VARIATION_DATA_TYPE = "dataType";

    static final String RETURN_VAR = "return var";

    final static String IGNORE = "ignore";
    final static String CONTROL_PATTERN_SIGN = "[+\\-*/=<>]";
    final static String CONTROL_PATTERN = "if|else|while|break|continue|return|switch|";
    static final DataType DATA_VOID, DATA_ITEM_TYPE, DATA_UNIT, DATA_BLOCK, DATA_STRING, DATA_NUMBER, DATA_OBJECT;
    static final String TYPE_PATTERN;
    final static int maxPriority;
    final static HashMap<String, String[]> OperationSet;
    static final HashMap<String, String[]> logicDictionary = new HashMap<>();

    static {

        DATA_OBJECT = DataType.OBJECT;
        DATA_NUMBER = DataType.createDataType("num");
        DATA_STRING = DataType.createDataType("str");
        DATA_BLOCK = DataType.createDataType("block");
        DATA_UNIT = DataType.createDataType("unit");

        DATA_ITEM_TYPE = DataType.createDataType("item");

        DATA_VOID = DataType.createDataType("void");
        TYPE_PATTERN = DataType.getAllDataTypesNames();

        logicDictionary.put("add_2", new String[]{"add result a b", "a", "b"});
        logicDictionary.put("add_0", new String[]{"add result "});

        maxPriority = 10;


        OperationSet = new HashMap<>();
        OperationSet.put("*", new String[]{"10", "mul", "double"});
        OperationSet.put("+", new String[]{"9", "add", "double"});
        OperationSet.put("=", new String[]{"3", "set", "double"});
        OperationSet.put("++", new String[]{"9", "selfAdd", "opt-left"});
    }


    int current;

    int currentOperationClass = maxPriority;


    final static String TEMPT_VAR = "tempt var";
    final static String TEMPT_MARK = "tempt mark";

    static void compileLibrary(String id) {

        //todo fatal return invalid
        Tree.Node func = Library.functionList.get(id);
        Tree.Node content = (Tree.Node) func.getData(Library.CONTENT);


        HashMap<String, DataType> variationMap = new HashMap<>();


        // register variables
        //todo auto capacity
        String[] results = new String[1];
        //todo it seemed that i can reuse the repeated code
        new Tree.visitor() {


            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                switch (node.getStringData(Compiler.TREE_TYPE)) {

                    case Compiler.AST_TYPE_FUNCTION: {

                        String nameSpace = "native";
                        String name = node.getStringData(Compiler.TREE_VALUE);
                        if (name.contains(".")) {
                            String[] callerSet = name.split("\\.");

                            nameSpace = callerSet[0];
                            name = callerSet[1];
                        }

                        DataType[] argTypes = new DataType[dataFromChildren.size()];
                        for (int i = 0; i < dataFromChildren.size(); i++) {


                            if (dataFromChildren.get(i) != null) {
                                argTypes[i] = (DataType) dataFromChildren.get(i);
                            } else {
                                argTypes[i] = DataType.OBJECT;
                            }

                            //todo here ?

//
                        }

                        try {

                            //todo unify var with type

                            //adjust before or after set

                            Object[] information = Library.getReturnDataType(nameSpace, name, argTypes);
                            node.putData(Compiler.AST_FUNCTION_RETURN_TYPE, information[0]);

                            node.putData(Compiler.AST_FUNCTION_ID, information[1]);

                            if (node.getData(Compiler.RETURN_VAR) == null) {
                                //todo better without take place many func inner variations names
                                variationMap.put(getTemptVarName(this.index), Compiler.DATA_OBJECT);
                                node.putData(Compiler.RETURN_VAR, getTemptVarName(this.index));
                            } else {
                                variationMap.put(node.getStringData(RETURN_VAR), Compiler.DATA_OBJECT);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                            //e.printStackTrace();
                        }

                        return node.getData(Compiler.AST_FUNCTION_RETURN_TYPE);

                    }
                    //break;
                    case Compiler.AST_TYPE_VARIATION: {


                        Object type = variationMap.get(node.getData(Compiler.TREE_VALUE));
                        if (type == null) {
                            variationMap.put(node.getStringData(Compiler.TREE_VALUE), DataType.OBJECT);
                        }
                        if (!node.hasKey(AST_VARIATION_DATA_TYPE)) {
                            node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_OBJECT);
                        }


                        return node.getData(Compiler.AST_VARIATION_DATA_TYPE);
                    }

                    case Compiler.AST_TYPE_NUMBER_LITERAL: {
                        node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_NUMBER);
                        return Compiler.DATA_NUMBER;
                    }
                    case Compiler.AST_TYPE_STRING_LITERAL: {
                        node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_STRING);
                        return Compiler.DATA_STRING;
                    }

                }

                return null;

            }


        }.visit(content);


        // register variation all obj except args
        for (Tree.Node var : func.children) {
            variationMap.put(var.getStringData(Compiler.TREE_VALUE), (DataType) var.getData(Compiler.AST_VARIATION_DATA_TYPE));

        }

        //todo maybe none-useful like this

        final String MARK_TO_JUMP = "_TO_JUMP";

        StringBuilder NativeCodeBuilder = new StringBuilder();
        //func is your home
        //generate native code
        new Tree.visitor() {
            final static String SURROUND_MARK_FRONT = "surround mark front";
            final static String SURROUND_MARK_BACK = "surround mark back";


            //迭代正当其时
            @Override
            public Object enter(Tree.Node parent) {

                if (parent.hasParent() && (Compiler.AST_TYPE_EXPRESSION.equals(parent.getStringData(Compiler.TREE_TYPE))

                        || Compiler.AST_TYPE_CODE_BLOCK.equals(parent.getStringData(Compiler.TREE_TYPE)))) {
                    String surroundMarkBase = func.getStringData(Library.FUNC_ID) + parent.getStringData(TREE_VALUE) + "_INDEX_" + totalIndex + "_" + index + "_START:";
                    NativeCodeBuilder.append(surroundMarkBase).append("\n");
                    parent.putData(SURROUND_MARK_FRONT, surroundMarkBase);
                }


                return super.enter(parent);
            }

            @Override
            public Object exit(Tree.Node parent) {

                if (parent.hasParent() && (Compiler.AST_TYPE_EXPRESSION.equals(parent.getStringData(Compiler.TREE_TYPE)) || Compiler.AST_TYPE_CODE_BLOCK.equals(parent.getStringData(Compiler.TREE_TYPE)))) {
                    String surroundMarkBase = func.getStringData(Library.FUNC_ID) + parent.getStringData(TREE_VALUE) + "_INDEX_" + totalIndex + "_" + index + "_END:";
                    NativeCodeBuilder.append(surroundMarkBase).append("\n");
                    parent.putData(SURROUND_MARK_BACK, surroundMarkBase);
                }

                return super.enter(parent);
            }

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {


                switch (node.getStringData(Compiler.TREE_TYPE)) {


                    case Compiler.AST_TYPE_FUNCTION: {

                        if (Library.RAW_FUNCTION.equals(node.getStringData(Compiler.TREE_VALUE))) {

                            String rawCode = (String) dataFromChildren.get(0);
                            NativeCodeBuilder.append(rawCode).append("\s\n");
                            return null;
                        } else //ELSE open your mouse ou no eyes
                        {


                            Tree.Node isDefined = Library.functionList.get(node.getData(Library.FUNC_ID));

                            if (isDefined != null && !(Boolean) isDefined.getData(Compiler.AST_FUNCTION_DEFINED)) {
                                Compiler.compileLibrary(node.getStringData(Library.FUNC_ID));

                            }
                            String code = Library.functionList.get(node.getData(Library.FUNC_ID)).getStringData(Library.NATIVE_CODE);

                            String[] subArgs = (String[]) Library.functionList.get(node.getData(Library.FUNC_ID)).getData(Library.ARGS);
                            String[] subReturn = (String[]) Library.functionList.get(node.getData(Library.FUNC_ID)).getData(Library.RETURNS);
                            HashMap<String, Object> varlist = (HashMap<String, Object>) Library.functionList.get(node.getData(Library.FUNC_ID)).getData(Library.VAR_LIST);


//                            for (int i = 1; i < node.children.size(); i++) {
//                                if (node.children.get(i) != null) {
//                                    //todo raw must like this
//                                    String param = node.children.get(i).getStringData(Compiler.TREE_VALUE);
//                                    String[] paramSplit = param.split("_");
//
//                                    String Type = paramSplit[0];
//                                    String Name = paramSplit[1];
//
//
//                                    //todo many func many start end
//                                    rawCode = rawCode.replaceAll("\s" + param + "\s", "\s" + Name + "\s");
//                                }
//
//                            }


                            //todo confuse arg with return                 no as long as x is reapply to a value it occurs
                            //replace args
                            for (int i = 0; i < node.children.size(); i++) {
                                code = code.replaceAll("\s" + subArgs[i] + "\s", "\s" + dataFromChildren.get(i).toString() + "\s");
                            }

                            //rerereplace tempt var
                            for (String var : varlist.keySet()) {
                                if (var.contains("TEMPT-VAR")) {
                                    code = code.replaceAll("\s" + var + "\s", "\s" + getTemptVarName(Integer.valueOf(var.split("_")[1]) + index - 1) + "\s");
                                } else {

                                }

                            }


                            //todo link between //subReturn and sub

//                            // todo
//                            if (node.getData(Compiler.RETURN_VAR) == null) {
//                                //todo better without take place many func inner variations names
//                                variationMap.put(getTemptVarName(this.index), (DataType) node.getData(Compiler.AST_FUNCTION_RETURN_TYPE));
//                                node.putData(Compiler.RETURN_VAR,getTemptVarName(this.index));
//                            } else {
//
//                            }

                            variationMap.put((String) node.getData(Compiler.RETURN_VAR), (DataType) node.getData(Compiler.AST_FUNCTION_RETURN_TYPE));

                            // replace result
                            code = code.replaceAll("\s" + subReturn[0] + "\s", "\s" + node.getStringData(Compiler.RETURN_VAR) + "\s")
                            // .replaceAll("ignore", "ignore_"+totalIndex)
                            ;


                            //todo every time new replacement

                            code = code.replaceAll("_INDEX_", "_INDEX_" + totalIndex + "_" + index + "_");
                            NativeCodeBuilder.append(code);

                            if (node.getData(Compiler.RETURN_VAR) != null) {


                                return node.getData(Compiler.RETURN_VAR);
                            } else {
                                //todo index
                                return getTemptVarName(this.index);
                            }

                        }


                    }
                    case Compiler.AST_TYPE_VARIATION: {

                        if (node.hasKey(AST_VARIATION_DATA_TYPE) && !node.getData(AST_VARIATION_DATA_TYPE).equals(Compiler.DATA_OBJECT)) {
                            variationMap.put(node.getStringData(Compiler.TREE_VALUE), (DataType) node.getData(Compiler.AST_VARIATION_DATA_TYPE));

                        }
                        return node.getStringData(Compiler.TREE_VALUE);
                    }
                    case Compiler.AST_TYPE_EXPRESSION: {
                        //
                        return dataFromChildren.get(0);
                    }
                    //   todo all the replacement is at function parent

                    case Compiler.AST_TYPE_CONTROL: {

                        //todo if get its condition from expression add start or end

                        //todo maybe jump is not this way
                        //todo  also its block

                        if ("if".equals(node.getStringData(Compiler.TREE_VALUE))) {

                            //after expression jump area after code block
                            NativeCodeBuilder.insert(NativeCodeBuilder.indexOf(node.getFirstChild().getStringData(SURROUND_MARK_BACK)), " jump " + " notEqual " + dataFromChildren.get(0) + " true " + node.getLastChild().getStringData(SURROUND_MARK_BACK).replaceAll(":", MARK_TO_JUMP + ":\n"));


                        }
                        if ("while".equals(node.getStringData(Compiler.TREE_VALUE))) {


                            NativeCodeBuilder.insert(NativeCodeBuilder.indexOf(node.getFirstChild().getStringData(SURROUND_MARK_BACK)), " jump " + " notEqual " + dataFromChildren.get(0) + " true " + node.getLastChild().getStringData(SURROUND_MARK_BACK).replaceAll(":", MARK_TO_JUMP + ":\n"));
                            NativeCodeBuilder.insert(NativeCodeBuilder.indexOf(node.getLastChild().getStringData(SURROUND_MARK_BACK)), " jump " + "always 0 0 " + node.getFirstChild().getStringData(SURROUND_MARK_FRONT).replaceAll(":", MARK_TO_JUMP + ":\n"));

                            //   NativeCodeBuilder.insert(NativeCodeBuilder.indexOf(jumpBase), code);


                        }


                        //it is stupid to use multi callback
                        if ("return".equals(node.getStringData(Compiler.TREE_VALUE))) {
                            for (int i = 0; i < node.children.size(); i++) {
                                results[0] = (node.getChild(i).getStringData(Compiler.TREE_VALUE));
                                //todo add a jump
                            }
                        }
                        return IGNORE;
                    }
                }

                return node.getStringData(Compiler.TREE_VALUE);
                // return IGNORE;
            }

            //avoid multi jump to the same place and confuse the compiler
        }.visit(content);


        String[] args = new String[func.children.size()];

        for (int i = 0; i < func.children.size(); i++) {
            args[i] = func.children.get(i).getStringData(Compiler.TREE_VALUE);
        }
        //todo


        func.putData(Library.NATIVE_CODE, NativeCodeBuilder.toString().replaceAll(MARK_TO_JUMP, ""), Compiler.AST_FUNCTION_DEFINED, true);

        func.putData(Library.ARGS, args, Library.RETURNS, results, Library.VAR_LIST, variationMap);


    }

    static private String getTemptVarName(int index) {
        //not touch it
        return "TEMPT-VAR_" + index;
    }

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
            if (Pattern.compile("#").matcher(character + "").find()) {

                // StringBuilder value = new StringBuilder();
                while (!Pattern.compile("[\n\r]").matcher(character + "").find()) {


                    // value.append(character);
                    character = input.charAt(++current);


                }
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
                while (Pattern.compile("[a-z]|[A-Z]|[.:_]|[0-9]").matcher(character + "").find()) {


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

    //walk also is your parser
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


        // operation to pre-function
        this.currentOperationClass = maxPriority;
        while (this.currentOperationClass > 0) {
            this.current = 0;


            new Tree.visitor() {

                @Override
                public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                    if (AST_TYPE_OPERATION.equals(node.getStringData(TREE_TYPE))) {
                        String name = node.getStringData(TREE_VALUE);


                        if (Compiler.this.currentOperationClass == Integer.parseInt(OperationSet.get(name)[0])) {


                            switch (OperationSet.get(name)[2]) {
                                case "double": {
                                    node.left.setParent(node);
                                    node.jump = node.right;
                                    node.right.jump = node;
                                    node.right.setParent(node);
                                    break;
                                }
                                case "opt-right": {
                                    node.jump = node.right;
                                    node.right.jump = node;
                                    node.right.setParent(node);
                                    break;
                                }
                                case "opt-left": {
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


        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {


                if (AST_TYPE_EXPRESSION.equals(node.getStringData(TREE_TYPE))) {


                    if ((node.getStringData(TREE_VALUE).endsWith(IGNORE))

                    ) {
                        if (node.singleChild() && AST_TYPE_EXPRESSION.equals(node.parent.getStringData(TREE_TYPE))) {
                            node.replacedBy(node.getLastChild());
                        }


                    } else {
                        node.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                    }


                }

                //todo mmm
                //todo refuse to clean multi box

//                if (AST_TYPE_CODE_BLOCK.equals(node.getStringData(TREE_TYPE))) {
//                    if (node.singleChild() && (node.getStringData(TREE_VALUE).endsWith(IGNORE))&&AST_TYPE_CODE_BLOCK.equals(node.parent.getStringData(TREE_TYPE))) {
//
//
//                        node.replacedBy(node.getLastChild());
//
//
//                    }
//
//                }
                //simplify add with direct result


                if (AST_TYPE_TYPE_DECLARATION.equals(node.getStringData(TREE_TYPE))) {

                    // troubled by replaced a right node
                    if (AST_TYPE_VARIATION.equals(node.right.getStringData(TREE_TYPE))) {

                        node.right.putData(AST_VARIATION_DATA_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
                        //node.setJump(node.right);
                        node.replacedBy(node.right);
                        return null;
                    }

                    if ((AST_TYPE_EXPRESSION.equals(node.right.getStringData(TREE_TYPE)) && "set".equals(node.right.getStringData(TREE_VALUE)))) {

                        node.right.putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
                        node.right.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                        node.right.getFirstChild().putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
                        //node.setJump(node.right);
                        node.replacedBy(node.right);
                        return null;

                    }

                }

                if ("set".equals(node.getStringData(TREE_VALUE))) {

                    if (node.children.size() == 2 && (AST_TYPE_FUNCTION.equals(node.getLastChild().getStringData(TREE_TYPE))) && node.getLastChild().getData(RETURN_VAR) == null) {


                        node.getLastChild().putData(RETURN_VAR, node.getFirstChild().getStringData(TREE_VALUE));
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
                if (AST_TYPE_TYPE_DECLARATION.equals(node.getStringData(TREE_TYPE))) {


                    if (AST_TYPE_FUNCTION.equals(node.right.getStringData(TREE_TYPE))) {

                        node.right.putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));

                        //todo a better way to do it

                        node.right.putData(AST_FUNCTION_DEFINED, false);

                        // it is a declaration!!!

                        //here I put a data and give up add to child
                        node.right.putData(AST_FUNCTION_CONTENT, node.right.right.removeFromParent());



                        node.replacedBy(node.right);
                        return null;

                    }

                }
                return null;
            }
        }.visit(ast);


//merge unneeded suite


        // I want to unify our vars


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


            case TREE_TOKEN_NUMBER: {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_NUMBER_LITERAL, TREE_VALUE, value);
                //break;
            }
            case TREE_TOKEN_STRING: {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_STRING_LITERAL, TREE_VALUE, value);
                //break;
            }
            case AST_TYPE_OPERATION: {
                this.current++;
                return new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value);
                // break;
            }

            case TREE_TOKEN_PAREN: {
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

            case TREE_TOKEN_NAME: {

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
                            wantAction = true;

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


            default: {

                throw new Exception(type);
                //break;
            }


        }


    }

    private String getExpressionName(int index) {
        return "_EXPRESSION_" + index + "_ignore";
    }

    private String getCodeBlockName(int index) {
        return "_CODE_BLOCK_" + index + "_ignore";
    }

    public Tree loader(String path) throws Exception {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // this is important to and an end
        Tree mlogProject = tokenizer(text + "\nend: ");
        Tree library = preParser(mlogProject, "func");
        library = parser(library);


        for (Tree.Node node : library.root.children)    {

            String names = node.getStringData(Compiler.TREE_VALUE);

            if(names.contains("_")){

                String[] optionList=names.split("_");
            }




        }
        //no more operation
        // library = semanticParser(library);


        String nameSpace = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));


        //give it an id
        new Tree.visitor() {


            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                if (node.hasParent() && Compiler.AST_TYPE_FUNCTION.equals(node.parent.getStringData(Compiler.TREE_TYPE))) {
                    return node.getData(Compiler.AST_VARIATION_DATA_TYPE);

                }
                if (Compiler.AST_TYPE_FUNCTION.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    StringBuilder sb = new StringBuilder(nameSpace).append('.').append(node.getStringData(Compiler.TREE_VALUE));

                    // no need consider func in func
                    for (Object dt : dataFromChildren) {

                        if (dt != null) {
                            sb.append('_').append(dt);
                        }


                    }
                    node.putData(Library.FUNC_ID, sb.toString());
                }
                return null;
            }


        }.visit(library);


        mlogProject = new Tree().addNode(library.putData(Library.LIBRARY_SORT_NAME, Library.LIBRARY)).putData(Library.NAMESPACE, nameSpace);


        Library.addLibrary(mlogProject);
        return Library.FunctionTree;


    }


    //only designed for main
    public Tree semanticParser(Tree ast) {

        HashMap<String, DataType> variationMap = new HashMap<>();

        //make clear every type
        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {

                switch (node.getStringData(Compiler.TREE_TYPE)) {

                    case Compiler.AST_TYPE_FUNCTION: {

                        String nameSpace = "native";
                        String name = node.getStringData(Compiler.TREE_VALUE);
                        if (name.contains(".")) {
                            String[] callerSet = name.split("\\.");

                            nameSpace = callerSet[0];
                            name = callerSet[1];
                        }

                        DataType[] argTypes = new DataType[dataFromChildren.size()];
                        for (int i = 0; i < dataFromChildren.size(); i++) {


                            if (dataFromChildren.get(i) != null) {
                                argTypes[i] = (DataType) dataFromChildren.get(i);
                            } else {
                                argTypes[i] = DataType.OBJECT;
                            }


//
                        }

                        try {

                            //todo unify var with type

                            //adjust before or after set

                            Object[] information = Library.getReturnDataType(nameSpace, name, argTypes);
                            node.putData(Compiler.AST_FUNCTION_RETURN_TYPE, information[0]);

                            node.putData(Compiler.AST_FUNCTION_ID, information[1]);


                            //todo here ?
                            if (node.getData(RETURN_VAR) != null) {
                                variationMap.put(node.getStringData(RETURN_VAR), (DataType) node.getData(AST_FUNCTION_RETURN_TYPE));


                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                            //e.printStackTrace();
                        }

                        return node.getData(Compiler.AST_FUNCTION_RETURN_TYPE);

                    }
                    //break;
                    case Compiler.AST_TYPE_VARIATION: {

                        if (node.getData(Compiler.AST_VARIATION_DATA_TYPE) != null) {

                            variationMap.put(node.getStringData(Compiler.TREE_VALUE), (DataType) node.getData(Compiler.AST_VARIATION_DATA_TYPE));
                        } else {
//todo
                            Object type = variationMap.get(node.getStringData(Compiler.TREE_VALUE));
                            if (type != null) {
                                node.putData(Compiler.AST_VARIATION_DATA_TYPE, type);
                            } else {
                                node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_OBJECT);
                                variationMap.put(node.getStringData(Compiler.TREE_VALUE), (DataType) node.getData(Compiler.AST_VARIATION_DATA_TYPE));

                            }


                        }

                        return node.getData(Compiler.AST_VARIATION_DATA_TYPE);
                    }
                    case Compiler.AST_TYPE_NUMBER_LITERAL: {
                        node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_NUMBER);
                        return Compiler.DATA_NUMBER;
                    }
                    case Compiler.AST_TYPE_STRING_LITERAL: {
                        //node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_STRING);
                        return Compiler.DATA_STRING;
                    }

                }


                return null;
            }


        }.visit(ast);


        return ast;


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
