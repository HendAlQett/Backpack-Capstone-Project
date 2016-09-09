package com.hend.backpack.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = LandmarkProvider.AUTHORITY, database = LandmarkDatabase.class)
public class LandmarkProvider {

    public static final String AUTHORITY =
            "com.hend.backpack.data.LandmarkProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String LANDMARKS = "landmarks";

    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = LandmarkDatabase.LANDMARKS)
    public static class Landmarks {
        @ContentUri(
                path = Path.LANDMARKS,
                type = "vnd.android.cursor.dir/landmark",
                defaultSort = LandmarkColumns.LANDMARK_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.LANDMARKS);


        @InexactContentUri(
                name = "LANDMARK_ID",
                path = Path.LANDMARKS + "/#",
                type = "vnd.android.cursor.item/landmark",
                whereColumn = LandmarkColumns.LANDMARK_ID,
                pathSegment = 1)
        public static Uri withLandmarkId(long landmark_id) {
            return buildUri(Path.LANDMARKS, String.valueOf(landmark_id));
        }
    }


}
