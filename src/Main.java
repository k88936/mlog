import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        String code;
        compiler compiler = new compiler();

        code="add(1 2)";

        System.out.println(code);
        ArrayList<Object> tokens = compiler.tokenizer( code);



        System.out.println(tokens);
        HashMap<String, Object> ast = compiler.parser(tokens);
        System.out.println(ast);
        HashMap<String, Object> newAst = compiler.transformer(ast);
        System.out.println(newAst);




        ArrayList<HashMap<String, Object>> array = new ArrayList<>();
        compiler.argumentDistribute(newAst,array,0);
        System.out.println(array);
        String outPut = compiler.codeGenerator(array);
        System.out.println(outPut);
    }
}









