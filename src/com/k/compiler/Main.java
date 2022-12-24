package com.k.compiler;

public class Main {
    //static Compiler compiler = new Compiler();


    public static void main(String[] args) throws Exception {

        //
        var code = " public class main{ static  void java ( num a , num b ){} }";
        // Tree nativeLibrary = Compiler.loader("MLogBin/native.mlog");
        System.out.println(code);
        // Library.compileLibraries();
        System.out.println("-------------------------------------------------");
        com.k.compiler.Tree tokens = Compiler.tokenizer(code + "\s");

        com.k.compiler.Tree ast = Compiler.parser(tokens);
        System.out.println("\nAST: \n" + ast);
        System.out.println("-------------------------------------------------");
        System.exit(100);

//        String code;
//        code = "main:  x=1+2 end: ";
//
//
//        //
//
//     //
//        com.k.compiler.Tree pre1 = compiler.preParser(tokens, "main");
//
//
//        ast=compiler.semanticParser(ast);
//
//
//
//
//
//       ast= com.k.compiler.Compiler.translator(ast);
//    //    System.out.println("\nAST: \n" + ast);
//
//        System.out.println("-------------------------------------------------");


    }


}









