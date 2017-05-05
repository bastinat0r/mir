import java.util.Scanner;

/**
 * Created by sebastian on 5/2/17.
 */
public class Repl {
    Scanner inputScanner;
    EvalInterface evaluator;

    /**
     * Makes a new repl
     * @param inputScanner input to this Repl
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
    public String read() {
        System.out.print("query :>");
        return inputScanner.nextLine();
    }


    /**
     * next round of the REPL-loop
     * @return true if the program will continue after the execution of this loop-cycle
     */
    public boolean next(){
        String in = read();
        return evaluator.eval(in);
    }

    /**
     * run the REPL until it exits
     */
    public void run() {
        while(this.next()) {
        }
    }
}
