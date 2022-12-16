public class Main {
    public static void main(String[] args) throws Exception {
        String code;
        compiler compiler = new compiler();

        code="x=(1+1)*2 ";

        System.out.println(code);
        Tree tokens = compiler.tokenizer( code);

       // System.out.println(tokens.toString());

        Tree ast=compiler.parser( tokens);

        System.out.println("\nAST: \n" + ast);


//        System.out.println(tokens);
//        HashMap<String, Object> ast = compiler.parser(tokens);
//        System.out.println(ast);
//        HashMap<String, Object> newAst = compiler.transformer(ast);
//        System.out.println(newAst);
//
//
//
//
//        ArrayList<HashMap<String, Object>> array = new ArrayList<>();
//        compiler.argumentDistribute(newAst,array,0);
//        System.out.println(array);
//        String outPut = compiler.codeGenerator(array);
//        System.out.println(outPut);
    }
}









