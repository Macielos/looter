package pl.looter.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

import lombok.Data;

@Entity
@Data
public class User {

    @Id
    Long id;

    @Index
    private String name;

    @Index
    private String registrationId;

    private Date registrationTime;

    private Date lastLogin;

    private int level;

    private long xp;

    public User(String registrationId) {
        this(null, registrationId);
    }

    public User(String name, String registrationId) {
        this.name = name;
        this.registrationId = registrationId;
        this.registrationTime = new Date();
        this.level = 1;
        this.xp = 0;
    }
}