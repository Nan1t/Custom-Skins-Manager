package ru.csm.api.network;

public final class Sequences {

    public static final byte[] WAIT_NEXT_SEQUENCE = new byte[]{
            (byte) 0xCD, (byte) 0xCF,
            (byte) 0xBD, (byte) 0xBF
    };

    public static final byte[] LAST_ELEMENT_SEQUENCE = new byte[]{
            (byte) 0xED, (byte) 0xAF,
            (byte) 0xAD, (byte) 0xEF
    };

    private Sequences(){ }

    public static boolean isWaitNext(byte[] bytes){
        return isStartWithSequence(bytes, WAIT_NEXT_SEQUENCE);
    }

    public static boolean isLastElement(byte[] bytes){
        return isStartWithSequence(bytes, LAST_ELEMENT_SEQUENCE);
    }

    private static boolean isStartWithSequence(byte[] data, byte[] sequence){
        return data[0] == sequence[0]
                && data[1] == sequence[1]
                && data[2] == sequence[2]
                && data[3] == sequence[3];
    }
}
