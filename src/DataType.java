public class DataType {
    static final DataType OBJECT = new DataType();
    DataType father=OBJECT;
    public static DataType createDataType(){
        return new DataType().setFather(OBJECT);
    }
    private DataType() {
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
    public static DataType createDataType(DataType father){
        return new DataType().setFather(father);
    }
    public boolean isInstanceOf(DataType target){
        return isInstanceOf(this,target);
    }
}
