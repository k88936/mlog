package com.k.compiler;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.k.compiler.Compiler.Library.functionMap;


public class Compiler {


//sample code


//    public class native {
//
//
//
//        public static void main(String[] args) {
//
//        }
//
//
//
//
//
//
//
//
//
//
//
//        static num add( num a, num b){
//            return a + b;
//        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    }


    final static String TREE_TYPE = "type";
    final static String TREE_VALUE = "value";
    final static String TREE_TOKEN_PAREN = "paren";
    final static String TREE_TOKEN_STRING = "string";
    final static String TREE_TOKEN_NUMBER = "number";
    final static String TREE_TOKEN_NAME = "name";
    final static String AST_TYPE_NUMBER_LITERAL = "NumberLiteral";
    final static String AST_TYPE_STRING_LITERAL = "StringLiteral";
    final static String AST_TYPE_FUNCTION = "function";
    final static String AST_TYPE_EXPRESSION = "expression";// used by com.k.compiler.Compiler
    final static String AST_TYPE_VARIATION = "variation";
    final static String AST_TYPE_CODE_BLOCK = "code block";
    final static String AST_TYPE_TYPE_DECLARATION = "typeDeclaration";
    final static String AST_TYPE_CONTROL = "control";
    final static String AST_TYPE_OPERATION = "operation";
    //static final String AST_TYPE_TYPE_KEY_WORD = "keyword";
    final static String AST_FUNCTION_RETURN_TYPE = "returnType";
    static final String AST_FUNCTION_ID = Library.FUNC_ID;
    final static String AST_FUNCTION_DEFINED = "defined";
    final static String AST_FUNCTION_CONTENT = "content";
    final static String AST_VAR_FUNC_CLASS_KEYWORD = "key word";
    final static String AST_VARIATION_DATA_TYPE = "dataType";

    static final String RETURN_VAR = "return var";
    final static String IGNORE = "ignore";
    //+
    //-
    //*
    ///
    //%
    //=
    //==
    //!=
    //<
    //>
    //<=
    //>=
    //(
    //)
    //{
    //}
    //&&
    //||
    final static String CONTROL_PATTERN_SIGN = "[+\\-*/=<>|&!%^]";
    final static String CONTROL_PATTERN = "if|else|while|break|continue|return|switch";

    final static String KEY_WORD_PATTERN = "public|private|protected|static|class|hidden";

    static final DataType DATA_VOID, DATA_ITEM_TYPE, DATA_UNIT, DATA_BLOCK, DATA_STRING, DATA_NUMBER, DATA_OBJECT;
    static final String TYPE_PATTERN;
    final static int maxPriority = 10;
    final static HashMap<String, String[]> OperationSet;
    static final HashMap<String, String[]> logicDictionary = new HashMap<>();
    final static String TEMPT_VAR = "tempt var";
    final static String TEMPT_MARK = "tempt mark";
    final static String AST_EXPRESSION_AS_ARGS = "use as args";
    final static String AST_TYPE_CLASS = "class";
    final static String OBJ_ORI_INVOKER = "ooObj ";
    final static String OBJ_ORI_FUNC = "ooFunc";
    static int current;
    static int currentOperationClass = maxPriority;
    static HashSet<String> KEY_WORDS_RECORDER = new HashSet<>();

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

        // maxPriority = 10;


        OperationSet = new HashMap<>();
        OperationSet.put("*", new String[]{"10", "mul", "double"});
        OperationSet.put("+", new String[]{"9", "add", "double"});
        OperationSet.put("=", new String[]{"3", "set", "double"});
        OperationSet.put("++", new String[]{"9", "selfAdd", "opt-left"});
    }

    static private String getTemptVarName(int index) {
        //not touch it
        return "TEMPT-VAR_" + index;
    }
    final static String IS_CONSTRUNCTOR = "is constructor";
    final static String AST_FUNCTION_RETURN_NEW_OBJECT = "new Object";
    static void analyzing(Tree jst) {

        for (String id : functionMap.keySet()) {

            if ((Boolean) functionMap.get(id).getData(Compiler.AST_FUNCTION_DEFINED)) {
                continue;
            }

            Compiler.analysis(id);

        }

        System.out.println("library compile complete: " + functionMap.size());
        System.out.println("----------------------------------------------------------------------------------------");

        for (Tree.Node fc : functionMap.values()) {

            if (fc.getData(AST_VAR_FUNC_CLASS_KEYWORD) != null) {

                HashSet hp = (HashSet) fc.getData(AST_VAR_FUNC_CLASS_KEYWORD);
                if (hp.contains("hidden") || hp.contains("private")) {
                    //continue;
                }

            }
            System.out.println(fc);
            System.out.println("----------------------------------------------------------------------------------------");

        }
        System.out.println("================================================================================");

    }

    static Tree loader(Tree jst) throws Exception {

        //String code=" ";

        new Tree.visitor() {

            static final StringBuilder namespace = new StringBuilder("project");

            @Override
            public Object enter(Tree.Node node) {
                if (Compiler.AST_TYPE_CLASS.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    namespace.append('.').append(node.getStringData(Compiler.TREE_VALUE));
                    node.putData(Library.CLASS_ID, namespace.toString());
                    DataType.createDataType(namespace.toString());
                }

                return null;
            }

            public Object exit(Tree.Node node) {
                if (Compiler.AST_TYPE_CLASS.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    namespace.delete(namespace.lastIndexOf("."), namespace.length());
                }
                return null;
            }

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {

////                if (node.hasParent() && Compiler.AST_TYPE_FUNCTION.equals(node.parent.getStringData(Compiler.TREE_TYPE))) {
////                    return node.getData(Compiler.AST_VARIATION_DATA_TYPE);
////
////
////
////
////                }
//
//                if (Compiler.AST_TYPE_FUNCTION.equals(node.getStringData(Compiler.TREE_TYPE))) {
//
//
//                    if ((boolean) node.getData(Compiler.AST_FUNCTION_DEFINED)) {
//
//                        if (node.getStringData(Compiler.TREE_VALUE).contains(".")) {
//
//
//
//
//                            //StringBuilder sb = new StringBuilder(namespace.toString()).append('.').append(node.getStringData(Compiler.TREE_VALUE));
//                            node.putData(Library.FUNC_ID, node.getStringData(Compiler.TREE_VALUE));
//                            node.putData(Library.FUNC_SHORT_ID, node.getStringData(Compiler.TREE_VALUE));
////                            String[] objOriSet = node.getStringData(Compiler.TREE_VALUE).split("\\.");
////
////                            node.putData(Compiler.OBJ_ORI_INVOKER, objOriSet[0]).putData(Compiler.OBJ_ORI_FUNC, objOriSet[objOriSet.length - 1]);
//
//
//                        } else {
//
//                            node.putData(Library.FUNC_ID, namespace.toString() + '.' + node.getStringData(Compiler.TREE_VALUE));
//                            node.putData(Library.FUNC_SHORT_ID, namespace.toString() + '.' + node.getStringData(Compiler.TREE_VALUE));
//                        }
//
//
//                    } else {
//
//                        //game begins
//                        StringBuilder sb = new StringBuilder(namespace.toString()).append('.').append(node.getStringData(Compiler.TREE_VALUE));
//
//                        node.putData(Library.FUNC_SHORT_ID, sb.toString());
//                        // no need consider func in func
//                        for (Tree.Node dt : node.getFirstChild().children) {
//
//                            if (dt != null) {
//                                sb.append('_').append(dt.getData(Compiler.AST_VARIATION_DATA_TYPE));
//                            }
//                        }
//
//
//                        node.putData(Library.FUNC_ID, sb.toString());
//                        Library.addFunction(node);
//
//                    }
//
//
//                }
                return null;
            }


        }.visit(jst);

        new Tree.visitor() {

            static final StringBuilder namespace = new StringBuilder("project");

            @Override
            public Object enter(Tree.Node node) {
                if (Compiler.AST_TYPE_CLASS.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    namespace.append('.').append(node.getStringData(Compiler.TREE_VALUE));
                    node.putData(Library.CLASS_ID, namespace.toString());
                }

                return null;
            }

            public Object exit(Tree.Node node) {
                if (Compiler.AST_TYPE_CLASS.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    namespace.delete(namespace.lastIndexOf("."), namespace.length());
                }
                return null;
            }

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {

//                if (node.hasParent() && Compiler.AST_TYPE_FUNCTION.equals(node.parent.getStringData(Compiler.TREE_TYPE))) {
//                    return node.getData(Compiler.AST_VARIATION_DATA_TYPE);
//
//
//
//
//                }

                if (Compiler.AST_TYPE_FUNCTION.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    String value = node.getStringData(Compiler.TREE_VALUE);
                    if ((boolean) node.getData(Compiler.AST_FUNCTION_DEFINED)) {


                        if (node.getStringData(Compiler.TREE_VALUE).contains(".")) {

                            //todo java.jvm.compile assurt it is not obj


                            if (value.contains("project.")) {
                                node.putData(Library.FUNC_ID, value);
                                node.putData(Library.FUNC_SHORT_ID, value);
                            } else {
                                node.putData(Library.FUNC_ID, "project." + value);
                                node.putData(Library.FUNC_SHORT_ID, "project." + value);
                            }

                            //StringBuilder sb = new StringBuilder(namespace.toString()).append('.').append(node.getStringData(Compiler.TREE_VALUE));


//                            String[] objOriSet = node.getStringData(Compiler.TREE_VALUE).split("\\.");
////
//                          node.putData(Compiler.OBJ_ORI_INVOKER, objOriSet[0]).putData(Compiler.OBJ_ORI_FUNC, objOriSet[objOriSet.length - 1]);


                        } else {

                            node.putData(Library.FUNC_ID, namespace.toString() + '.' + value);
                            node.putData(Library.FUNC_SHORT_ID, namespace.toString() + '.' + value);

                        }


                        if (Compiler.AST_FUNCTION_RETURN_NEW_OBJECT.equals(node.getStringData(Compiler.AST_FUNCTION_RETURN_TYPE))) {


                            node.putData(Compiler.AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(Library.FUNC_ID)));
                        }


                    } else {


                        //game begins
                        StringBuilder sb = new StringBuilder(namespace.toString()).append('.').append(value);

                        node.putData(Library.FUNC_SHORT_ID, sb.toString());
                        // no need consider func in func
                        for (Tree.Node dt : node.getFirstChild().children) {

                            if (dt != null) {
                                sb.append('_').append(dt.getData(Compiler.AST_VARIATION_DATA_TYPE));
                            }
                        }


                        node.putData(Library.FUNC_ID, sb.toString());


                        //
                        if (node.parent.parent.getStringData(TREE_VALUE).equals(value)) {
                            node.putData(Compiler.AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(Library.FUNC_ID)));
                            node.putData(Compiler.IS_CONSTRUNCTOR, true);
                        }


                        Library.addFunction(node);

                    }


                }
                return null;
            }


        }.visit(jst);


        return jst;
    }

    @Deprecated
    static Tree preParser(Tree tokens, final String part) {

        final Tree partOfTokens = new Tree();
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

    static Tree tokenizer(String input) throws Exception {

        input = input + '\n';
        int current = 0;


        Tree tokens = new Tree();
        while (current < input.length()) {

            char character = input.charAt(current);

            //System.out.println(character);
            if (Pattern.compile("[(){}\\[\\]]").matcher(String.valueOf(character)).find()) {


                tokens.addNode(new Tree.Node(TREE_TYPE, TREE_TOKEN_PAREN, TREE_VALUE, character));
                current++;
                continue;
            }
            //+-*/     WARNING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (Pattern.compile(CONTROL_PATTERN_SIGN).matcher(String.valueOf(character)).find()) {

                StringBuilder value = new StringBuilder();
                if (character == '/' && input.charAt(current + 1) == '/') {

                    while (!Pattern.compile("[\n\r]").matcher(String.valueOf(character)).find()) {


                        // value.append(character);
                        character = input.charAt(++current);


                    }
                    continue;


                } else {
                    while (Pattern.compile(CONTROL_PATTERN_SIGN).matcher(String.valueOf(character)).find()) {


                        value.append(character);
                        character = input.charAt(++current);


                    }

                }


                tokens.addNode(new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value.toString()));
                continue;
            }

            if (Pattern.compile("[ ,\r\n;]").matcher(character + "").find()) {
                current++;
                continue;
            }
//            if (Pattern.compile("#").matcher(character + "").find()) {
//
//                // StringBuilder value = new StringBuilder();
//                while (!Pattern.compile("[\n\r]").matcher(character + "").find()) {
//
//
//                    // value.append(character);
//                    character = input.charAt(++current);
//
//
//                }
//                continue;
//            }


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

    static void analysis(String id) {


        //todo fatal return invalid
        final Tree.Node function = functionMap.get(id);
        //Tree.Node content = (Tree.Node) func.getData(Library.CONTENT);


        final HashMap<String, DataType> variationMap = new HashMap<>();


        // register variables
        //todo auto capacity
        final String[] results = new String[1];
        //todo it seemed that i can reuse the repeated code
        new Tree.visitor() {


            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                switch (node.getStringData(Compiler.TREE_TYPE)) {

                    case Compiler.AST_TYPE_FUNCTION: {

//                        String nameSpace = "native";
//                        String name = node.getStringData(Compiler.TREE_VALUE);
//                        if (name.contains(".")) {
//                            String[] callerSet = name.split("\\.");
//
//                            nameSpace = callerSet[0];
//                            name = callerSet[1];
//                        }

                        Object seeminglyAList = dataFromChildren.get(0);
                        ;
                        if (ArrayList.class.isInstance(seeminglyAList)) {
                            ArrayList List = (ArrayList<Object>) seeminglyAList;
                            dataFromChildren.remove(0);
                            for (int i = 0; i < List.size(); i++) {
                                dataFromChildren.add(i, List.get(i));
                            }
                        }

                        //dataFromChildren = (ArrayList<Object>) null;

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
                            String Anyid = node.getStringData(Library.FUNC_SHORT_ID);

                            if (Compiler.AST_FUNCTION_RETURN_NEW_OBJECT.equals(node.getStringData(Compiler.AST_FUNCTION_RETURN_TYPE))) {
                                Anyid = Anyid + '.' + node.getStringData(TREE_VALUE);
                                node.putData(Library.FUNC_SHORT_ID, Anyid);
                            }

                            //id is undeclared waiting for compile
                            Object[] information = Library.getReturnDataType(Anyid, argTypes);


                            node.putData(Compiler.AST_FUNCTION_RETURN_TYPE, information[0]);

                            node.putData(Compiler.AST_FUNCTION_ID, information[1]);

                            if (node.getData(Compiler.RETURN_VAR) == null) {
                                //todo better without take place many func inner variations names
                                variationMap.put(getTemptVarName(index), Compiler.DATA_OBJECT);
                                node.putData(Compiler.RETURN_VAR, getTemptVarName(index));
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
                    case Compiler.AST_TYPE_EXPRESSION:
                        return dataFromChildren;

                }

                return null;

            }


        }.visit(function.getLastChild());


        // register variation all obj except args
        ArrayList<Tree.Node> children = function.getFirstChild().children;
        String[] args = new String[children.size()];
        for (int i = 0; i < children.size(); i++) {
            Tree.Node var = children.get(i);
            args[i] = var.getStringData(Compiler.TREE_VALUE);
            variationMap.put(var.getStringData(Compiler.TREE_VALUE), (DataType) var.getData(Compiler.AST_VARIATION_DATA_TYPE));

        }

        //todo maybe none-useful like this

        final String MARK_TO_JUMP = "_TO_JUMP";

        final StringBuilder NativeCodeBuilder = new StringBuilder();
        //func is your home
        //generate native code
        new Tree.visitor() {
            final static String SURROUND_MARK_FRONT = "surround mark front";
            final static String SURROUND_MARK_BACK = "surround mark back";


            //迭代正当其时
            @Override
            public Object enter(Tree.Node parent) {

                if ((Compiler.AST_TYPE_EXPRESSION.equals(parent.getStringData(Compiler.TREE_TYPE)) || Compiler.AST_TYPE_CODE_BLOCK.equals(parent.getStringData(Compiler.TREE_TYPE)))) {
                    String surroundMarkBase = function.getStringData(Library.FUNC_ID) + parent.getStringData(TREE_VALUE) + "_INDEX_" + totalIndex + "_" + index + "_START:";
                    NativeCodeBuilder.append(surroundMarkBase).append("\n");
                    parent.putData(SURROUND_MARK_FRONT, surroundMarkBase);
                }


                return super.enter(parent);
            }

            @Override
            public Object exit(Tree.Node parent) {

                if ((Compiler.AST_TYPE_EXPRESSION.equals(parent.getStringData(Compiler.TREE_TYPE)) || Compiler.AST_TYPE_CODE_BLOCK.equals(parent.getStringData(Compiler.TREE_TYPE)))) {
                    String surroundMarkBase = function.getStringData(Library.FUNC_ID) + parent.getStringData(TREE_VALUE) + "_INDEX_" + totalIndex + "_" + index + "_END:";
                    NativeCodeBuilder.append(surroundMarkBase).append("\n");
                    parent.putData(SURROUND_MARK_BACK, surroundMarkBase);
                }

                return super.exit(parent);
            }

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {


                switch (node.getStringData(Compiler.TREE_TYPE)) {


                    case Compiler.AST_TYPE_FUNCTION: {

                        if (Library.NATIVE_FUNCTION.equals(node.getStringData(Compiler.TREE_VALUE))) {

                            String rawCode = (String) dataFromChildren.get(0);
                            NativeCodeBuilder.append(rawCode).append(" \n");
                            return null;
                        } else //ELSE open your mouse ou no eyes
                        {


                            Tree.Node isDefined = functionMap.get(node.getData(Library.FUNC_ID));

                            if (isDefined != null && !(Boolean) isDefined.getData(Compiler.AST_FUNCTION_DEFINED)) {
                                Compiler.analysis(node.getStringData(Library.FUNC_ID));

                            }

                            String code = functionMap.get(node.getData(Library.FUNC_ID)).getStringData(Library.NATIVE_CODE);

                            String[] subArgs = (String[]) functionMap.get(node.getData(Library.FUNC_ID)).getData(Library.ARGS);
                            String[] subReturn = (String[]) functionMap.get(node.getData(Library.FUNC_ID)).getData(Library.RETURNS);
                            HashMap<String, Object> varlist = (HashMap<String, Object>) functionMap.get(node.getData(Library.FUNC_ID)).getData(Library.VAR_LIST);


//                            for (int i = 1; i < node.children.size(); i++) {
//                                if (node.children.get(i) != null) {
//                                    //todo raw must like this
//                                    String param = node.children.get(i).getStringData(com.k.compiler.Compiler.TREE_VALUE);
//                                    String[] paramSplit = param.split("_");
//
//                                    String Type = paramSplit[0];
//                                    String Name = paramSplit[1];
//
//
//                                    //todo many func many start end
//                                    rawCode = rawCode.replaceAll(" " + param + " ", " " + Name + " ");
//                                }
//
//                            }


                            //todo confuse arg with return                 no as long as x is reapply to a value it occurs
                            //replace args
                            for (int i = 0; i < node.children.size(); i++) {
                                code = code.replaceAll(" " + subArgs[i] + " ", " " + dataFromChildren.get(i).toString() + " ");
                            }

                            //rerereplace tempt var
                            for (String var : varlist.keySet()) {
                                if (var.contains("TEMPT-VAR")) {
                                    code = code.replaceAll(" " + var + " ", " " + getTemptVarName(Integer.valueOf(var.split("_")[1]) + index - 1) + " ");
                                } else {

                                }

                            }


                            //todo link between //subReturn and sub

//                            // todo
//                            if (node.getData(com.k.compiler.Compiler.RETURN_VAR) == null) {
//                                //todo better without take place many func inner variations names
//                                variationMap.put(getTemptVarName(this.index), (com.k.compiler.DataType) node.getData(com.k.compiler.Compiler.AST_FUNCTION_RETURN_TYPE));
//                                node.putData(com.k.compiler.Compiler.RETURN_VAR,getTemptVarName(this.index));
//                            } else {
//
//                            }

                            variationMap.put((String) node.getData(Compiler.RETURN_VAR), (DataType) node.getData(Compiler.AST_FUNCTION_RETURN_TYPE));

                            // replace result
                            code = code.replaceAll(" " + subReturn[0] + " ", " " + node.getStringData(Compiler.RETURN_VAR) + " ")
                            // .replaceAll("ignore", "ignore_"+totalIndex)
                            ;


                            //todo every time new replacement

                            code = code.replaceAll("_INDEX_", "_INDEX_" + totalIndex + "_" + index + "_");
                            NativeCodeBuilder.append(code);

                            if (node.getData(Compiler.RETURN_VAR) != null) {


                                return node.getData(Compiler.RETURN_VAR);
                            } else {
                                //todo index
                                return getTemptVarName(index);
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
                        if (dataFromChildren.isEmpty()) {
                            return null;
                        } else {
                            return dataFromChildren.get(0);
                        }

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
        }.visit(function.getLastChild());





        //todo


        function.putData(Library.NATIVE_CODE, NativeCodeBuilder.toString().replaceAll(MARK_TO_JUMP, ""), Compiler.AST_FUNCTION_DEFINED, true);

        function.putData(Library.ARGS, args, Library.RETURNS, results, Library.VAR_LIST, variationMap);


    }

    //walk also is your com.k.compiler.parser
    static Tree parser(Tree tokens) throws Exception {
        current = 0;
        Tree ast = new Tree();

        // ast.root.putData(TYPE, PROGRAM).putData(TYPE, BODY);


//a new tree is created
        //in case null!!!!!!!!!!!!!!!!
        tokens.addNode(new Tree.Node(TREE_TYPE, "", TREE_VALUE, ""));
        while (current < tokens.root.children.size()) {
            ast.addNode(Compiler.walk(tokens));
        }
        // System.out.println(ast);


        //here no current use
        //OOperation Great


        // operation to pre-function
        currentOperationClass = maxPriority;
        while (currentOperationClass > 0) {
            current = 0;


            new Tree.visitor() {

                @Override
                public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                    if (AST_TYPE_OPERATION.equals(node.getStringData(TREE_TYPE))) {
                        String name = node.getStringData(TREE_VALUE);


                        if (currentOperationClass == Integer.parseInt(OperationSet.get(name)[0])) {


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
            currentOperationClass--;

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
                        node.putData(TREE_TYPE, AST_TYPE_FUNCTION, AST_FUNCTION_DEFINED, true);
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


//                if (AST_TYPE_TYPE_DECLARATION.equals(node.getStringData(TREE_TYPE))) {
//
//                    // troubled by replaced a right node
//                    if (AST_TYPE_VARIATION.equals(node.right.getStringData(TREE_TYPE))) {
//
//                        node.right.putData(AST_VARIATION_DATA_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
//                        //node.setJump(node.right);
//                        node.replacedBy(node.right);
//                        return null;
//                    }
//
//                    if ((AST_TYPE_EXPRESSION.equals(node.right.getStringData(TREE_TYPE)) && "set".equals(node.right.getStringData(TREE_VALUE)))) {
//
//                        node.right.putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
//                        node.right.putData(TREE_TYPE, AST_TYPE_FUNCTION);
//                        node.right.getFirstChild().putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
//                        //node.setJump(node.right);
//                        node.replacedBy(node.right);
//                        return null;
//
//                    }
//
//                }
//

                if ("set".equals(node.getStringData(TREE_VALUE))) {

                    if (node.children.size() == 2 && (AST_TYPE_FUNCTION.equals(node.getLastChild().getStringData(TREE_TYPE))) && node.getLastChild().getData(RETURN_VAR) == null) {


                        node.getLastChild().putData(RETURN_VAR, node.getFirstChild().getStringData(TREE_VALUE));
                        node.replacedBy(node.getLastChild());


                    }
                    //node.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                }


                if ("return".equals(node.getStringData(TREE_VALUE))) {

                    node.addChild(node.right.removeFromParent());

//                    if (node.children.size() == 2 && (AST_TYPE_FUNCTION.equals(node.getLastChild().getStringData(TREE_TYPE))) && node.getLastChild().getData(RETURN_VAR) == null) {
//
//
//                        node.getLastChild().putData(RETURN_VAR, node.getFirstChild().getStringData(TREE_VALUE));
//                        node.replacedBy(node.getLastChild());
//
//
//                    }
                    //node.putData(TREE_TYPE, AST_TYPE_FUNCTION);
                }

                return null;


            }


        }.visit(ast);


        //history
//        new Tree.visitor() {
//
//            @Override
//            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
//                if (AST_TYPE_TYPE_DECLARATION.equals(node.getStringData(TREE_TYPE))) {
//
//
//                    if (AST_TYPE_FUNCTION.equals(node.right.getStringData(TREE_TYPE))) {
//
//                        node.right.putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(node.getStringData(TREE_VALUE)));
//
//                        // a better way to do it
//
//                        node.right.putData(AST_FUNCTION_DEFINED, false);
//
//                        // it is a declaration!!!
//
//                        //here I put a data and give up add to child
//                        node.right.putData(AST_FUNCTION_CONTENT, node.right.right.removeFromParent());
//
//
//                        node.replacedBy(node.right);
//                        return null;
//
//                    }
//
//                }
//                return null;
//            }
//        }.visit(ast);


//merge unneeded suite


        // I want to unify our vars


        return ast;


    }

    //todo class variation
    private static Tree.Node walk(Tree tokens) throws Exception {

        //an awful design to use parems here


        Tree.Node currentToken = tokens.getNode(current);

        if (currentToken == null) {
            return null;
        }
        current++;
        String type = currentToken.getStringData(TREE_TYPE);
        String value = currentToken.getStringData(TREE_VALUE);


        // System.out.println(currentToken);
        switch (type) {


            case TREE_TOKEN_NUMBER: {

                return new Tree.Node(TREE_TYPE, AST_TYPE_NUMBER_LITERAL, TREE_VALUE, value);
                //break;
            }
            case TREE_TOKEN_STRING: {

                return new Tree.Node(TREE_TYPE, AST_TYPE_STRING_LITERAL, TREE_VALUE, value);
                //break;
            }
            case AST_TYPE_OPERATION: {

                return new Tree.Node(TREE_TYPE, AST_TYPE_OPERATION, TREE_VALUE, value);
                // break;
            }

            case TREE_TOKEN_PAREN: {

                // current--;
                //  Tree.Node token = currentToken;
                //currentToken = tokens.getNode(current - 1);
                //because name comes before paren    (tokenLeft.getStringData(VALUE).matches("if|else|while")) &
                //just check if it is a block


                if (Objects.equals(currentToken.getStringData(TREE_VALUE), "(")) {

                    Tree.Node node = new Tree.Node(TREE_TYPE, AST_TYPE_EXPRESSION, TREE_VALUE, Compiler.getExpressionName(current));
                    // currentToken = tokens.getNode(current);//now is right ...

                    while (!Objects.equals(tokens.getNode(current).getStringData(TREE_VALUE), ")")) {
                        // currentToken = tokens.getNode(current);
                        node.addChild(walk(tokens));
                        //currentToken = tokens.getNode(current - 1);
                    }
                    //current++;
                    return node;
                }
                if (Objects.equals(currentToken.getStringData(TREE_VALUE), "{")) {

                    Tree.Node node = new Tree.Node(TREE_TYPE, AST_TYPE_CODE_BLOCK, TREE_VALUE, Compiler.getCodeBlockName(current));
                    // currentToken = tokens.getNode(current);//now is right ...

                    while (!Objects.equals(tokens.getNode(current).getStringData(TREE_VALUE), "}")) {
                        //amazing fix
                        // currentToken = tokens.getNode(current);
                        node.addChild(walk(tokens));

                    }
                    //current++;
                    return node;
                } else {
                    return null;
                }


                // break;
            }
            // todo keep these codeblocks please

            case TREE_TOKEN_NAME: {


                //here already current plus


                //current is future

                Tree.Node node;

                //is a var type declaration
                if (value.matches(TYPE_PATTERN)) {
                    // currentToken = tokens.getNode(++current);
                    //  node = new Tree.Node(TREE_TYPE, AST_TYPE_TYPE_DECLARATION, TREE_VALUE, value);


                    currentToken = walk(tokens);

                    if (AST_TYPE_FUNCTION.equals(currentToken.getStringData(TREE_TYPE))) {

                        currentToken.putData(AST_FUNCTION_RETURN_TYPE, DataType.getDataType(value));//.addChild(walk(tokens));
                        currentToken.putData(AST_FUNCTION_DEFINED, false);
                    } else if (AST_TYPE_VARIATION.equals(currentToken.getStringData(TREE_TYPE))) {
                        currentToken.putData(AST_VARIATION_DATA_TYPE, DataType.getDataType(value));


                        //currentToken.addChild(walk(tokens));// must be a ()

                        //currentToken.addChild(walk(tokens));//must be a {}
                    } else if (AST_TYPE_CLASS.equals(currentToken.getStringData(TREE_TYPE))) {
                        //current++;
                        break;
                    }

                    //current++;
                    return currentToken;

                    //used by functon declition too  but i planto departch it
                } else if (value.matches(CONTROL_PATTERN)) {

                    //is an "if" or "else"
                    node = new Tree.Node(TREE_TYPE, AST_TYPE_CONTROL, TREE_VALUE, currentToken.getStringData(TREE_VALUE));


                    boolean wantExpression = false;
                    boolean wantAction = false;
                    boolean actAsKeyword = false;
                    switch (value) {
                        case "if":
                        case "switch":
                        case "case":
                        case "for"://todo not ok for for statements
                        case "while":
                            wantExpression = true;
                            wantAction = true;

                            break;
                        case "else":

                            wantAction = true;
                            break;
                        case "return":
                            // wantExpression = true;
                            break;
                        default:


                    }


                    //here comes many matches
                    if (wantExpression) {
                        Tree.Node expressionNode = walk(tokens);
                        if (AST_TYPE_EXPRESSION.equals(expressionNode.getStringData(TREE_TYPE)) || AST_TYPE_VARIATION.equals(expressionNode.getStringData(TREE_TYPE))) {
                            node.addChild(expressionNode);
                        } else {
                            throw new Exception("no condition");
                        }
                    }

                    if (wantAction) {
                        Tree.Node actionNode = walk(tokens);
                        if (AST_TYPE_CODE_BLOCK.equals(actionNode.getStringData(TREE_TYPE)) || AST_TYPE_CONTROL.equals(actionNode.getStringData(TREE_TYPE))) {
                            node.addChild(actionNode);
                        } else {
                            throw new Exception("no methods");
                        }
                    }

                    return node;


                } else if ("new".equals(value)) {
                    currentToken = walk(tokens);

                    if (AST_TYPE_FUNCTION.equals(currentToken.getStringData(TREE_TYPE))) {

                        currentToken.putData(AST_FUNCTION_RETURN_TYPE, AST_FUNCTION_RETURN_NEW_OBJECT);
                        currentToken.putData(AST_FUNCTION_DEFINED, true);
                    }
                    return currentToken;

                } else if (value.matches(KEY_WORD_PATTERN)) {


                    //little fix


                    KEY_WORDS_RECORDER.clear();
                    // KEY_WORDS_RECORDER.add(tokenWithName.getStringData(TREE_VALUE));
                    while (currentToken.getStringData(TREE_VALUE).matches(KEY_WORD_PATTERN)) {
                        KEY_WORDS_RECORDER.add(currentToken.getStringData(TREE_VALUE));
                        currentToken = currentToken.right;
                        current++;
                    }
                    current = current - 1;
                    currentToken = walk(tokens).putData(AST_VAR_FUNC_CLASS_KEYWORD, KEY_WORDS_RECORDER);

                    return currentToken;


                } else {

                    //is a func or var   or a class

                    //current Token is future token
                    if (Objects.equals(currentToken.right.getStringData(TREE_TYPE), TREE_TOKEN_PAREN) && Objects.equals(currentToken.right.getStringData(TREE_VALUE), "(")) {
                        node = new Tree.Node(TREE_TYPE, AST_TYPE_FUNCTION, TREE_VALUE, value);


                        node.addChild(walk(tokens));
                        node.putData(AST_FUNCTION_DEFINED, true);
                        if ("{".equals(tokens.getNode(current++).getStringData(Compiler.TREE_VALUE))) {
                            node.addChild(walk(tokens));
                            node.putData(AST_FUNCTION_DEFINED, false);

                        } else {
                            node.addChild(walk(tokens));
                        }

                        //func
//                        currentToken = tokens.getNode(++current);
//
//                        //todo expression stands for args
//                        Tree.Node exepressionNode = new Tree.Node(TREE_TYPE, AST_TYPE_EXPRESSION, TREE_VALUE, AST_EXPRESSION_AS_ARGS);
//
//
//
//
//                        while ((!Objects.equals(currentToken.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) || (Objects.equals(currentToken.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) & !Objects.equals(currentToken.getStringData(TREE_VALUE), ")")) {
//                            node.addChild(walk(tokens));
//                            currentToken = tokens.getNode(current);
//                        }
//
//                        current++;

                        return node;

                    } else if (Objects.equals(currentToken.right.getStringData(TREE_TYPE), TREE_TOKEN_PAREN) && Objects.equals(currentToken.right.getStringData(TREE_VALUE), "{")) {
                        node = new Tree.Node(TREE_TYPE, AST_TYPE_CLASS, TREE_VALUE, value);
                        //current++;
                        node.addChild(walk(tokens));
//                        //todo extends
//                        //func
//                        currentToken = tokens.getNode(++current);
//
//                        while ((!Objects.equals(currentToken.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) || (Objects.equals(currentToken.getStringData(TREE_TYPE), TREE_TOKEN_PAREN)) & !Objects.equals(currentToken.getStringData(TREE_VALUE), "}")) {
//                            node.addChild(walk(tokens));
//                            currentToken = tokens.getNode(current);
//                        }
//                        current++;

                        return node;
                    } else {
                        //var
                        // ++current;
                        return new Tree.Node(TREE_TYPE, AST_TYPE_VARIATION, TREE_VALUE, value);
                    }
                }

                // break;
            }


//            default: {
//
//                throw new Exception(type);
//                //break;
//            }


        }


        return null;
    }

    private static String getExpressionName(int index) {
        return "_EXPRESSION_" + index + "_ignore";
    }

    private static String getCodeBlockName(int index) {
        return "_CODE_BLOCK_" + index + "_ignore";
    }
    /*
     * @param tokens
     * @return numberLiteral StringLiteral FUNCTION Variation ?optMark control
     */
//    @Deprecated
//    public static Tree loader(String path) throws Exception {
//
//
//        //
//        //project class classes(import from here)
//        //
//        //
//        String text = "";
//        try {
//            text = new String(Files.readAllBytes(Paths.get(path)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // this is important to and an end
//
//        Tree mlogProject = tokenizer(text + "\nend: ");
//        Tree library = preParser(mlogProject, "func");
//
//        library = parser(library);
//
//
//        //no more operation
//        // library = semanticParser(library);
//
//
//        String nameSpace = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
//
//
//        //give it an id
//        new Tree.visitor() {
//
//
//            @Override
//            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
//                if (node.hasParent() && Compiler.AST_TYPE_FUNCTION.equals(node.parent.getStringData(Compiler.TREE_TYPE))) {
//                    return node.getData(Compiler.AST_VARIATION_DATA_TYPE);
//
//                }
//                if (Compiler.AST_TYPE_FUNCTION.equals(node.getStringData(Compiler.TREE_TYPE))) {
//
//
//                    StringBuilder sb = new StringBuilder(nameSpace).append('.').append(node.getStringData(Compiler.TREE_VALUE));
//
//                    // no need consider func in func
//                    for (Object dt : dataFromChildren) {
//
//                        if (dt != null) {
//                            sb.append('_').append(dt);
//                        }
//
//
//                    }
//                    node.putData(Library.FUNC_ID, sb.toString());
//                }
//                return null;
//            }
//
//
//        }.visit(library);
//
//
//        mlogProject = new Tree().addNode(library.putData(Library.LIBRARY_SORT_NAME, Library.LIBRARY)).putData(Library.NAMESPACE, nameSpace);
//
//
//        Library.addLibrary(mlogProject);
//        return Library.FunctionTree;
//
//
//    }

    private String getArgument(int index) {
        return "Argument_" + index + "_ignore";
    }

    static class Library {


        public final static String FUNC_ID = "fID";
        public final static String CLASS_ID = "cID";
        static final Tree FunctionTree = new Tree();
        final static String VAR_LIST = "var list";

        static final HashMap<String, Tree.Node> functionMap = new HashMap<>();

        final static String NAMESPACE = "nameSpace";
        final static String LIBRARY = "library";
        final static String LIBRARY_SORT_NAME = "name";

        final static String CONTENT = "content";
        static final String UNDEFINED_FUNCTION = "undefined function";
        static final String NATIVE_CODE = "native code";
        final static String NATIVE_FUNCTION = "native";

        static final String ARGS = "args";
        static final String RETURNS = "return";
        static final String FUNC_SHORT_ID = "shortFID";

        static void addFunction(Tree.Node node) {


            //todo if i should copy

            functionMap.put(node.getStringData(FUNC_ID), node.copyTo());


            Library.FunctionTree.addNode(node);


        }


        //todo how to fin the nearest function not add(obj) but add(num)   as long as

        static Object[] getReturnDataType(String name, DataType[] argTypes) {


            String[] findAcc = name.split("\\.");
            if (findAcc.length != 0) {
                int gotFound = 0;

                String findAccording = findAcc[findAcc.length - 1];

                Tree.Node function = FunctionTree.findNodeReset().findNode(Compiler.TREE_VALUE, findAccording);
                while (function != null) {
                    if (argTypes.length != function.children.size()) {

                        continue;
                    }
                    boolean pass = false;
                    int i = 0;
                    for (DataType argType : argTypes) {
                        if (!argType.isInstanceOf((function.getFirstChild().getChild(i).getData(Compiler.AST_VARIATION_DATA_TYPE))) && !argType.isInstanceOf((function.getFirstChild().getChild(i).getData(Compiler.AST_FUNCTION_RETURN_TYPE)))) {
                            pass = true;

                        }
                        i++;

                    }
                    if (pass) {
                        function = FunctionTree.findNextNode(Compiler.TREE_VALUE, findAccording);
                    } else {
                        gotFound++;
                    }


                }
                if (gotFound == 1) {
                    return new Object[]{function.getData(Compiler.AST_FUNCTION_RETURN_TYPE), function.getData(FUNC_ID)};
                }
            }


            //todo here it comes


            // todo order user added             other class          native


            //todo find in obj list to class


            //todo maybe I can use a different way to end this


            //  Tree.Node nextFound=FunctionTree.findNodeReset().findNextNode(FUNC_SHORT_ID, Shortid) ;


            //maybe stable

            Tree.Node function = FunctionTree.findNodeReset().findNode(FUNC_SHORT_ID, name);
            while (function != null) {
                if (argTypes.length != function.children.size()) {

                    continue;
                }
                boolean pass = false;
                int i = 0;
                for (DataType argType : argTypes) {
                    if (!argType.isInstanceOf((function.getFirstChild().getChild(i).getData(Compiler.AST_VARIATION_DATA_TYPE))) && !argType.isInstanceOf((function.getFirstChild().getChild(i).getData(Compiler.AST_FUNCTION_RETURN_TYPE)))) {
                        pass = true;

                    }
                    i++;

                }
                if (pass) {
                    function = FunctionTree.findNextNode(FUNC_ID, name);
                } else {
                    return new Object[]{function.getData(Compiler.AST_FUNCTION_RETURN_TYPE), function.getData(FUNC_ID)};
                }


            }


            return new Object[]{DataType.OBJECT, UNDEFINED_FUNCTION};

        }


    }

    static class DataType implements Serializable {
        static final HashMap<String, DataType> MAP = new HashMap();
        static final Tree DataTypes = new Tree();
        static final DataType OBJECT = new DataType("obj");

        static {
            DataTypes.root = OBJECT.node;
        }

        public final String name;
        Tree.Node node = new Tree.Node();
        DataType father;

        private DataType(String name) {

            this.name = name;
            DataType.MAP.put(name, this);
            this.node.putData("name", name);
        }

        public static String getAllDataTypesNames() {
            return OBJECT.getAllChildrenNames() + OBJECT.name;

        }

        public static DataType createDataType(String name) {

            return new DataType(name).setFather(OBJECT);
        }

        public static DataType getDataType(String name) {
            if (MAP.containsKey(name)) {
                return MAP.get(name);
            } else {
                return createDataType(name);
            }

        }

        //terrible fix
        public static boolean isInstanceOf(DataType local, DataType target) {

            if (OBJECT.equals(target) || local.equals(target)) {
                return true;
            } else {
                if (local.equals(OBJECT)) {
                    return false;
                } else {
                    return isInstanceOf(local.father, target);
                }

            }


        }

        public static DataType createDataType(String name, DataType father) {
            return new DataType(name).setFather(father);
        }

        /**
         * include equal situations
         */
        public boolean equals(DataType other) {
            if (other == null) {
                return false;
            } else return this.name.equals(other.name);

        }

        public String getAllChildrenNames() {

            final StringBuilder builder = new StringBuilder();
            new Tree.visitor() {

                @Override
                public Object execute(Tree.Node child, ArrayList<Object> dataFromChildren) {
                    builder.append(child.getStringData("name"));
                    builder.append('|');
                    return null;
                }


            }.visit(this.node);


//
            return builder.toString();


        }

        public String toString() {
            //"TYPE: " +
            return this.name;
        }

        private DataType setFather(DataType father) {
            this.father = father;
            father.node.addChild(this.node);
            return this;
        }

        public boolean isInstanceOf(Object target) {

            if (target != null) {
                return isInstanceOf(this, (DataType) target);
            } else return this.equals(OBJECT);

        }
    }

}
//only designed for main


//    enum TreeAttribute {
//        TREE_TYPE,
//        TREE_VALUE,
//        TREE_TOKEN_PAREN,
//        TREE_TOKEN_STRING,
//        TREE_TOKEN_NUMBER,
//        TREE_TOKEN_NAME,
//        AST_TYPE_NUMBER_LITERAL,
//        AST_TYPE_STRING_LITERAL,
//        AST_TYPE_FUNCTION,
//        AST_TYPE_EXPRESSION,
//        AST_TYPE_VARIATION,
//        AST_TYPE_CODE_BLOCK,
//        AST_TYPE_TYPE_DECLARATION,
//        AST_TYPE_CONTROL,
//        AST_TYPE_OPERATION,
//        AST_FUNCTION_RETURN_TYPE,
//        AST_FUNCTION_ID,
//        AST_FUNCTION_DEFINED,
//        AST_FUNCTION_CONTENT,
//        AST_VARIATION_DATA_TYPE,
//        RETURN_VAR;
//    }
//


