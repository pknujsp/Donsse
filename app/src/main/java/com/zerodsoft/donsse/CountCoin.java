package com.zerodsoft.donsse;

public class CountCoin
{
    int won_10s = 0;
    int won_10b = 0;
    int won_50 = 0;
    int won_100 = 0;
    int won_500 = 0;

    public void SetCoin10s()
    {
        won_10s++;
    }

    public void SetCoin10b()
    {
        won_10b++;
    }


    public void SetCoin50()
    {
        won_50++;
    }

    public void SetCoin100()
    {
        won_100++;
    }

    public void SetCoin500()
    {
        won_500++;
    }

    public int GetCoin10s()
    {
        return won_10s;
    }


    public int GetCoin10b()
    {
        return won_10b;
    }

    public int GetCoin50()
    {
        return won_50;
    }

    public int GetCoin100()
    {
        return won_100;
    }

    public int GetCoin500()
    {
        return won_500;
    }

}
