package de.mgeo.cose.lib;

import java.sql.Timestamp;
import java.util.Scanner;

import static java.lang.System.out;

public class AppTools {

    public String getFromCommandline(String question) {
        out.println(question);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine() + "";
    }

    public String getTimestamp() {
        return new Timestamp(System.currentTimeMillis()) + "";
    }

    public void out(String s) {
        out.println(s);
    }

    public void printText(String text) {
        out.println(text);
    }

    public void printError(String text) {
        out.println(text);
    }


    public void printLine(int max, String s) {
        for (int i = 0; i < max; i++) {
            out.printf("%s", s);
        }
        out.println(" ");
    }

    public void printHello() {
        printText(" ");
        printLine(70, "#");
        printText("# Secret & Configmap Creator");
        printLine(70, "-");
    }

    public void printUsage() {
        printText(" * Usage: 'java -jar jarfilename.jar <absolute_path_inputfile>'");
        printLine(70, "-");
    }
}
