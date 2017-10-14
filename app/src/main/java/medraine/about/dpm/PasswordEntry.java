package medraine.about.dpm;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by Raine on 04.12.2016.
 */

class PasswordEntry {
    private String title;
    private String pssw;
    private String usr;
    private String place;
    private int length = -1;

    PasswordEntry(String title, String pssw, String usr, String place) {
        this.title = title;
        this.pssw = pssw;
        this.usr = usr;
        this.place = place;
    }

    PasswordEntry(String title, int length, String usr, String place) {
        this.title = title;
        this.pssw = reGenerate(length);
        this.length = length;
        this.usr = usr;
        this.place = place;
    }
    PasswordEntry() {}

    int getLength() {
        return length;
    }

    String getTitle() {
        return title;
    }

    String getPssw() {
        return pssw;
    }

    String getUsr() {
        return usr;
    }

    String getPlace() {
        return place;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    void setPssw(String pssw) {
        this.pssw = pssw;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    //int l - rework
    String reGenerate(int length) {
        int l = length, j = 0;
        char set[] = new char[91];
        ArrayList<Integer> used = new ArrayList<>();

        for(int i = 0; i < length; i++) {
            used.add(-1);
        }

        set[0] = 'a';
        set[1] = 'b';
        set[2] = 'c';
        set[3] = 'd';
        set[4] = 'e';
        set[5] = 'f';
        set[6] = 'g';
        set[7] = 'h';
        set[8] = 'i';
        set[9] = 'j';
        set[10] = 'k';
        set[11] = 'l';
        set[12] = 'm';
        set[13] = 'n';
        set[14] = 'o';
        set[15] = 'p';
        set[16] = 'q';
        set[17] = 'r';
        set[18] = 's';
        set[19] = 't';
        set[20] = 'u';
        set[21] = 'v';
        set[22] = 'w';
        set[23] = 'x';
        set[24] = 'y';
        set[25] = 'z';

        set[26] = 'A';
        set[27] = 'B';
        set[28] = 'C';
        set[29] = 'D';
        set[30] = 'E';
        set[31] = 'F';
        set[32] = 'G';
        set[33] = 'H';
        set[34] = 'I';
        set[35] = 'J';
        set[36] = 'K';
        set[37] = 'L';
        set[38] = 'M';
        set[39] = 'N';
        set[40] = 'O';
        set[41] = 'P';
        set[42] = 'Q';
        set[43] = 'R';
        set[44] = 'S';
        set[45] = 'T';
        set[46] = 'U';
        set[47] = 'V';
        set[48] = 'W';
        set[49] = 'X';
        set[50] = 'Y';
        set[51] = 'Z';

        set[52] = '0';
        set[53] = '1';
        set[54] = '2';
        set[55] = '3';
        set[56] = '4';
        set[57] = '5';
        set[58] = '6';
        set[59] = '7';
        set[60] = '8';
        set[61] = '9';

        set[62] = '?';
        set[63] = '&';
        set[64] = '!';
        set[65] = '@';
        set[66] = '#';
        set[67] = '$';
        set[68] = '%';
        set[69] = '(';
        set[70] = ')';
        set[71] = '~';

        set[72] = '^';
        set[73] = '`';
        set[74] = '_';
        set[75] = '-';
        set[76] = '+';
        set[77] = '=';
        set[78] = '{';
        set[79] = '[';
        set[80] = '}';
        set[81] = ']';
        set[82] = '"';
        set[83] = '\'';
        set[84] = '*';
        set[85] = '>';
        set[86] = '.';
        set[87] = '<';
        set[88] = ',';
        set[89] = ':';
        set[90] = ';';

        SecureRandom srnd = new SecureRandom(ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(System.currentTimeMillis()*(long)length).array());

        StringBuilder builder = new StringBuilder();
        builder.append("");
        while(!isStrongEnough(builder.toString())) {
            if(j < 10) {
                builder = new StringBuilder();
                builder.append("");
                for (int i = 0; i < l; i++) {
                    used.set(i, -1);
                }

                for (int i = 0; i < l; i++) {
                    int rand = srnd.nextInt(72);
                    if (used.get(i) == -1) {
                        while (used.contains(rand)) {
                            rand = srnd.nextInt(72);
                        }
                    }
                    builder.append(set[rand]);
                    used.set(i, rand);
                }
                j++;
            }
            else {
                j = 0;
                l++;
            }
        }

        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return builder.toString();
    }

    private boolean isStrongEnough(String s) {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(s);

        if(strength.getCrackTimesDisplay().getOnlineThrottling100perHour().equals("centuries") &&
                strength.getCrackTimesDisplay().getOnlineNoThrottling10perSecond().equals("centuries") &&
                strength.getCrackTimesDisplay().getOfflineSlowHashing1e4perSecond().equals("centuries") &&
                strength.getCrackTimesDisplay().getOfflineFastHashing1e10PerSecond().equals("centuries")) {
            return true;
        }
        return false;
    }
}
