import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        String code;
        Compiler compiler = new Compiler();


        String nativeLibrary=readFileAsString("MLogBin/native.mlog");






        Tree Ntokens = compiler.tokenizer( nativeLibrary);

        // System.out.println(tokens.toString());

        Tree NLibrary = compiler.preParser(Ntokens, "main");

        System.out.println(NLibrary.toString());









//        code=readFileAsString("MLogProjects/main.mlog");
//
//
//
//
//
//
//
//        System.out.println(code);
//        Tree tokens = compiler.tokenizer( code+"\s");
//
//       // System.out.println(tokens.toString());
//
//        Tree pre1 = compiler.preParser(tokens, "main");
//        System.out.println(pre1.toString());
//
//        Tree ast=compiler.parser(pre1);
//
//
//        System.out.println("\nAST: \n" + ast);



    }
    //read and append all to a String  from a  file



    public static String readFileAsString(String path) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }




}









