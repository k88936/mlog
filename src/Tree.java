import java.util.ArrayList;
import java.util.HashMap;

public class Tree {
    static final String CHILDREN = "children";
    Node root= new Node();


    Tree addNode(Node node){


        root.addChild(node);
        return this;
    }
    Node getNode(int index){
        return root.children.get(index);

    }
    int getSize(){
        return root.children.size();
    }
    Node newNode(){
        return new Node();

    }
    void putData(String name, Object object){
        root.putData(name, object);
    }
    void putData(String name1,Object object1,String name2,Object object2){
        root.putData(name1,object1,name2,object2);

    }

    public String toString(Node node ){



        visitor visitor = new visitor() {


            int level = 0;

            @Override
            public String doWithSelf(Node node,ArrayList<Object> dataFromChildren) {

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
                    sb.append(doWithSelf(child,null));
                }
                level--;
                 chs = new char[level*4];
                for (int i = 0; i < level*4; i++) {
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
        public void removeFromParent(){
            if (right != null) {
                this.right.left=this.left;
            }
            if (left!= null) {
                this.left.right=this.right;
            }


            parent.children.remove(this);

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
        public Node getFirstChild() {
            if (children.size() > 0) {
                return children.get(0);
            }
            return null;
        }
        Node getChild(int index) {
            return children.get(index);
        }
        public Node getRight () {
            return right;
        }
        public Node getLeft () {
            return left;
        }
        public Node getParent () {
            return parent;
        }

        public Node setParent(Node node) {

            if (parent != null) {
                this.removeFromParent();

                node.addChild(this);
            }

            return this;

        }
        public Node replacedBy(Node node) {

            node.removeFromParent();


            if (left != null) {
                node.left=this.left;
            }
            if (right!= null) {
                node.right=this.right;
            }


            parent.children.add(parent.children.indexOf(this),node);

            this.removeFromParent();

          //TODO

        //TODO make null function
            return null;
        }

        public boolean hasChildren(){
            return  children.size() > 0;
        }
        public boolean hasLeft(){
            return left!= null;
        }
        public boolean hasRight(){
            return right!= null;
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
            parent.children.add(node);
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
            return  children.size() == 1;
        }
        public Object getData(String key) {
            //if null then return a String of blank

            return  data.get(key);
        }
        public String getStringData(String key) {
            //char to string
            if (data.containsKey(key)) {
                return getData(key).toString();
            }
            else {
                return "\s";
            }


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
    static abstract class visitor {
        Object walk(Tree tree){
          return   walk(tree.root);

        }
        Object walk(Node node){
            ArrayList<Object> dataFromChildren = new ArrayList<>();
            int i = 0;
            Node child=node.getFirstChild();

            //&&(child.hasRight()||!child.hasLeft())
            while (child!=null){


                if (child.hasChildren()){
                    walk(child);
                }
                dataFromChildren.add(doWithChild(child,node));
               child = child.right;
            }

            return doWithSelf(node,dataFromChildren);
        }


//
        /*
        @return walk return
         */
        public abstract Object doWithSelf(Node self,ArrayList<Object> dataFromChildren);
        public abstract Object doWithChild(Node child,Node parent);


    }



}
