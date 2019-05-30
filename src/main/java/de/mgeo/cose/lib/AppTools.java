package de.mgeo.cose.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Scanner;

import static java.lang.System.out;

public class AppTools {

    public String getFromCommandline(String question) {
        if (System.getProperty("DEBUG_MODE").equals("TRUE")) {
            out.println(question);
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine() + "";
        }
        else {
            return this.getPassword(question);
        }
    }


    public String getPassword(String question) {
        out.println(question);
        String password = "";

        ConsoleEraser consoleEraser = new ConsoleEraser();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        consoleEraser.start();
        try {
            password = in.readLine();
        }
        catch (IOException e){
            System.out.println("Error trying to read your password!");
            System.exit(1);
        }

        consoleEraser.halt();
        //System.out.print("\b");

        return password;
    }




    private class ConsoleEraser extends Thread {
        private boolean running = true;
        public void run() {
            while (running) {
                System.out.print("\b ");
                try {
                    Thread.currentThread().sleep(1);
                }
                catch(InterruptedException e) {
                    break;
                }
            }
        }
        public synchronized void halt() {
            running = false;
        }
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
