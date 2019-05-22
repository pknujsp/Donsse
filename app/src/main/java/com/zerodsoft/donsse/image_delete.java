package com.zerodsoft.donsse;


import java.io.File;

public class image_delete
{
    String FILEPATH;
    File imagefile;

    image_delete()
    {

    }

    image_delete(File infofile)
    {
        this.FILEPATH = infofile.getAbsolutePath();
        this.imagefile = new File(FILEPATH);
    }

    public Boolean deleteImageFile()
    {

        Boolean checking = imagefile.delete();
        return checking;
    }

    public void delete_image(String filepath)
    {
        this.imagefile = new File(filepath);
        imagefile.delete();
    }
}
