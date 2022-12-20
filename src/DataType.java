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
        }else return this.name.equals(other.name);

    }
    //terrible fix
    public static boolean isInstanceOf(DataType local, DataType target) {

        if (OBJECT.equals(target)) {
            return true;
        }
        else {
            if (local.equals(OBJECT) ) {
                return false;
            }else {
               return isInstanceOf(local.father, target);
            }

        }




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



        }.visit(this.node);


//
        return builder.toString();


    }

    public String toString() {
        //"TYPE: " +
        return  this.name;
    }

    private DataType setFather(DataType father) {
        this.father = father;
        father.node.addChild(this.node);
        return this;
    }

    public boolean isInstanceOf(Object target) {

        if (target != null) {
            return isInstanceOf(this, (DataType) target);
        }else return this.equals(OBJECT);

    }
}
