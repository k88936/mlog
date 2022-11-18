import java.util.ArrayList;
import java.util.HashMap;

public class tree {
    static final String CHILDREN = "children";
    Node root= new Node();


    tree addNode(Node node){


        root.addChild(node);
        return this;
    }
    Node newNode(){
        return new Node();

    }
    public String toString(Node node ){



        visitor visitor = new visitor() {


            int level = 0;

            @Override
            public String visit(Node node) {
                level++;
                StringBuilder sb = new StringBuilder();
                for (Node child : node.children) {



                    sb.append("\n");

                    {
                        char[] chs = new char[level];
                        for (int i = 0; i < level; i++) {
                            chs[i] = ' ';
                        }
                        sb.append(new String(chs));
                    }

                    //sb.append(' ');

                    for (String key:child.data.keySet()) {

                        if (key==CHILDREN)continue;

                        sb.append(key);
                        sb.append(":");
                        sb.append(child.data.get(key));
                        sb.append(';');


                    }

                    sb.append(visit(child));
                }
                level--;
                return sb.toString();
            }
        };
        return visitor.visit(node);
    }
    public String toString() {
        return this.toString(root);

    }
    static class Node {

        Node parent;

        Node left;
        Node right;



        HashMap<String, Object> data = new HashMap();
        ArrayList<Node> children = new ArrayList<Node>();

        public Node() {

            data.put(CHILDREN,children);

        }
        public Node(Node parent) {
            setParent(parent);
            data.put(CHILDREN,children);
        }
        public Node(String key1,Object value1)
        {
            data.put(CHILDREN,children);
            putData(key1,value1);

        }
        public Node(String key1,Object value1,String key2,Object value2)
        {
            data.put(CHILDREN,children);
            putData(key1,value1,key2,value2);

        }
        public Node(String key1,Object value1,String key2,Object value2,String key3,Object value3){
            data.put(CHILDREN,children);
            putData(key1,value1,key2,value2,key3,value3);

        }
        public Node(String key1,Object value1,String key2,Object value2,String key3,Object value3,String key4,Object value4){
            data.put(CHILDREN,children);
            putData(key1,value1,key2,value2,key3,value3,key4,value4);

        }

        public Node addChild(Node node) {



            node.left=this.getLastChild();
            if (this.getLastChild() != null) {
                this.getLastChild().right=node;
            }

            children.add(node);
            node.parent = this;

            return this;
        }
        Node  getLastChild() {
            if (children.size() > 0) {
                return children.get(children.size() - 1);
            }
            return null;
        }

        public Node setParent(Node node) {

            parent.addChild(this);
            return this;

        }

        public String getStringData(String key) {
            return (String) data.get(key);
        }
        void putData(String key, Object value) {
            data.put(key,value);

        }
        void putData(String key1, Object value1,String key2, Object value2) {
            data.put(key1,value1);
            data.put(key2,value2);


        }
        void putData(String key1, Object value1,String key2, Object value2,String key3,Object value3){
            data.put(key1,value1);
            data.put(key2,value2);
            data.put(key3,value3);

        }
        void putData(String key1, Object value1,String key2, Object value2,String key3,Object value3,String key4,Object value4){
            data.put(key1,value1);
            data.put(key2,value2);
            data.put(key3,value3);
            data.put(key4,value4);


        }
    }
    abstract class visitor {



        public abstract String visit(Node node);
    }

}
