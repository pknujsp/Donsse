package com.zerodsoft.donsse;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class processing_image
{
    boolean check_circle = true;
    CountCoin countCoin = null;

    Mat houghtransform(Mat processed_data, Mat originaldata)
    {

        CalcFromRadius calc = new CalcFromRadius();
        Mat givendata = processed_data;
        Mat original = originaldata;
        Mat circles = new Mat();
        Imgproc.HoughCircles(givendata, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 95, 110, 30, 42, 84);

        if (circles.cols() > 0)
        {
            for (int x = 0; x < circles.total(); x++)
            {
                double circleDATA[] = circles.get(0, x);
                if (circleDATA == null)
                {
                    break;
                }
                Point center = new Point((int) circleDATA[0], (int) circleDATA[1]);
                int radius = (int) circleDATA[2];

                String string = calc.GetCoin(radius);

                Imgproc.circle(original, center, radius, new Scalar(0, 0, 255), 2);
                Imgproc.putText(original, string, new Point(center.x - 25, center.y + 10), Core.FONT_ITALIC, 1.1, new Scalar(255, 255, 0), 2);
            }
        } else
        {
            check_circle = false;
        }
        countCoin = calc.GetCountClass();
        return original;
    }

    CountCoin GetCountClass()
    {
        return countCoin;
    }

    boolean CheckDetection()
    {
        return check_circle;
    }

}