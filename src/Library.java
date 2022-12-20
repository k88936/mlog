import java.util.HashMap;

public class Library {


    public final static String FUNC_ID = "ID";

    static final Tree FunctionTree = new Tree();


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

//todo this is useless
        Library.FunctionTree.addNode(mlogLibrary);


    }

    static void compileLibraries() {

        for (String id : functionList.keySet()) {

            Compiler.compileLibrary(id);

        }

        System.out.println("library compile complete");
        System.out.println(functionList);
        System.out.println("----------------------------------------------------------------------------------------");

    }
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

    static Object[] getReturnDataType(String nameSpace, String name, DataType[] argTypes) {


        //todo find in obj list to class


        //todo maybe i can use a different way to end this


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


        //todo Library with out put to string job

        // throw new Exception("no such method");

        return new Object[]{DataType.OBJECT, UNDEFINED_FUNCTION};

    }


}
