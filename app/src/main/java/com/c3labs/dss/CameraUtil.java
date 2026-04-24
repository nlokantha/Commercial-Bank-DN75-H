package com.c3labs.dss;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.Environment;
import android.telephony.mbms.FileInfo;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@SuppressLint("NewApi")
public class CameraUtil {


    private static int store_file_prefix = 1000;
    private static String store_file_suffix = "CRASH";
    private static String separation = "_";


    private static final String TAG = "CameraUtil——ccxbg";
    public ArrayList<FileInfo> folder_array;

    static enum RecordState {

        NONE(0), RECORD(1), REPEAT_RECORD(2);

        protected int value = 0;

        private RecordState(int value) {
            this.value = value;
        }

        public static RecordState valueOf(int value) {
            switch (value) {
                case 0:
                    return RecordState.NONE;
                case 1:
                    return RecordState.RECORD;
                case 2:
                    return RecordState.REPEAT_RECORD;
                default:
                    return null;
            }
        }

        public int value() {
            return this.value;
        }
    }

    public static class ServiceToken {
        ContextWrapper mWrappedContext;

        ServiceToken(ContextWrapper context) {
            mWrappedContext = context;
        }
    }


    public static String createStoreDirectory() {
        File mediaStorageDir = getMediaDir();
        if (getMediaDir() == null) return null;
        List<String> listStore = new ArrayList<String>();
        File[] listFiles = mediaStorageDir.listFiles();
        if (listFiles.length == 0)
            return mediaStorageDir.getAbsolutePath() + "/" + store_file_prefix + separation + store_file_suffix;
        for (File file : listFiles) {
            if (file.isDirectory())
                listStore.add(file.getName());
        }
        Collections.sort(listStore, new Comparator<String>() {
            @Override
            public int compare(String name1, String name2) {
                return name1.compareToIgnoreCase(name2);
            }
        });


        if (listStore.size() == 0)
            return mediaStorageDir.getAbsolutePath() + "/" + store_file_prefix + separation + store_file_suffix;
        String storeDirName = listStore.get(listStore.size() - 1);
        String[] split = storeDirName.split(separation);
        Integer storefile_prefix = Integer.valueOf(split[0]);
        storefile_prefix++;
        return mediaStorageDir.getAbsolutePath() + "/" + storefile_prefix + separation + store_file_suffix;

    }

    public static File getMediaDir() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failedto create directory");
                return null;
            }
        }
        return mediaStorageDir;
    }


    private static ArrayList<Integer> getSupportedVideoQuality(int cameraId) {
        ArrayList<Integer> supported = new ArrayList<Integer>();
        // Check for supported quality
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_1080P)) {
            supported.add(CamcorderProfile.QUALITY_1080P);
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_720P)) {
            supported.add(CamcorderProfile.QUALITY_720P);
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P)) {
            supported.add(CamcorderProfile.QUALITY_480P);
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_HIGH)) {
            supported.add(CamcorderProfile.QUALITY_HIGH);
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW)) {
            supported.add(CamcorderProfile.QUALITY_LOW);
        }
        return supported;
    }

    public static int getSupportedHighestVideoQuality(int cameraId,
                                                      int defaultQuality) {
        // When launching the camera app first time, we will set the video quality
        // to the first one (i.e. highest quality) in the supported list
        List<Integer> supported = getSupportedVideoQuality(cameraId);
        if (supported == null || 0 == supported.size()) {
            //Log.e(TAG, "No supported video quality is found");
            return defaultQuality;
        }
        return supported.get(0);
    }

    public static Size getOptimalPreviewSize(DisplayMetrics dm,
                                             List<Size> sizes, double targetRatio) {

        Point[] points = new Point[sizes.size()];

        int index = 0;
        for (Size s : sizes) {
            points[index++] = new Point(s.width, s.height);
        }

        int optimalPickIndex = getOptimalPreviewSize(dm, points, targetRatio);
        return (optimalPickIndex == -1) ? null : sizes.get(optimalPickIndex);
    }

    public static int getOptimalPreviewSize(DisplayMetrics dm,
                                            Point[] sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.01;
        if (sizes == null) return -1;

        int optimalSizeIndex = -1;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of preview surface. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size.
        Point point = new Point(dm.widthPixels, dm.heightPixels);
        int targetHeight = Math.min(point.x, point.y);
        // Try to find an size match aspect ratio and size
        for (int i = 0; i < sizes.length; i++) {
            Point size = sizes[i];
            double ratio = (double) size.x / size.y;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.y - targetHeight) < minDiff) {
                optimalSizeIndex = i;
                minDiff = Math.abs(size.y - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSizeIndex == -1) {
            Log.w(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (int i = 0; i < sizes.length; i++) {
                Point size = sizes[i];
                if (Math.abs(size.y - targetHeight) < minDiff) {
                    optimalSizeIndex = i;
                    minDiff = Math.abs(size.y - targetHeight);
                }
            }
        }
        return optimalSizeIndex;
    }

    // Returns the largest picture size which matches the given aspect ratio.
    public static Size getOptimalVideoSnapshotPictureSize(
            List<Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null) return null;

        Size optimalSize = null;

        // Try to find a size matches aspect ratio and has the largest width
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (optimalSize == null || size.width > optimalSize.width) {
                optimalSize = size;
            }
        }

        // Cannot find one that matches the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            Log.w(TAG, "No picture size match the aspect ratio");
            for (Size size : sizes) {
                if (optimalSize == null || size.width > optimalSize.width) {
                    optimalSize = size;
                }
            }
        }
        return optimalSize;
    }

    public static int[] getMaxPreviewFpsRange(Parameters params) {
        List<int[]> frameRates = params.getSupportedPreviewFpsRange();
        if (frameRates != null && frameRates.size() > 0) {
            // The list is sorted. Return the last element.
            return frameRates.get(frameRates.size() - 1);
        }
        return new int[0];
    }
}
