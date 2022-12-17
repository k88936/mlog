import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        String code;
        Compiler compiler = new Compiler();

//        code="""
//
//        """;










        //code="if{ (s) break(((x)=1))} ";

        code=readFileAsString("MLogProjects/main.mlog");







        System.out.println(code);
        Tree tokens = compiler.tokenizer( code+"\s");

       // System.out.println(tokens.toString());

        Tree pre1 = compiler.preParser(tokens, "main");
        System.out.println(pre1.toString());

        Tree ast=compiler.parser(pre1);
        

        System.out.println("\nAST: \n" + ast);


//        System.out.println(tokens);
//        HashMap<String, Object> ast = Compiler.parser(tokens);
//        System.out.println(ast);
//        HashMap<String, Object> newAst = Compiler.transformer(ast);
//        System.out.println(newAst);
//
//
//
//
//        ArrayList<HashMap<String, Object>> array = new ArrayList<>();
//        Compiler.argumentDistribute(newAst,array,0);
//        System.out.println(array);
//        String outPut = Compiler.codeGenerator(array);
//        System.out.println(outPut);
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









