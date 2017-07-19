package pl.sportdata.beestro.entities.users;

import java.io.Serializable;

public class User implements Serializable {

    public final int id;
    public final String name;
    public final String password;
    public final Permission permissions;
    public final String patternSha1;

    public User(int id, String name, String password, Permission permissions, String patternSha1) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.permissions = permissions;
        this.patternSha1 = patternSha1;
    }
}
