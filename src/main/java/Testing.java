import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Testing {
    private static final Pattern linePattern = Pattern.compile("^\\s*(\\w+)\\s+(?:(\\w+)\\s+(\\w+)?)?\\s*");

    // Create a Map that will store the lazily evaluated register values
    private Map<String,LazyValue> regMap = new HashMap<>();

    // The interface describing the lazy value
    interface LazyValue {
        int evaluate();
    }

    class IntValue implements LazyValue {
        private int value;

        IntValue(int value) {
            this.value = value;
        }

        public int evaluate() {
            return value;
        }
    }

    class RefValue implements LazyValue {
        private String ref;

        RefValue(String ref) {
            this.ref = ref;
        }

        public int evaluate() {
            LazyValue lv = regMap.get(ref);
            return lv == null ? null : lv.evaluate();
        }
    }

    abstract class BinOp implements LazyValue {
        LazyValue lhs;
        LazyValue rhs;

        BinOp(LazyValue lhs, LazyValue rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    class Add extends BinOp {
        Add(LazyValue lhs, LazyValue rhs) {
            super(lhs, rhs);
        }

        public int evaluate() {
            return lhs.evaluate() + rhs.evaluate();
        }
    }

    class Subtract extends BinOp {
        Subtract(LazyValue lhs, LazyValue rhs) {
            super(lhs, rhs);
        }

        public int evaluate() {
            return lhs.evaluate() - rhs.evaluate();
        }
    }

    class Multiply extends BinOp {
        Multiply(LazyValue lhs, LazyValue rhs) {
            super(lhs, rhs);
        }

        public int evaluate() {
            return lhs.evaluate() * rhs.evaluate();
        }
    }

    class Divide extends BinOp {
        Divide(LazyValue lhs, LazyValue rhs) {
            super(lhs, rhs);
        }

        public int evaluate() {
            return lhs.evaluate() / rhs.evaluate();
        }
    }

    public static void main(String[] args) {

        // This rdr just uses System.in. Could just as well be a FileReader reading
        // form a file.
        try(Reader rdr = new InputStreamReader(System.in)) {
            new Testing().serve(rdr);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void serve(Reader input) throws IOException {
        // Use a LineNumberReader to make it easy to read one line at a time
        LineNumberReader lnr = new LineNumberReader(input);
        String line;
        while((line = lnr.readLine()) != null) {

            // Everything is supposed to be case insensitive, so convert the whole line
            // to lower case before splitting it on whitespace
            String[] ops = line.toLowerCase().split("\\s+");
            System.out.println(Arrays.toString(ops));

            // First token is either a command (quit or print) or a register name
            String reg = ops[0];
            if(reg.equals("quit")) {
                return;
            }

            // Let the rest of the arguments be names of registers. Not part of the
            // assignment but this allowes "print A B C".
            if(reg.equals("print")) {
                for(int i = 1; i < ops.length; i++) {
                    LazyValue v = regMap.get(ops[i]);
                    if(v != null) {
                        System.out.println(v.evaluate());
                    }
                }
                continue;
            }

            // Neither quit or print. Must be an operator then.
            if(ops.length != 3) {
                System.out.println("Invalid input. Try again");
                continue;
            }

            String op = ops[1];

            // Use Integer instead of just int to avoid NullPointerException when there is
            // no value.
            LazyValue lhs = regMap.get(reg);

            try {
                try {
                    LazyValue rhs = new IntValue(Integer.parseInt(ops[2]));
                    if(lhs == null) {
                        regMap.put(reg, rhs);
                    } else {
                        regMap.put(reg, performIntegerOp(op, lhs, rhs));
                    }
                } catch(NumberFormatException e) {
                    // Obviously not an integer. Must be name of register
                    String r2 = ops[2];
                    LazyValue rhs = regMap.get(r2);
                    if(rhs == null) {
                        rhs = new RefValue(r2);
                    }
                    if(lhs == null) {
                        regMap.put(reg, rhs);
                    } else {
                        regMap.put(reg, performIntegerOp(op, lhs, rhs));
                    }
                }
            } catch(IllegalArgumentException e) {
                System.out.printf("Unable to perform operation: %s\n", e.getMessage());
            }
        }
    }

    // Perform the given operation or throw exception.
    private LazyValue performIntegerOp(String op, LazyValue lhs, LazyValue rhs) {
        switch(op) {
            case "add":
                return new Add(lhs, rhs);
            case "subtract":
                return new Subtract(lhs, rhs);
            case "multiply":
                return new Multiply(lhs, rhs);
            case "divide":
                return new Divide(lhs, rhs);
        }
        throw new IllegalArgumentException("bad int operator: '" + op + '\'');
    }
}
