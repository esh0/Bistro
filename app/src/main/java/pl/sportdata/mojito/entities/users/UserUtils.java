package pl.sportdata.mojito.entities.users;

import android.support.annotation.Nullable;

import java.util.List;

public class UserUtils {

    @Nullable
    public static User findUserWithId(int id, @Nullable List<User> users) {
        User user = null;
        if (users != null) {
            for (User existingUser : users) {
                if (existingUser != null && existingUser.id == id) {
                    user = existingUser;
                    break;
                }
            }
        }

        return user;
    }
}
