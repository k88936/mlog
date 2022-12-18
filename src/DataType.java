public class DataType {
    static final DataType OBJECT = new DataType("Obj");
    public final String name;
    DataType father=OBJECT;
    public static DataType createDataType(String name){

        return new DataType(name).setFather(OBJECT);
    }
    private DataType(String name) {
        this.name = name;
    }
    /*

    include equal situations
     */
    public static boolean isInstanceOf(DataType local,DataType target){

        if (local.equals(target)){
            return true;
        }else if (local.father!=OBJECT)
        {
            return isInstanceOf(local.father, target);
        }


        return false;

    }
    private DataType setFather(DataType father){
        this.father=father;
        return this;
    }
    public static DataType createDataType(String name,DataType father){
        return new DataType(name).setFather(father);
    }
    public boolean isInstanceOf(DataType target){
        return isInstanceOf(this,target);
    }
}
