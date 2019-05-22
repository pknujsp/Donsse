package com.zerodsoft.donsse;

public class CalcFromRadius
{
    CountCoin countcoin = new CountCoin();

    String GetCoin(int radiusVal)
    {
        String COIN = "";
        if (radiusVal >= 50 && radiusVal <= 54)
        {
            COIN = "10W";
            countcoin.SetCoin10s();
        } else if (radiusVal >= 64 && radiusVal <= 68)
        {
            COIN = "10W";
            countcoin.SetCoin10b();
        } else if (radiusVal >= 59 && radiusVal <= 63)
        {
            COIN = "50W";
            countcoin.SetCoin50();
        } else if (radiusVal >= 69 && radiusVal <= 71)
        {
            COIN = "100W";
            countcoin.SetCoin100();
        } else if (radiusVal >= 75 && radiusVal <= 79)
        {
            COIN = "500W";
            countcoin.SetCoin500();
        }
        return COIN;
    }

    public CountCoin GetCountClass()
    {
        return countcoin;
    }
}
