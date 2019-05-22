package com.zerodsoft.donsse;


import android.database.Cursor;

public class CardItem
{
    private int ID;
    private String date;
    private String imageaddress;
    private int won_10s;
    private int won_10b;
    private int won_50;
    private int won_100;
    private int won_500;

    public CardItem()
    {

    }

    public int getID()
    {
        return ID;
    }

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getImageaddress()
    {
        return imageaddress;
    }

    public void setImageaddress(String imageaddress)
    {
        this.imageaddress = imageaddress;
    }

    public int getWon_10s()
    {
        return won_10s;
    }

    public void setWon_10s(int won_10s)
    {
        this.won_10s = won_10s;
    }

    public int getWon_10b()
    {
        return won_10b;
    }

    public void setWon_10b(int won_10b)
    {
        this.won_10b = won_10b;
    }

    public int getWon_50()
    {
        return won_50;
    }

    public void setWon_50(int won_50)
    {
        this.won_50 = won_50;
    }

    public int getWon_100()
    {
        return won_100;
    }

    public void setWon_100(int won_100)
    {
        this.won_100 = won_100;
    }

    public int getWon_500()
    {
        return won_500;
    }

    public void setWon_500(int won_500)
    {
        this.won_500 = won_500;
    }
}
