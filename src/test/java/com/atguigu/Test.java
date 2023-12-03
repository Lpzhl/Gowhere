package com.atguigu;

abstract class w {
    abstract void show();
}


class t extends w{

    @Override
    void show() {
        System.out.println("你好");
    }
}


class p extends w{
    @Override
    void show(){
        System.out.println("你不好");
    }
}


class oj{
    public static w creat(String name){
        if(name == null){
            return null;
        }
        if(name.equals("h")){
            return new t();
        }else if(name.equals("w")){
            return new p();
        }else {
            System.out.println("未知");
        }
        return null;
    }
}

public class Test {
    public static void main(String[] args) {
        w t = oj.creat("h");
        t.show();

        w o = oj.creat("w");
        o.show();

        w n = oj.creat("p");
        try{
            n.show();
        }catch (Exception e){

        }

    }
}
