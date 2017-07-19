package pl.sportdata.beestro.entities.configuration;

import com.google.gson.annotations.SerializedName;

public class Configuration {

    @SerializedName("gastro_day")
    public final String gastroDay; //08/01/2008
    @SerializedName("challenge_code")
    public final int challengeCode; //42022
    @SerializedName("local_time")
    public final String localTime; //2017-02-22 11:53:51

    public Configuration(String gastroDay, int challengeCode, String localTime) {
        this.gastroDay = gastroDay;
        this.challengeCode = challengeCode;
        this.localTime = localTime;
    }
}
