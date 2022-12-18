import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 */
public class Main {
    static Compiler compiler = new Compiler();


    public static void main(String[] args) throws Exception {





        Tree nativeLibrary= loader("MLogBin/native.mlog");

        System.out.println("-------------------------------------------------");
        System.out.println(nativeLibrary);
        System.out.println("-------------------------------------------------");





        String code;
        code="main:\n" +
                "if(){\n" +
                "\n" +
                " native.add(1 1)\n" +
                " }\n" +
                "setup:\n" +
                "\n" +
                "loop:\n" +
                "\n" +
                "func:\n" +
                "end:";


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
        mlogProject=new Tree().addNode(library.putData(Library.NAME, Library.LIBRARY)).putData(Library.NAMESPACE, nameSpace);


        Library.addLibrary(mlogProject);
        return Library.FunctionTree;





    }



}









