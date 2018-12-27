package com.mmall.util;

public class TestArrary {

    public static int[] orderArrary(int[] array){

        int tmep = 0;
        for (int i = 0; i <array.length ; i++) {
            for (int j = i+1; j<array.length ;j++) {
                if (array[i]>array[j]){
                     tmep = array[i];
                    array[i] = array[j];
                    array[j] = tmep;
                }
            }
        }
        return array;
    }

    public static void main(String[] args) {

        int[] a = new int[]{2,4,5,6,1,5};
        int[] arrary = orderArrary(a);
        for (int i = 0; i < arrary.length; i++) {
            System.out.print(arrary[i]);
        }
    }

}
