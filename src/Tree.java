import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Tree {
    static final String CHILDREN = "children";
    Node root = new Node();
Tree() {
    root.parent = root;
    //maybe a crazy idea, but it has to
}

    Tree addNode(Node node) {


        this.root.addChild(node);
        return this;
    }

    Tree addNode(Tree subTree) {


        this.root.addChild(subTree.root);
        return this;
    }


    Node getNode(int index) {
        return this.root.children.get(index);

    }

    int getSize() {
        return this.root.children.size();
    }

    Node newNode() {
        return new Node();

    }

    Tree putData(String name, Object object) {
        this.root.putData(name, object);
        return this;
    }

    Tree putData(String name1, Object object1, String name2, Object object2) {
        this.root.putData(name1, object1, name2, object2);
        return this;
    }




    public String toString() {
        return this.root.toString();

       // return "";
    }

    public Node getNode(String name, String library) {
        return this.root.getChild(name, library);

    }


    static class Node implements Serializable {

        Node parent;

        Node left;
        Node right;

        Node jump=null;

        HashMap<String, Object> data = new HashMap();
        ArrayList<Node> children = new ArrayList<>();

        public Node() {

            this.data.put(CHILDREN, this.children);

        }

        public Node(Node parent) {
            this.setParent(parent);
            this.data.put(CHILDREN, this.children);
        }

        public Node(String key1, Object value1) {
            this.data.put(CHILDREN, this.children);
            this.putData(key1, value1);

        }

        public Node(String key1, Object value1, String key2, Object value2) {
            this.data.put(CHILDREN, this.children);
            this.putData(key1, value1, key2, value2);

        }

        public Node(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
            this.data.put(CHILDREN, this.children);
            this.putData(key1, value1, key2, value2, key3, value3);

        }

        public Node(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
            this.data.put(CHILDREN, this.children);
            this.putData(key1, value1, key2, value2, key3, value3, key4, value4);

        }

        public Node removeFromParent() {
            if (this.parent == null) {
                return null;
            }
            if (this.right != null) {
                this.right.left = this.left;
            }
            if (this.left != null) {
                this.left.right = this.right;
            }


            this.parent.children.remove(this);

            this.parent = null;
            this.left = null;
            this.right = null;
            return this;

        }


        public Node addChild(Node node) {

            if (node == null) {
                return this;
            }


            node.left = this.getLastChild();
            if (this.getLastChild() != null) {
                this.getLastChild().right = node;
            }

            this.children.add(node);
            node.parent = this;

            return this;
        }

        Node getLastChild() {
            if (this.children.size() > 0) {
                return this.children.get(this.children.size() - 1);
            }
            return null;
        }

        public Node getFirstChild() {
            if (this.children.size() > 0) {
                return this.children.get(0);
            }
            return null;
        }

        Node getChild(int index) {
            return this.children.get(index);
        }
        Node getChild(String key, Object value){
            for (Node node : this.children) {
                if (node.data.containsKey(key) && node.data.get(key).equals(value)) {
                    return node;
                }
            }
            return null;
        }

        public Node getRight() {
            return this.right;
        }

        public Node getLeft() {
            return this.left;
        }

        public Node getParent() {
            return this.parent;
        }

        public Node setParent(Node node) {

            if (this.parent != null) {
                this.removeFromParent();

                node.addChild(this);
            }

            return this;

        }
        public Node setJump(Node node ){
            this .jump = node;
            return this;
        }

        // pull inside out is fully ok
        //exactly copyFrom
        // it will cause lost some node if input right
        public Node replacedBy(Node node) {










            if(node==this.right){
                this.jump=this;
            }

            node.removeFromParent();
            node = node.cutAndCopyTo();
            this.data = node.data;
            this.children = node.children;


//            node.removeFromParent();
//
//
//            if (left != null) {
//                node.left=this.left;
//            }
//            if (right!= null) {
//                node.right=this.right;
//            }
//
//
//            parent.children.add(parent.children.indexOf(this),node);
//
//            this.removeFromParent();


            //TODO

            //TODO make null function

            return null;
        }

        public boolean hasChildren() {
            return this.children.size() > 0;
        }
        public boolean hasParent() {
            return this.parent != null;
        }

        public boolean hasLeft() {
            return this.left != null;
        }

        public boolean hasRight() {
            return this.right != null;
        }

        //insert a node before it
        public Node insertBefore(Node node) {
            if (node == null) {
                return this;
            }
            if (this.left != null) {
                this.left.right = node;
                node.left = this.left;
            }
            this.left = node;
            node.right = this;
            this.parent.children.add(node);
            node.parent = this.parent;
            return this;
        }

        //insert a node after it
        public Node insertAfter(Node node) {
            if (node == null) {
                return this;
            }
            if (this.right != null) {
                this.right.left = node;
                node.right = this.right;
            }
            this.right = node;
            node.left = this;
            return this;
        }

        /**
         */
       public String toString() {

           return (String) new visitor() {

               static int level = 0;

               String generateBlank(){
                   char[] chs = new char[level * 4];
                   for (int i = 0; i < level * 4; i++) {
                       chs[i] = '-';
                   }

                   return new String(chs);
               }

               @Override
               public Object execute(Node node, ArrayList<Object> dataFromChildren) {


                   StringBuilder sb = new StringBuilder();


                   sb.append("\r").append(this.generateBlank());
                   for (String key : node.data.keySet()) {

                       if (Objects.equals(key, CHILDREN)) continue;
                       if (Objects.equals(key, Library.ARGS)) continue;
                       if (Objects.equals(key, Library.RETURNS)) continue;
                       sb.append(key);
                       sb.append(":");

                       if (Objects.equals(key, Library.NATIVE_CODE)) {
                           sb.append("\n\n");
                       }
                       if (Objects.equals(key, Compiler.AST_FUNCTION_CONTENT)) {


                           //sb.append("content");
                           sb.append("\n\n@CONTENT_BEGIN\n\n");
                           sb.append(node.data.get(key).toString());
                           sb.append("\n@CONTENT_OVER");

                       } else {
                           sb.append(node.data.get(key).toString());

                       }

                       if (Objects.equals(key, Library.NATIVE_CODE)) {
                           sb.append("\n\n");
                       } else {
                           sb.append(';');
                           sb.append("\n");
                       }

                       sb.append(this.generateBlank());


                   }
                   sb.append("-{" + "\n");
                   for (Object childData :
                           dataFromChildren) {
                       sb.append(childData);
                   }
                   sb.append(this.generateBlank()).append("-}").append("\n");
                   return sb.toString();
               }

               @Override
               public Object enter(Node startPoint) {
                   level++;
                   return null;
               }

               @Override
               public Object exit(Node startPoint) {
                   level--;
                   return null;
               }
           }.visit(this);


        }

        Node copyTo() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(this);
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bis);
                return (Node) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        Node cutAndCopyTo() {

            Node newNode = this.copyTo();
            newNode.parent = null;
            newNode.left = null;
            newNode.right = null;
            return newNode;
        }

        public boolean singleChild() {
            return this.children.size() == 1;
        }

        public Object getData(String key) {
            //if null then return a String of blank

            return this.data.get(key);
        }

        public String getStringData(String key) {
            //char to string
            if (this.data.containsKey(key)) {
                return this.getData(key).toString();
            } else {
                return "\s";
            }


        }

        Node putData(String key, Object value) {
            this.data.put(key, value);

            return this;
        }

        Node putData(String key1, Object value1, String key2, Object value2) {
            this.data.put(key1, value1);
            this.data.put(key2, value2);

            return this;
        }

        Node putData(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
            this.data.put(key1, value1);
            this.data.put(key2, value2);
            this.data.put(key3, value3);
            return this;
        }

        Node putData(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
            this.data.put(key1, value1);
            this.data.put(key2, value2);
            this.data.put(key3, value3);
            this.data.put(key4, value4);

            return this;
        }


    }

    static abstract class visitor {

        public int index = 0;
        int totalIndex = 0;
        static Node currentNode;
        Object visit(Tree tree) {
            return this.visit(tree.root);

        }

        Object visit(Node node) {

            int localIndex=index;
            totalIndex++;
            index++;
            currentNode=node;
            ArrayList<Object> dataFromChildren = new ArrayList<>();
            this.enter(node);

            Node child = node.getFirstChild();
            while (child != null) {

                dataFromChildren.add(this.visit(child));
                Node jump = child.jump;
                if (jump != null) {
                    child.setJump(null);
                    child=jump;
                }else {
                    child = child.right;
                }


            }
            index=localIndex;
            this.exit(node);
            return this.execute(node, dataFromChildren);
        }


        public abstract Object execute(Node node, ArrayList<Object> dataFromChildren);

        public Object enter(Node parent) {

            return null;
        }

        public Object exit(Node parent) {
            return null;
        }

        public boolean isRoot() {

            return currentNode.hasParent();
        }
        //
    }
}
