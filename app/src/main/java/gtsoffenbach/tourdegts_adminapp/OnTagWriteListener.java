package gtsoffenbach.tourdegts_adminapp;

/**
 * Created by Kern on 21.07.2014.
 */
public class OnTagWriteListener {

        public static final int WRITE_OK = 0;
        public static final int WRITE_ERROR_READ_ONLY = 1;
        public static final int WRITE_ERROR_CAPACITY = 2;
        public static final int WRITE_ERROR_BAD_FORMAT = 3;
        public static final int WRITE_ERROR_IO_EXCEPTION = 4;
        public static final int WRITE_ERROR_TAG_LOST = 5;

        public static String onTagWrite(int status) {
            switch (status) {
                case 0:
                    return "WRITE_OK";
                case 1:
                    return "WRITE_ERROR_READ_ONLY";
                case 2:
                    return "WRITE_ERROR_CAPACITY";
                case 3:
                    return "WRITE_ERROR_BAD_FORMAT";
                case 4:
                    return "WRITE_ERROR_IO_EXCEPTION";
                case 5:
                    return "WRITE_ERROR_TAG_LOST";
            }
            return null;
        }
    }

