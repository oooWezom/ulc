package com.wezom.ulcv2.mvp.model;

import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wezom.ulcv2.net.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.01.2016.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
@Table(name = "Persons")
@NoArgsConstructor
public class Person extends TruncatableModel {

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, name = "serverId")
    private int mServerId;
    @Column(name = "name")
    private String mName;
    @Column(name = "status")
    private int mStatus;
    @Column(name = "level")
    private int mLevel;
    @Column(name = "avatar")
    private String mAvatar;

    @Nullable
    public static Person getByServerId(int id) {
        return new Select().from(Person.class).where("serverId = ?", id).executeSingle();
    }

    public static Person createOrUpdatePerson(User sender) {
        Person person = getByServerId(sender.getId());
        if (person == null) {
            person = new Person();
        }

        person.setServerId(sender.getId());
        person.setName(sender.getName());
        person.setStatus(sender.getStatus().getId());
        person.setAvatar(sender.getAvatar());
        person.setLevel(sender.getLevel());
        person.save();

        return person;
    }
}
