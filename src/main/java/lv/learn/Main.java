package lv.learn;

public class Main {

    byte vByte = 0;
    short vShort = 0;
    float vFloat = 1.1F;
    double vDouble = 1.1D;

    int decVal = 26;
    int hexVal = 0x1a;
    int binVal = 0b11010;

    long creditCardNumber = 1234_5678_9012_3456L;
    float pi =  3.14_15F;
    long hexWords = 0xCAFE_BABE;
    long maxLong = 0x7fff_ffff_ffff_ffffL;

    Main[][] arr = {{new Main(), new Main()}, {}, {}};

    static long longValue = Long.parseUnsignedLong("18446744073709551615");

    public static void main(String[] args) {

        System.out.println(longValue); // -1
        System.out.println(Long.toUnsignedString(longValue)); // 18446744073709551615
    }

//    public static void main(String[] args) {
//
//        Integer varInteger = 1;
//        Integer.unsigne
//
//    }
}