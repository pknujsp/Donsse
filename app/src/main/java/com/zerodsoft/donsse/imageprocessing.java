package com.zerodsoft.donsse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class imageprocessing
{

    String path;
    CountCoin countCoin = null;

    Mat process_(File file)
    {
        image_absolute_path(file);
        Mat image_original = Imgcodecs.imread(file.getAbsolutePath(), 4);
        preprocess_image ppe = new preprocess_image();
        processing_image pg = new processing_image();
        GetA4image getA4image = new GetA4image();
        Mat cropped_image = getA4image.GetCALCULATED_IMAGE(image_original);
        Mat processed_image = ppe.pre_process(cropped_image);

        Mat image = pg.houghtransform(processed_image, cropped_image);
        countCoin = pg.GetCountClass();
        if (pg.CheckDetection())
        {
            return image;
        } else
        {
            return null;
        }
    }

    void image_absolute_path(File file)
    {
        this.path = file.getAbsolutePath();

    }

    CountCoin GetCountClass()
    {
        return countCoin;
    }

    String get_image_path()
    {
        return this.path;
    }

}



