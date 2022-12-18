public class Library {



    public final String nameSpace;
    DataType returnType;
    DataType[] argTypes;
    String name;

    static final Tree FunctionTree= new Tree();





    final static String NAMESPACE="nameSpace";
    final static String LIBRARY="library";
    final static String NAME="name";
    final static String METHOD="method";

    final static String CONTENT="content";

    Library(String nameSpace,String name , DataType returnType, DataType... argTypes) {
        this.nameSpace= nameSpace;
        this.name = name;
        this.returnType = returnType;
        this.argTypes = argTypes;
    }






    static void addLibrary(Tree mlogLibrary) {
        Library.FunctionTree.addNode(mlogLibrary);

    }


    static DataType getReturnDataType(String nameSpace, String name,DataType[] argTypes) throws Exception {


        //todo find in obj list to class

        //todo with instanceOf decide functions before pick up

        //todo maybe i can use a different way to end this


        for (int i = 0; i < FunctionTree.getSize(); i++) {
            Tree.Node classTree = FunctionTree.getNode(i);



            if (nameSpace.equals(classTree.getStringData(NAMESPACE))) {
                for (int t = 0; t < classTree.children.size(); t++) {
                    // library index=1 not stable
                    Tree.Node func = classTree.getChild(0).getChild(t);
                    if (name.equals(func.getStringData(Compiler.TREE_VALUE))){
                        // a big mistake caused by the so many const


                        boolean pass=false;
                        for (int j = 0; j < argTypes.length; j++) {
                            if (!argTypes[j].isInstanceOf((DataType) (func.getChild(i).getData(Compiler.AST_VARIATION_DATA_TYPE)))
                            &&!argTypes[j].isInstanceOf((DataType) (func.getChild(i).getData(Compiler.AST_FUNCTION_RETURN_TYPE)))){
                                pass=true;
                            }
                        }
                        if (pass) {
                          continue;
                        }else {
                            return (DataType) func.getData(Compiler.AST_FUNCTION_RETURN_TYPE);

                            //Tree.Node content = (Tree.Node) func.getData(CONTENT);
                           // content = content.copyTo();
                        }








                    }


                }
            }





        }





        //todo Library with out put to string job

       // throw new Exception("no such method");

        return DataType.OBJECT;

    }

















}
