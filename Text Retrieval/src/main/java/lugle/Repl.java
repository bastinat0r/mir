package lugle;

import javax.sound.midi.SysexMessage;
import java.util.Scanner;

/**
 * Created by sebastian on 5/2/17.
 */
public class Repl {
    Scanner inputScanner;
    EvalInterface evaluator;

    /**
     * Makes a new repl
     * @param inputScanner input to this lugle.Repl
     * @param evalInterface how to evaluate the input
     */
    public Repl(Scanner inputScanner, EvalInterface evalInterface) {
        this.inputScanner = inputScanner;
        this.evaluator = evalInterface;
    }

    /**
     * reads a line from the input
     * @return string that was read
     */
    public String read() throws java.util.NoSuchElementException {
        System.out.print("query :>");
        return inputScanner.nextLine();
    }


    /**
     * next round of the REPL-loop
     * @return true if the program will continue after the execution of this loop-cycle
     */
    public boolean next(){
        try {
            String in = read();
            return evaluator.eval(in);
        } catch (java.util.NoSuchElementException e) {
            System.out.println("Exiting Program!\n");
        }
        return false;
    }

    /**
     * run the REPL until it exits
     */
    public void run() {
        while(this.next()) {
        }
    }
}
