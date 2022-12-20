import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 */
public class Main {
    static Compiler compiler = new Compiler();


    public static void main(String[] args) throws Exception {





        Tree nativeLibrary= loader("MLogBin/native.mlog");

        Library.compileLibraries();

//        System.out.println("-------------------------------------------------");
//        System.out.println(nativeLibrary);
//        System.out.println("-------------------------------------------------");





        String code;
        code="main: x=native.add(1,2); x=1; return 0; end: ";


        //System.out.println(code);
        Tree tokens = compiler.tokenizer( code+"\s");
     //   System.out.println("-------------------------------------------------");
        Tree pre1 = compiler.preParser(tokens, "main");

        Tree ast=compiler.parser(pre1);
      //  System.out.println("\nAST: \n" + ast);
       // System.out.println("-------------------------------------------------");
       ast=compiler.semanticParser(ast);

        System.out.println("\nAST: \n" + ast);

        System.out.println("-------------------------------------------------");

    }






    public static Tree  loader(String path) throws Exception {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // this is important to and an end
        Tree mlogProject = compiler.tokenizer(text+"\nend: ");
        Tree library = compiler.preParser(mlogProject, "func");
        library = compiler.parser(library);
        library= compiler.semanticParser(library);


        String nameSpace=        path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));


        //give it an id
       new Tree.visitor(){


            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {
                if (node.hasParent()&&Compiler.AST_TYPE_FUNCTION.equals(node.parent.getStringData(Compiler.TREE_TYPE))){
                    return node.getData(Compiler.AST_VARIATION_DATA_TYPE);

                }
                if (Compiler.AST_TYPE_FUNCTION.equals(node.getStringData(Compiler.TREE_TYPE))){


                    StringBuilder sb = new StringBuilder(nameSpace)
                            .append('.')
                            .append(node.getStringData(Compiler.TREE_VALUE));

                    // no need consider func in func
                    for (Object dt:
                         dataFromChildren) {

                        sb.append('_')
                        .append(dt.toString());

                    }
                    node.putData(Library.FUNC_ID,sb.toString());
                }
                return null;
            }


        }.visit(library);




        mlogProject=new Tree().addNode(library.putData(Library.LiBRARY_SORT_NAME, Library.LIBRARY)).putData(Library.NAMESPACE, nameSpace);


        Library.addLibrary(mlogProject);
        return Library.FunctionTree;





    }



}









