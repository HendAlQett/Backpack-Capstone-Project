package com.hend.backpack.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by hend on 8/14/16.
 */
@Database(version = LandmarkDatabase.VERSION)
public class LandmarkDatabase {

    private LandmarkDatabase() {
    }

    public static final int VERSION = 1;

    @Table(LandmarkColumns.class)
    public static final String LANDMARKS = "landmarks";
}
