package pl.sportdata.mojito.entities.parameters;

import com.google.gson.annotations.SerializedName;

public class Parameters {

    @SerializedName("gastro_day")
    public final String gastroDay; //08/01/2008
    @SerializedName("sale_point_id")
    public final int salePointId; //42022
    @SerializedName("device_id")
    public final int devideId; //2017-02-22 11:53:51
    @SerializedName("challenge") //12345
    public final int challengeCode;

    public Parameters(String gastroDay, int salePointId, int devideId, int challengeCode) {
        this.gastroDay = gastroDay;
        this.salePointId = salePointId;
        this.devideId = devideId;
        this.challengeCode = challengeCode;
    }
}
