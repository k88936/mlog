import java.util.ArrayList;
import java.util.HashMap;

public class Library {


    public final static String FUNC_ID = "ID";
    //public final String nameSpace;
    // DataType returnType;
    //DataType[] argTypes;
    //String name;

    static final Tree FunctionTree = new Tree();


    static final HashMap<String, Tree.Node> functionList = new HashMap();

    final static String NAMESPACE = "nameSpace";
    final static String LIBRARY = "library";
    final static String LiBRARY_SORT_NAME = "name";
    final static String METHOD = "method";

    final static String CONTENT = "content";
    static final String UNDEFINED_FUNCTION = "undefined function";
    static final String NATIVE_CODE = "native code";

    static void addLibrary(Tree mlogLibrary) {


        for (Tree.Node func : mlogLibrary.getNode(LiBRARY_SORT_NAME, LIBRARY).children) {

            functionList.put(func.getStringData(FUNC_ID), func);


        }

//todo this is useless
        Library.FunctionTree.addNode(mlogLibrary);


    }

    final static String RAW_FUNCTION = "raw";
    static void compileLibraries() {

        for (String id : functionList.keySet()) {

            compile(id);

        }

        System.out.println("library compile complete");
        System.out.println(functionList);
        System.out.println("----------------------------------------------------------------------------------------");

    }
    static void compile(String id){
        Tree.Node func = functionList.get(id);
        Tree.Node content = (Tree.Node) func.getData(CONTENT);


        HashMap<String,DataType> variationMap = new HashMap<String,DataType>();


        for (Tree.Node var : func.children
             ) {
            variationMap.put(var.getStringData(Compiler.TREE_VALUE), (DataType) var.getData(Compiler.AST_VARIATION_DATA_TYPE));

        }


        //todo auto capcity
        String[] results=new String[1];
        //todo it seemed that i can reuse the repeated code
        new Tree.visitor() {








            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                switch (node.getStringData(Compiler.TREE_TYPE)) {

                    case Compiler.AST_TYPE_FUNCTION: {
//                        if (node.getData(Compiler.AST_FUNCTION_DEFINED) != null) {
//                            return null;
//                        }

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
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                            //e.printStackTrace();
                        }

                        return node.getData(Compiler.AST_FUNCTION_RETURN_TYPE);

                    }
                    //break;
                    case Compiler.AST_TYPE_VARIATION: {
                        if (node.getStringData(Compiler.AST_VARIATION_DATA_TYPE) != null) {

                        } else {

                            Object type= variationMap.get(node.getStringData(Compiler.TREE_VALUE));
                            if (type != null) {
                                node.putData(Compiler.AST_VARIATION_DATA_TYPE, type);
                            }else {
                                node.putData(Compiler.AST_VARIATION_DATA_TYPE, Compiler.DATA_OBJECT);
                            }
                           // variationMap.put(var.getStringData(Compiler.TREE_VALUE), (DataType) var.getData(Compiler.AST_VARIATION_DATA_TYPE));


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


        }.visit(content);


        //todo register variation


        //todo maybe none-useful like this









        StringBuilder NativeCode=new StringBuilder();
        //func is your home
        new Tree.visitor(){

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {





                switch (node.getStringData(Compiler.TREE_TYPE)){











                    case Compiler.AST_TYPE_FUNCTION :{
                        if(RAW_FUNCTION.equals(node.getStringData(Compiler.TREE_VALUE))) {

                            String rawCode= node.getFirstChild().getStringData(Compiler.TREE_VALUE);

                            for (int i = 1; i < node.children.size(); i++) {
                                if (node.children.get(i)!= null) {
                                    //todo raw must like this
                                    String param=node.children.get(i).getStringData(Compiler.TREE_VALUE);
                                    String[] paramSplited = param.split("_");

                                    String Type=paramSplited[0];
                                    String Name=paramSplited[1];



                                    rawCode=rawCode.replaceAll("\s"+param, "\s"+Name);
                                }

                            }
                            NativeCode.append(rawCode);
                            //NativeCode.append("\n");






                        }else {

                            //todo dangerous
                            if (!(Boolean) functionList.get(node.getData(FUNC_ID)).getData(Compiler.AST_FUNCTION_DEFINED)){
                                compile(node.getStringData(FUNC_ID));



                            }
                            String code=functionList.get(node.getData(FUNC_ID)).getStringData(NATIVE_CODE);
                            String[] subArgs= (String[]) functionList.get(node.getData(FUNC_ID)).getData(ARGS);
                            String[] subReturn= (String[]) functionList.get(node.getData(FUNC_ID)).getData(RETURNS);
                            for (int i = 0; i < node.children.size(); i++) {
                               code= code.replaceAll("\s"+subArgs[i],"\s"+node.getChild(i).getStringData(Compiler.TREE_VALUE));
                            }
                            if (node.getData(Compiler.RENTURN_VAR) != null) {
                                code= code.replaceAll("\s"+subReturn[0],"\s"+node.getStringData(Compiler.RENTURN_VAR)) ;
                            }



                            NativeCode.append(code);
                            NativeCode.append("\n");


                        }






                        break;
                    }

                    case Compiler.AST_TYPE_CONTROL:{


                        //it is stupid to use multi callback
                        switch (node.getStringData(Compiler.TREE_VALUE)){
                            case "return":{
                                for (int i = 0; i < node.children.size(); i++) {
                                    results[0]=(node.getChild(i).getStringData(Compiler.TREE_VALUE));
                                }

                                break;
                            }
                        }
                        break;
                    }
                }





                return null;
            }
        }.visit(content);


        String[] args = new String[func.children.size()];
        for (int i = 0; i < func.children.size(); i++) {
            args[i] = func.children.get(i).getStringData(Compiler.TREE_VALUE);
        }
        //todo



        func.putData(NATIVE_CODE, NativeCode.toString(),Compiler.AST_FUNCTION_DEFINED,true);

        func.putData(ARGS,args,RETURNS,results);








    }


    static final String ARGS="args";
    static final String RETURNS="return";
  //  static final String ID="id";
//    class Function{
//
//        public String functionId;
//        public String name;
//        public String[] returnVar;
//        public String[] args;
//
//        public String toNative() {
//
//        }
//
//
//    }



    //todo how to fin the nearest function not add(obj) but add(num)



    static Object[] getReturnDataType(String nameSpace, String name, DataType[] argTypes) throws Exception {


        //todo find in obj list to class


        //todo maybe i can use a different way to end this


        for (int i = 0; i < FunctionTree.getSize(); i++) {
            Tree.Node classTree = FunctionTree.getNode(i);


            if (nameSpace.equals(classTree.getStringData(NAMESPACE))) {
                for (int t = 0; t < classTree.children.size(); t++) {
                    // library index=1 not stable
                    Tree.Node func = classTree.getChild(LiBRARY_SORT_NAME, LIBRARY).getChild(t);
                    if (name.equals(func.getStringData(Compiler.TREE_VALUE))) {
                        // a big mistake caused by the so many const


                        boolean pass = false;
                        if (argTypes.length!=func.children.size()) {

                            continue;
                        }
                        for (int j = 0; j < argTypes.length; j++) {
                            if (!argTypes[j].isInstanceOf( (func.getChild(i).getData(Compiler.AST_VARIATION_DATA_TYPE))) && !argTypes[j].isInstanceOf((func.getChild(i).getData(Compiler.AST_FUNCTION_RETURN_TYPE)))) {
                                pass = true;
                            }
                        }
                        if (pass) {
                            continue;
                        } else {


                            return new Object[]{func.getData(Compiler.AST_FUNCTION_RETURN_TYPE), func.getData(FUNC_ID)};


                            //Tree.Node content = (Tree.Node) func.getData(CONTENT);
                            // content = content.copyTo();
                        }


                    }


                }
            }


        }


        //todo Library with out put to string job

        // throw new Exception("no such method");

        return new Object[]{DataType.OBJECT, UNDEFINED_FUNCTION};

    }


}
