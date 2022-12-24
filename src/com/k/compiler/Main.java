package com.k.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    //static Compiler compiler = new Compiler();


    static String code;

    static {
        try {
            code = new String(Files.readAllBytes(Paths.get("MLogBin/native.jml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        Tree jast = Compiler.parser(Compiler.tokenizer(code));
        Tree jst = Compiler.loader(jast);

//System.out.println(jst);


        System.out.println("-------------------------------------------------");


        Compiler.analyzing(jst);







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
//


    }


}









