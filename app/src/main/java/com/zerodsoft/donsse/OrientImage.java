package com.zerodsoft.donsse;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class OrientImage
{
    ExifInterface EXIF = null;
    int value_orientation;

    public void GetEXIF(String path)
    {
        try
        {
            EXIF = new ExifInterface(path);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        value_orientation = EXIF.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    public Bitmap ProcessImage(String path, Bitmap bitmap)
    {
        GetEXIF(path);
        return RotateImage(bitmap, value_orientation);
    }

    public Bitmap RotateImage(Bitmap bitmap, int value)
    {
        Matrix matrix = new Matrix();

        switch (value)
        {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }

        try
        {
            Bitmap rotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotate;
        } catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
