package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TruckDimensions {

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

    @SerializedName("Height")
    @Expose
    String Height;

    @SerializedName("Weight")
    @Expose
    String Weight;

    @SerializedName("length")
    @Expose
    String length;

    @SerializedName("width")
    @Expose
    String width;
}
