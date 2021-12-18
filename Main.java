import java.util.Scanner;

public class Main {

    public static void main(String arg[]) {
        String regEx = "";
        String fileName = "";
        if (arg.length > 0)
            regEx = arg[0];
        else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("  >> Please enter a regEx: ");
            regEx = scanner.next();
            scanner.close();
        }

        if (regEx.length() < 1) {
            System.out.println("  >> ERROR: empty regEx.");
            return;
        }

        regEx = regEx.replace(".", "");
        regEx =regEx.replace("()", "");
        System.out.print("  >> ASCII codes: [" + (int) regEx.charAt(0));
        for (int i = 1; i < regEx.length(); i++)
            System.out.print("," + (int) regEx.charAt(i));
        System.out.println("].");

        if (arg.length > 1)
            fileName = arg[1];

        Reader.grep(regEx, fileName);
    }
}
