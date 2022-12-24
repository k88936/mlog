package com.k.compiler;

import java.util.HashMap;

public class Library {


    public final static String FUNC_ID = "fID";
    public final static String CLASS_ID = "cID";
    static final Tree FunctionTree = new Tree();
    final static String VAR_LIST = "var list";

    static final HashMap<String, Tree.Node> functionList = new HashMap<>();

    final static String NAMESPACE = "nameSpace";
    final static String LIBRARY = "library";
    final static String LIBRARY_SORT_NAME = "name";

    final static String CONTENT = "content";
    static final String UNDEFINED_FUNCTION = "undefined function";
    static final String NATIVE_CODE = "native code";
    final static String RAW_FUNCTION = "raw";
    static final String ARGS = "args";
    static final String RETURNS = "return";

    static void addLibrary(Tree mlogLibrary) {


        for (Tree.Node func : mlogLibrary.getNode(LIBRARY_SORT_NAME, LIBRARY).children) {

            functionList.put(func.getStringData(FUNC_ID), func);


        }


        Library.FunctionTree.addNode(mlogLibrary);


    }

    static void compileLibraries() {

        for (String id : functionList.keySet()) {

            if ((Boolean) functionList.get(id).getData(Compiler.AST_FUNCTION_DEFINED)) {
                continue;
            }

            Compiler.compileLibrary(id);

        }

        System.out.println("library compile complete: " + functionList.size());
        System.out.println("----------------------------------------------------------------------------------------");

        for (Tree.Node fc:
                functionList.values()) {
            System.out.println(fc);
            System.out.println("----------------------------------------------------------------------------------------");

        }
        System.out.println("================================================================================");

    }


    //todo how to fin the nearest function not add(obj) but add(num)   as long as

    static Object[] getReturnDataType(String nameSpace, String name, DataType[] argTypes) {


        //todo find in obj list to class


        //todo maybe I can use a different way to end this


        for (int i = 0; i < FunctionTree.getSize(); i++) {
            Tree.Node classTree = FunctionTree.getNode(i);


            if (nameSpace.equals(classTree.getStringData(NAMESPACE))) {
                for (int t = 0; t < classTree.getChild(LIBRARY_SORT_NAME,LIBRARY).children.size(); t++) {
                    // library index=1 not stable
                    Tree.Node func = classTree.getChild(LIBRARY_SORT_NAME, LIBRARY).getChild(t);
                    if (name.equals(func.getStringData(Compiler.TREE_VALUE))) {
                        // a big mistake caused by the so many const


                        boolean pass = false;
                        if (argTypes.length != func.children.size()) {

                            continue;
                        }
                        for (DataType argType : argTypes) {
                            if (!argType.isInstanceOf((func.getChild(i).getData(Compiler.AST_VARIATION_DATA_TYPE)))
                                    && !argType.isInstanceOf((func.getChild(i).getData(Compiler.AST_FUNCTION_RETURN_TYPE)))) {
                                pass = true;
                            }
                        }
                        if (pass) {
                            continue;
                        } else {


                            return new Object[]{func.getData(Compiler.AST_FUNCTION_RETURN_TYPE), func.getData(FUNC_ID)};


                        }


                    }


                }
            }


        }


        //todo com.k.compiler.Library without put to string job


        return new Object[]{DataType.OBJECT, UNDEFINED_FUNCTION};

    }


}
