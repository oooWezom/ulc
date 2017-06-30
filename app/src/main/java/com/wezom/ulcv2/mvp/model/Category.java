package com.wezom.ulcv2.mvp.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 05.07.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(prefix = "m")
@Table(name = "Categores")
public class Category extends TruncatableModel {

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, name = "serverId")
    private int mCategoryId;
    @Column(name = "name")
    private String mName;
    @Column(name = "nameRu")
    private String mNameRu;
    @Column(name = "icon")
    private String mIcon;

    private boolean mIsChecked;

    public static Category getOrCreateByServerId(int serverId) {
        Category model = new Select().from(Category.class).where("serverId = ?", serverId).executeSingle();

        if (model == null) {
            model = new Category();
        }

        return model;
    }

    public static ArrayList<Category> getCategories() {
        return new ArrayList<>(new Select().from(Category.class).execute());
    }

    public static Category getCategoryById(int id) {
        return new Select().from(Category.class).where("serverId = ?", id).executeSingle();
    }

    public static void updateCategories(ArrayList<com.wezom.ulcv2.net.models.Category> response) {
        ActiveAndroid.beginTransaction();
        try {
            for (com.wezom.ulcv2.net.models.Category category : response) {

                Category tempCategory = getOrCreateByServerId(category.getId());
                tempCategory.setCategoryId(category.getId());
                tempCategory.setName(category.getName());
                tempCategory.setNameRu(category.getNameRu());
                tempCategory.setIcon(category.getIcon());
                tempCategory.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}