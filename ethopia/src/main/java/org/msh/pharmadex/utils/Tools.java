package org.msh.pharmadex.utils;

/**
 * Created by Одиссей on 17.07.2016.
 */
public class Tools {
    /*
     * Возвращает определенное количество символов строки, слева
     */
    public static String left(String s, int num){
        if (s==null)
            return s;
        int len = s.length();
        if (num>=len || num<=0) return s;
        s=s.substring(0, num);
        return s;
    }

    /*
     * Возвращает левую часть строки, за исключение последних num символов
     */
    public static String leftBack(String s, int num){
        if (s==null)
            return s;
        int len = s.length();
        if (num>=len || num<=0) return s;
        s = s.substring(0, len-num);
        return s;
    }

    /*
     * Возвращает левую часть строки, за исключением последнего символа chr и всех, следующих за ним
     */
    public static String left(String s, String chr){
        int index= s.lastIndexOf(chr);
        if (index==0) return s;
        return left(s, index);
    }

    /**
     * Возвращает num символов справа в строке s
     * @param s
     * @param num
     * @return
     */
    public  static String right(String s, int num){
        if (s==null)
            return s;
        int len = s.length();
        if (num>=len || num<=0) return s;
        s = s.substring(len-num,len);
        return s;
    }

    /**
     * Возвращает правую часть строки до первого встреченного символа chr (слева)
     * @param s - исходная строка
     * @param chr - символ для поиска
     * @return
     */
    public  static String right(String s, String chr){
        if (s==null)
            return s;
        int len = s.length();
        if (len==0) return s;
        int index = s.indexOf(chr);
        if (index<0) return s;
        String res = s.substring(index+1,len);
        return res;
    }

    public static Double currencyToDouble(String s){
        if (s==null) return 0.0;
        s=s.trim();
        if (s.startsWith("$"))
            s=right(s,"$");
        Double res = Double.parseDouble(s);
        return res;
    }

    public static boolean isEmptyString(String str){
        if (str==null) return true;
        return "".equals(str);
    }

}
