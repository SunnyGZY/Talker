package net.sunny.talker.observe;

public interface Function<Result, Param> {

    Result function(Param... data);
}