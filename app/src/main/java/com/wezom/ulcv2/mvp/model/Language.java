package com.wezom.ulcv2.mvp.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.wezom.ulcv2.net.models.responses.LanguageResponse;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 04.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(prefix = "m")
@Table(name = "Languages")
public class Language extends TruncatableModel {

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, name = "serverId")
    private int mServerId;
    @Column(name = "name")
    private String mName;
    @Column(name = "checked")
    private boolean mCheckStatus;


    public static Language getOrCreateByServerId(int serverId) {
        Language model = new Select().from(Language.class).where("serverId = ?", serverId).executeSingle();

        if (model == null) {
            model = new Language();
        }

        return model;
    }

    public static ArrayList<Language> getLanguages() {
        return new ArrayList<>(new Select().from(Language.class).execute());
    }

    public static void updateLanguages(LanguageResponse languageResponse) {
        ActiveAndroid.beginTransaction();
        try {
            for (LanguageResponse.Language language : languageResponse.getResponse()) {
                Language localLanguage = getOrCreateByServerId(language.getId());
                localLanguage.setServerId(language.getId());
                localLanguage.setName(language.getDisplayName());
                localLanguage.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (RuntimeException e) {

        }
        ActiveAndroid.endTransaction();
    }

    public static void updateSelectedLanguages(List<Language> newLang) {

        new Delete().from(Language.class).executeSingle();
        try {
            ActiveAndroid.beginTransaction();
            for (Language language : newLang) {
                Language l = new Language();
                l.setServerId(language.getServerId());
                l.setName(language.getName());
                l.setCheckStatus(l.isCheckStatus());
                l.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (RuntimeException e) {

        }
        ActiveAndroid.endTransaction();

    }
}
