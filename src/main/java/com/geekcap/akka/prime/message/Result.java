package com.geekcap.akka.prime.message;

import java.util.ArrayList;
import java.util.List;

public class Result
{
    private List<Long> results = new ArrayList<Long>();

    public Result()
    {
    }

    public List<Long> getResults()
    {
        return results;
    }
}