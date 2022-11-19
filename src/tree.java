import java.util.ArrayList;
import java.util.HashMap;

public class tree {
    static final String CHILDREN = "children";
    Node root= new Node();


    tree addNode(Node node){


        root.addChild(node);
        return this;
    }
    Node getNode(int index){
        return root.children.get(index);

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
                char[] chs = new char[level*4];
                for (int i = 0; i < level*4; i++) {
                    chs[i] = '-';
                }





                sb.append("-{" +"\n");

                for (Node child : node.children) {



                   // sb.append("\n");



                       sb.append(new String(chs));


                    //sb.append(' ');

                    for (String key:child.data.keySet()) {

                        if (key==CHILDREN)continue;

                        sb.append(key);
                        sb.append(":");
                        sb.append(child.data.get(key));
                        sb.append(';');
                        sb.append("\n");
                        sb.append(new String(chs));

                    }
                   // sb.append("\n");
                    sb.append(visit(child));
                }
                level--;
                 chs = new char[level*4];
                for (int i = 0; i < level*4; i++) {
                    chs[i] = '-';
                }
                sb.append(new String(chs)+ "-}" +"\n");
                return sb.toString();
            }
        };
        return visitor.visit(node);
    }
    public String toString() {

        return this.toString(new Node().addChild(root));
//return this.toString(root);
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
            if (parent != null) {
                parent.addChild(this);
            }

            return this;

        }

        public String getStringData(String key) {
            return (String) data.get(key);
        }
        Node putData(String key, Object value) {
            data.put(key,value);

            return this;
        }
        Node putData(String key1, Object value1, String key2, Object value2) {
            data.put(key1,value1);
            data.put(key2,value2);

            return this;
        }
        Node putData(String key1, Object value1, String key2, Object value2, String key3, Object value3){
            data.put(key1,value1);
            data.put(key2,value2);
            data.put(key3,value3);
            return this;
        }
        Node putData(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4){
            data.put(key1,value1);
            data.put(key2,value2);
            data.put(key3,value3);
            data.put(key4,value4);

            return this;
        }


    }
    abstract class visitor {



        public abstract String visit(Node node);
    }

}
