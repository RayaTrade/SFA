package Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TruckType {

    @SerializedName("ID")
    @Expose
    String ID;
    @SerializedName("Type")
    @Expose
    String Type;
    @SerializedName("Height")
    @Expose
    String Height;
    @SerializedName("Weight")
    @Expose
    String Weight;
    @SerializedName("length")
    @Expose
    String length ;
    @SerializedName("width")
    @Expose
    String width  ;

    int count = 0;
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }



    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }


    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    double size = 0.0;
    public void setProSize(TruckType car)
    {
        car.size = Double.parseDouble(car.Height)  *Double.parseDouble(car.width) *Double.parseDouble(car.length);
    }


}
