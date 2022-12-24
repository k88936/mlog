package com.k.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

        //
//        var code = " public class main{ static  void java ( num a , num b ){} static  void java ( num a , num b ){}}";
//        // Tree nativeLibrary = Compiler.loader("MLogBin/native.mlog");
//        System.out.println(code);
//        // Library.compileLibraries();
//        System.out.println("-------------------------------------------------");
//        com.k.compiler.Tree tokens = Compiler.tokenizer(code + "\s");
//
//        com.k.compiler.Tree ast = Compiler.parser(tokens);
//        System.out.println("\nAST: \n" + ast);
//        System.out.println("-------------------------------------------------");
        // System.exit(100);


        das();
















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

    static void das() throws Exception {
        //all classes come here
        // to one


        //String code=" ";
        Tree jst = Compiler.parser(Compiler.tokenizer(code));

        new Tree.visitor() {

            static StringBuilder namespace = new StringBuilder("project");

            @Override
            public Object enter(Tree.Node node) {
                if (Compiler.AST_TYPE_CLASS.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    namespace.append('.').append(node.getStringData(Compiler.TREE_VALUE));
                    node.putData(Library.CLASS_ID, namespace.toString());
                }

                return null;
            }

            public Object exit(Tree.Node node) {
                if (Compiler.AST_TYPE_CLASS.equals(node.getStringData(Compiler.TREE_TYPE))) {


                    namespace.delete(namespace.lastIndexOf("."), namespace.length() - 1);
                }
                return null;
            }

            @Override
            public Object execute(Tree.Node node, ArrayList<Object> dataFromChildren) {

//                if (node.hasParent() && Compiler.AST_TYPE_FUNCTION.equals(node.parent.getStringData(Compiler.TREE_TYPE))) {
//                    return node.getData(Compiler.AST_VARIATION_DATA_TYPE);
//
//
//
//
//                }

                if (Compiler.AST_TYPE_FUNCTION.equals(node.getStringData(Compiler.TREE_TYPE))) {
                    if ((boolean) node.getData(Compiler.AST_FUNCTION_DEFINED)) {

                        if (node.getStringData(Compiler.TREE_VALUE).contains(".")) {

                            //todo java.jvm.compile
                            String[] objOriSet = node.getStringData(Compiler.TREE_VALUE).split("\\.");

                            node.putData(Compiler.OBJ_ORI_INVOKER, objOriSet[0]).putData(Compiler.OBJ_ORI_FUNC, objOriSet[objOriSet.length - 1]);


                        }


                    } else {

                        //game begins
                        StringBuilder sb = new StringBuilder(namespace.toString()).append('.').append(node.getStringData(Compiler.TREE_VALUE));

                        // no need consider func in func
                        for (Tree.Node dt : node.getFirstChild().children) {

                            if (dt != null) {
                                sb.append('_').append(dt.getData(Compiler.AST_VARIATION_DATA_TYPE));
                            }
                        }


                        node.putData(Library.FUNC_ID, sb.toString());
                    }


                }
                return null;
            }


        }.visit(jst);
        System.out.println(jst);
    }


}









