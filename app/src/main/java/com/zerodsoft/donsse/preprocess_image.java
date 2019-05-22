package com.zerodsoft.donsse;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class preprocess_image
{


    Mat pre_process(Mat givendata)
    {
        Mat OriginalImage = givendata;

        Mat mat1 = new Mat();

        Mat kernel = Mat.ones(new Size(5, 5), CvType.CV_8U);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));

        Imgproc.morphologyEx(OriginalImage, mat1, Imgproc.MORPH_ELLIPSE, element);

        Imgproc.GaussianBlur(mat1, mat1, new Size(5, 5), 4);

        Mat mat2 = new Mat();

        Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGB2GRAY);

        Imgproc.adaptiveThreshold(mat1, mat2, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 47,
                3);

        Imgproc.erode(mat2, mat2, kernel);

        java.util.List<MatOfPoint> contours = new ArrayList<>();

        Mat hierarchy = new Mat();

        Imgproc.findContours(mat2, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Object[] values = contours.toArray();

        Mat markers = Mat.zeros(mat2.size(), CvType.CV_8U);

        for (int i = 0; i < contours.size(); i++)
        {
            Point center = new Point();
            float[] radius = new float[1];
            MatOfPoint2f points = new MatOfPoint2f(((MatOfPoint) values[i]).toArray());
            Imgproc.minEnclosingCircle(points, center, radius);

            if (radius[0] > 45 && radius[0] < 80)
                Imgproc.circle(markers, center, (int) radius[0], new Scalar(255), 2);
        }

        return markers;
    }


}