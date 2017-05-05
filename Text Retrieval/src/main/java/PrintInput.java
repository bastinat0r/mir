/**
 * Created by sebastian on 5/2/17.
 */
public class PrintInput implements EvalInterface{
    /**
     * evaluates the input and prints the result
     * @param input
     * @return true if the repl should continue after this evaluation phase
     */
    @Override
    public boolean eval(String input) {
        System.out.println(input);
        return true;
    }
}
