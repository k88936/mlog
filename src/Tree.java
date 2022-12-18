import java.util.ArrayList;
import java.util.HashMap;

public class Tree {
    static final String CHILDREN = "children";
    Node root= new Node();


    Tree addNode(Node node){


        this.root.addChild(node);
        return this;
    }
    Node getNode(int index){
        return this.root.children.get(index);

    }
    int getSize(){
        return this.root.children.size();
    }
    Node newNode(){
        return new Node();

    }
    void putData(String name, Object object){
        this.root.putData(name, object);
    }
    void putData(String name1,Object object1,String name2,Object object2){
        this.root.putData(name1,object1,name2,object2);

    }

    public String toString(Node node ){



        visitor visitor = new visitor() {


            int level = 0;

            @Override
            public String doWithSelf(Node node,ArrayList<Object> dataFromChildren) {

                this.level++;
                StringBuilder sb = new StringBuilder();
                char[] chs = new char[this.level *4];
                for (int i = 0; i < this.level *4; i++) {
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
                    sb.append(this.doWithSelf(child,null));
                }
                this.level--;
                 chs = new char[this.level *4];
                for (int i = 0; i < this.level *4; i++) {
                    chs[i] = '-';
                }
                sb.append(new String(chs)+ "-}" +"\n");
                return sb.toString();
            }

            @Override
            public Object doWithChild(Node node,Node parent) {
                return null;
            }
        };
        return (String) visitor.doWithSelf(node,null);
    }
    public String toString() {

        return this.toString(new Node().addChild(this.root));
//return this.toString(root);
    }


    static class Node {

        Node parent;

        Node left;
        Node right;



        HashMap<String, Object> data = new HashMap();
        ArrayList<Node> children = new ArrayList<Node>();

        public Node() {

            this.data.put(CHILDREN, this.children);

        }
        public Node(Node parent) {
            this.setParent(parent);
            this.data.put(CHILDREN, this.children);
        }
        public Node(String key1,Object value1)
        {
            this.data.put(CHILDREN, this.children);
            this.putData(key1,value1);

        }
        public Node(String key1,Object value1,String key2,Object value2)
        {
            this.data.put(CHILDREN, this.children);
            this.putData(key1,value1,key2,value2);

        }
        public Node(String key1,Object value1,String key2,Object value2,String key3,Object value3){
            this.data.put(CHILDREN, this.children);
            this.putData(key1,value1,key2,value2,key3,value3);

        }
        public Node(String key1,Object value1,String key2,Object value2,String key3,Object value3,String key4,Object value4){
            this.data.put(CHILDREN, this.children);
            this.putData(key1,value1,key2,value2,key3,value3,key4,value4);

        }
        public void removeFromParent(){
            if (this.parent == null) {
                return;
            }
            if (this.right != null) {
                this.right.left=this.left;
            }
            if (this.left != null) {
                this.left.right=this.right;
            }


            this.parent.children.remove(this);

            this.parent = null;
            this.left=null;
            this.right=null;
        }



        public Node addChild(Node node) {

            if (node == null) {
                return this;
            }



            node.left=this.getLastChild();
            if (this.getLastChild() != null) {
                this.getLastChild().right=node;
            }

            this.children.add(node);
            node.parent = this;

            return this;
        }
        Node  getLastChild() {
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
        public Node getRight () {
            return this.right;
        }
        public Node getLeft () {
            return this.left;
        }
        public Node getParent () {
            return this.parent;
        }

        public Node setParent(Node node) {

            if (this.parent != null) {
                this.removeFromParent();

                node.addChild(this);
            }

            return this;

        }
        //exactly copyFrom
        public Node replacedBy(Node node) {
            this.data=node.data;
            this.children=node.children;

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

        public boolean hasChildren(){
            return this.children.size() > 0;
        }
        public boolean hasLeft(){
            return this.left != null;
        }
        public boolean hasRight(){
            return this.right != null;
        }
        //insert a node before it
        public Node insertBefore(Node node) {
            if (node == null) {
                return this;
            }
            if (this.left != null) {
                this.left.right=node;
                node.left=this.left;
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
            if (this.right!= null) {
                this.right.left=node;
                node.right=this.right;
            }
            this.right = node;
            node.left = this;
            return this;
        }

        public boolean singleChild(){
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
            }
            else {
                return "\s";
            }


        }
        Node putData(String key, Object value) {
            this.data.put(key,value);

            return this;
        }
        Node putData(String key1, Object value1, String key2, Object value2) {
            this.data.put(key1,value1);
            this.data.put(key2,value2);

            return this;
        }
        Node putData(String key1, Object value1, String key2, Object value2, String key3, Object value3){
            this.data.put(key1,value1);
            this.data.put(key2,value2);
            this.data.put(key3,value3);
            return this;
        }
        Node putData(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4){
            this.data.put(key1,value1);
            this.data.put(key2,value2);
            this.data.put(key3,value3);
            this.data.put(key4,value4);

            return this;
        }


    }
    static abstract class visitor {
        Object visit(Tree tree){
          return this.visit(tree.root);

        }
        Object visit(Node node){
            ArrayList<Object> dataFromChildren = new ArrayList<>();
            int i = 0;
            Node child=node.getFirstChild();

            //&&(child.hasRight()||!child.hasLeft())
            while (child!=null){


                if (child.hasChildren()){
                    this.visit(child);
                }
                dataFromChildren.add(this.doWithChild(child,node));

               child = child.right;
            }

            return this.doWithSelf(node,dataFromChildren);
        }


//
        /*

        @ execute doWithSelf only when hasChildren
        @return walk return
         */
        public abstract Object doWithSelf(Node self,ArrayList<Object> dataFromChildren);
        public abstract Object doWithChild(Node child,Node parent);


    }



}
