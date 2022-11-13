import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings({"RawUseOfParameterized", "unchecked"})
public class compiler {
    static final HashMap<String, String[]> logicDictionary = new HashMap<>();
   

    static {
        logicDictionary.put("add_2", new String[]{"add result a b", "a", "b"});
        logicDictionary.put("add_0", new String[]{"add result "});
    }

    int current;
class node {
     JSONObject data;
     compiler.node parent;
     ArrayList<compiler.node> children = new ArrayList<compiler.node>();

    node(String name, compiler.node parent) {
        this.parent = parent;
        data = new JSONObject();
        data.put("name", name);
        data.put("children", children);

    }

    compiler.node generate(String name) {
        compiler.node t = new node(name, this);
        children.add(t);
        return t;
    }

    compiler.node put(String name, Object value) {
        data.put(name, value);
        return this;
    }
    Object get(String name) {
        return data.get(name);
    }
    String getStringData(String name) {
        return get(name).toString();
    }



    Iterator iterator = new Iterator<compiler.node>() {//返回一个迭代器的对象

        private int cur = -1;//指针

        public boolean hasNext() {//迭代器的方法
            return cur !=children.size();//a数组末尾指针
        }
        public boolean hasChildOfChild() {//迭代器的方法
            return children.get(cur).children.iterator().hasNext();//a数组末尾指针
        }

        public compiler.node next() {//迭代器的方法
            cur++;
            return children.get(cur);//返回一个元素
        }


    };
    void addChild(compiler.node child) {
        children.add(child);
        child.parent=this;
    }
}
class tree{
     final String name;
     compiler.node root;


    tree(String name){
        this.name = name;
        this.root = new node(name, null);
    }
    node generateNode(String name){

        return new node(name, this.root);

    }
    tree addNode(String name){
        root.generate(name);
        return this;
    }
    Iterator getIterator(){
        return root.iterator;
    }
}







    private static HashMap<String, Object> dic(String type, Object value, ArrayList<Object> params, ArrayList<Object> body) {
        HashMap<String, Object> dic = new HashMap<>();
        dic.put("type", type);
        if (value != null) {
            dic.put("value", value);
        }
        if (params != null) {
            dic.put("parems", params);
        }
        if (body != null) {
            dic.put("body", body);

        }

        return dic;
    }

    final String  TOKENS="tokens";

    ArrayList<Object> tokenizer(String input) throws Exception {
        int current = 0;
        // Object[] tokens= new Object[]{};
        ArrayList<Object> tokens = new ArrayList<>();
        tree tokensTree=new tree("tokens");
        while (current < input.length()) {

            char character = input.charAt(current);
            if (character == '(') {
                tokens.add(dic("paren", "("));

                tokensTree.generateNode(TOKENS).put("paren", "(");
                current++;
                continue;
            }
            if (character == ')') {
                tokens.add(dic("paren", ")"));
                tokensTree.generateNode(TOKENS).put("paren", ")");

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
                tokens.add(dic("number", value.toString()));
                continue;
            }


            if (character == '"') {
                character = input.charAt(++current);
                StringBuilder value = new StringBuilder();
                while (character != '"') {
                    value.append(character);
                    character = input.charAt(++current);
                }
                tokens.add(dic("string", value.toString()));
                continue;
            }

            if (Pattern.compile("[a-z]").matcher(character + "").find()) {

                StringBuilder value = new StringBuilder();
                while (Pattern.compile("[a-z]").matcher(character + "").find()) {


                    value.append(character);
                    character = input.charAt(++current);


                }
                tokens.add(dic("name", value.toString()));
                continue;
            }


            throw new Exception("I don't know what this character is: " + character);

        }


        return tokens;
    }

    @SuppressWarnings("RawUseOfParameterized")
    HashMap<String, Object> parser(@SuppressWarnings("RawUseOfParameterized") ArrayList tokens) throws Exception {
        current = 0;


        HashMap<String, Object> ast = dic("Program", new ArrayList<>());
        while (current < tokens.size()) {
            ArrayList body = (ArrayList) ast.get("body");
            body.add(walk(tokens));
            ast.put("body", body);

        }

        return ast;


    }

    @SuppressWarnings("RawUseOfParameterized")
    private HashMap<String, Object> walk(ArrayList tokens) throws Exception {

        HashMap<String, String> token = (HashMap<String, String>) tokens.get(current);

        if (Objects.equals(token.get("type"), "number")) {
            current++;
            return dic("NumberLiteral", token.get("value"));
        }
        if (Objects.equals(token.get("type"), "string")) {
            current++;
            return dic("StringLiteral", token.get("value"));
        }

        if (Objects.equals(token.get("type"), "name")) {

            HashMap<String, String> tokenWithName = token;

            token = (HashMap<String, String>) tokens.get(++current);


            if (Objects.equals(token.get("type"), "paren") & Objects.equals(token.get("value"), "(")) {

                HashMap<String, Object> node = dic("CallExpression", tokenWithName.get("value"), new ArrayList<>());
                token = (HashMap<String, String>) tokens.get(++current);


                while ((!Objects.equals(token.get("type"), "paren")) || (
                        Objects.equals(token.get("type"), "paren")) & !Objects.equals(token.get("value"), ")")) {
                    ArrayList<Object> parems = (ArrayList<Object>) node.get("parems");
                    parems.add(walk(tokens));
                    node.put("parems", parems);
                    token = (HashMap<String, String>) tokens.get(current);

                }
                current++;
                return node;


            } else {
                return dic("CallVariation", tokenWithName.get("value"));
            }


        }


        throw new Exception(token.get("type"));
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

            code = code.replaceAll("\s"+codeSetting[++i]+"\s", "\s"+argument+"\s");
        }


        return code;
    }


    private String getArgument(int index) {
        return "Argument_" + index + "_ignore";

    }


    private HashMap<String, Object> dic(String type, String value) {

        return dic(type, value, null, null);
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
                        ((ArrayList) parent.get("_context")).add(compiler.dic(type, node.get("value"), null, null));
            }
        }

        @SuppressWarnings("EmptyMethod")
        public void exit(HashMap<String, Object> node, HashMap<String, Object> parent) {
        }
    }
}
