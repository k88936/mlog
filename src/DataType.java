import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DataType implements Serializable {
    static final HashMap<String, DataType> MAP= new HashMap();
    static final Tree DataTypes = new Tree();
    public final String name;
    Tree.Node node = new Tree.Node();
    static final DataType OBJECT = new DataType("obj");
    DataType father ;


    static {
        DataTypes.root = OBJECT.node;
    }
    private DataType(String name) {

        this.name = name;
        DataType.MAP.put(name, this);
        this.node.putData("name", name);
    }

    public static String getAllDataTypesNames() {
        return OBJECT.getAllChildrenNames() + OBJECT.name;

    }

    public static DataType createDataType(String name) {

        return new DataType(name).setFather(OBJECT);
    }

    static DataType getDataType(String name) {
        return MAP.get(name);
    }

    /**
     * include equal situations
     */
    public boolean equals(DataType other) {
        if (other == null) {
            return false;
        }else if(this.name.equals(other.name)){
            return true;
        }else {
            return false;
        }

    }
    public static boolean isInstanceOf(DataType local, DataType target) {

        if (local.equals(target)) {
            return true;
        } else if (local.father != OBJECT) {
            return isInstanceOf(local.father, target);
        }


        return false;

    }

    public static DataType createDataType(String name, DataType father) {
        return new DataType(name).setFather(father);
    }

    public String getAllChildrenNames() {

        StringBuilder builder = new StringBuilder();
        new Tree.visitor() {

            @Override
            public Object execute(Tree.Node child, ArrayList<Object> dataFromChildren) {
                builder.append(child.getStringData("name"));
                builder.append('|');
                return null;
            }

            @Override
            public Object enter(Tree.Node startPoint) {
                return null;
            }

            @Override
            public Object exit(Tree.Node startPoint) {
                return null;
            }


        }.visit(this.node);


//
        return builder.toString();


    }

    public String toString() {
        return "TYPE: " + this.name;
    }

    private DataType setFather(DataType father) {
        this.father = father;
        father.node.addChild(this.node);
        return this;
    }

    public boolean isInstanceOf(DataType target) {
        return isInstanceOf(this, target);
    }
}