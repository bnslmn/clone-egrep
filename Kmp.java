import java.util.ArrayList;

public class Kmp {
    public static ArrayList<Integer> kmp(String regEx, String texte) {
        ArrayList<Integer> result = new ArrayList<>();
        int[] carryOver = constructCarryOver(regEx);
        int i = 0;
        int j = 0;
        while (i < texte.length()) {
            if (regEx.charAt(j) == texte.charAt(i)) {
                i++;
                j++;
            }
            if (j == regEx.length()) {
                result.add(i - j);
                j = carryOver[j - 1];
            } else if (i < texte.length() && regEx.charAt(j) != texte.charAt(i)) {
                if (j != 0)
                    j = carryOver[j - 1];
                else
                    i++;
            }
        }
        return result;
    }

    private static int[] constructCarryOver(String regEx) {
        int carryOver[] = new int[regEx.length()];
        int len = 0;
        int i = 1;
        carryOver[0] = 0;
        while (i < regEx.length()) {
            if (regEx.charAt(i) == regEx.charAt(len)) {
                len++;
                carryOver[i] = len;
                i++;
            } else {
                if (len != 0)
                    len = carryOver[len - 1];
                else {
                    carryOver[i] = len;
                    i++;
                }
            }
        }
        return carryOver;
    }
}
