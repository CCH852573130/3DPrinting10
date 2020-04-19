package usbcontroler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class USBDiskState {
    private static final String MOUNTS_FILE = "/proc/mounts";

    private static String path = "/storage/usbhost1";

    public static boolean isMounted() {

        boolean blnRet = false;
        String strLine = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(MOUNTS_FILE));

            while ((strLine = reader.readLine()) != null) {
                if (strLine.contains(path)) {
                    blnRet = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reader = null;
            }
        }
        return blnRet;
    }
}
