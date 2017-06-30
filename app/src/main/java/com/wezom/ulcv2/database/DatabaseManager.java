package com.wezom.ulcv2.database;


import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.model.Person;
import com.wezom.ulcv2.net.ApiManager;

/**
 * Created: Zorin A.
 * Date: 24.05.2016.
 */
public class DatabaseManager {
    private ApiManager mApiManager;

    public DatabaseManager(ApiManager apiManager) {
        mApiManager = apiManager;
    }

    public void clearDatabase() {
        Message.truncate(Message.class);
        Dialog.truncate(Dialog.class);
        Person.truncate(Person.class);
        Language.truncate(Language.class);
    }

    public void clearCategories() {
        Category.truncate(Category.class);
    }
}
