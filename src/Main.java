/**
 *
 */
public class Main {
    static Compiler compiler = new Compiler();


    public static void main(String[] args) throws Exception {





        Tree nativeLibrary= compiler.loader("MLogBin/native.mlog");

        Library.compileLibraries();


        //System.exit(100);
        String code;
        code="main: num x; x=1+2+3 x=1+2 x=1+2+3+5 end: ";


        //System.out.println(code);
        Tree tokens = compiler.tokenizer( code+"\s");
     //   System.out.println("-------------------------------------------------");
        Tree pre1 = compiler.preParser(tokens, "main");

        Tree ast=compiler.parser(pre1);
        ast=compiler.semanticParser(ast);

        System.out.println("\nAST: \n" + ast);

        System.out.println("-------------------------------------------------");

       ast= Compiler.translator(ast);
    //    System.out.println("\nAST: \n" + ast);

        System.out.println("-------------------------------------------------");


    }







}









