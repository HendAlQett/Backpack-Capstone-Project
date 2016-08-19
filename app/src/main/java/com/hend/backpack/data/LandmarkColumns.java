package com.hend.backpack.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by hend on 8/14/16.
 */
public interface LandmarkColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String LANDMARK_ID = "landmark_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String LANDMARK_NAME_EN = "name_en";
    @DataType(DataType.Type.TEXT)
    @NotNull
    String LANDMARK_NAME_AR = "name_ar";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String LANDMARK_DESCRIPTION_EN = "description_en";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String LANDMARK_DESCRIPTION_AR = "description_ar";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String LANDMARK_IMAGE_URL = "image_url";

    @DataType(DataType.Type.REAL)
    @NotNull
    String LATITUDE = "latitude";
    @DataType(DataType.Type.REAL)
    @NotNull
    String LONGITUDE = "longitude";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String LANDMARK_RADIUS = "landmark_radius";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String FLAG_STREET_VIEW = "street_view";
}
