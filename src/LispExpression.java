import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LispExpression {

  /* Testing cases (uncomment only one)*/
  // [ ARITHMETIC OPERATIONS ]
  // => 10
  // static String lispInput = "(+ 5 5)";
  // => 0
  //   static String lispInput = "(+ -5 5)";
  // => 20
//     static String lispInput = "(+ (+ 5 5) 5 5)";
  // => 20
//  static String lispInput = "(- (+ 10 10 10) (+ 5 5))";
  // => -20
//     static String lispInput = "(- (+ 5 5) (+ 10 10 10))";
  // => -40
     static String lispInput = "(+ (- (+ 5 5)) (- (+ 10 10 10)))";
  // => -20
  //   static String lispInput = "(+ (- 10) (- 10))";
  // [ CAR ]
  // => a
  // static String lispInput = "(car '(a b c))";
  // => 1
  // static String lispInput = "(car '(1 2 3))";
  // => b
  // [ CDR ]
  // => bb2 cc3
  // static String lispInput = "(cdr '(aa1 bb2 cc3))";
  // => 20 30
  // static String lispInput = "(cdr '(10 20 30))";
  // => c
  // static String lispInput = "(cdr (cdr '(a b c)))";
  // [ CAR + CDR ]
  // => b
  // static String lispInput = "(car (cdr '(a b c)))";
  // => c
  // static String lispInput = "(car (cdr (cdr '(a b c d e f))))";
  // [ CAR + CDR + ARITHMETIC ]
  // => 8
  // static String lispInput = "(+ (car '(1 2 3)) 3 4 )";
  // => 25
  // static String lispInput = "(+ (car '(1 2 3)) (- 3 4) (* 5 5))";
  // [ INVALID EXPRESSIONS ]
  // => parentheses
  // static String lispInput = "((+ 5 5)";
  // => invalid
  // static String lispInput = "(+ + 5 5)";
  // => car after car
  // static String lispInput = "(car (car (cdr '(a b c d e f))))";
  // => cdr after car
  // static String lispInput = "(cdr (car '(a b c d e f)))";

  /* Parsing variables */
  static LinkedList<String> lispInputList = new LinkedList<>();

  /* Evaluation variables */
  static String currentToken;
  static String nextToken;

  /* Validates the parentheses by count and places into queque
   * Placing it in a queue will allows us to peek the next token */
  static void putInputIntoList() {
    int parentheses = 0;

    Pattern pattern = Pattern.compile("[()]|([a-zA-Z]|[-]?[0-9])+|['+\\-/*]");
    Matcher matcher = pattern.matcher(lispInput);

    while (matcher.find()) {
      String token = matcher.group();
      if (token.equals("(")) parentheses++;
      if (token.equals(")")) parentheses--;
      lispInputList.add(matcher.group());
    }

    // ERROR: Incomplete parentheses during parsing.
    if (parentheses != 0) {
      error("Parentheses is mismatched.");
    }
  }

  static void token() {
    if (!lispInputList.isEmpty()) currentToken = lispInputList.pop();
    if (!lispInputList.isEmpty()) nextToken = lispInputList.peek();
  }

  static String[] s_expression() {
    if (nextToken.matches("[-+*/]")) {
      return new String[] {String.valueOf(arithmetic_expr())};
    } else if (nextToken.matches("car|cdr")) {
      return car_cdr_expr();
    }
    return new String[] {""};
  }

  static String[] car_cdr_expr() {
    String[] stack = null;
    String[] current = null;
    if (car_keyword()) {
      // New parenthicals of expression
      if (left_paren()) stack = car_cdr_expr();
      // Evaluate the current token
      if (!currentToken.equals(")")) current = new String[] {car_opr()};
      // Finally, closing section...
      if (right_paren()) {
        if (stack != null) {
          if (stack.length < 2) error("Cannot have car after car. It should be a list.");
          return new String[] {stack[0]};
        }
        return current;
      }
    } else if (cdr_keyword()) {
      if (left_paren()) stack = car_cdr_expr();
      if (!currentToken.equals(")")) current = cdr_opr();
      if (right_paren()) {
        if (stack != null) {
          if (stack.length == 1) error("Cannot have cdr after car. It should be a list.");
          return new String[] {stack[1]};
        }
        return current;
      }
    }
    return current;
  }

  static String car_opr() {
    String result = "";
    if (single_quote()) {
      if (left_paren()) {
        result = nextToken;
        do {
          // Ignore all succeeding tokens
          token();
        } while (!right_paren());
      }
    }
    return result;
  }

  static String[] cdr_opr() {
    // StringBuilder has better performance impact.
    StringBuilder result = new StringBuilder();
    if (single_quote()) {
      if (left_paren()) {
        // Remove the first token
        token();
        // Then, store all succeeding tokens
        // by appending it to a string separated by a space.
        while (!right_paren()) {
          token();
          result.append(currentToken).append(" ");
        }
      }
    }
    // Split the tokens into arrays
    return result.toString().split(" ");
  }

  static int arithmetic_expr() {
    String operator = "";
    StringBuilder total = new StringBuilder();

    if (currentToken.equals("(")) {
      if (arithmetic_keyword()) {
        operator = currentToken;
        do {
          if (integer()) {
            total.append(currentToken).append(" ");
          } else if (left_paren()) {
            total.append(arithmetic_list(operator, s_expression())).append(" ");
          } else if (right_paren()) {
            return arithmetic_list(operator, total.toString().split(" "));
          } else {
            error("Expected token not found. Looking for list of numbers or parentheses.");
          }
        } while (!right_paren());
      }
    } else {
      error("Arithmetic expression mismatched!");
    }
    if (total.length() == 1 && operator.equals("-"))
      return Integer.parseInt("-" + total.toString().split(" ")[0]);
    return arithmetic_list(operator, total.toString().split(" "));
  }

  static int arithmetic_list(String operator, String[] list) {
    int total = 0;
    for (String s : list) {
      if (s.isEmpty()) continue;
      total = do_arithmetic(operator, total, Integer.parseInt(s));
    }
    return total;
  }

  static boolean car_keyword() {
    if (nextToken.equals("car")) {
      token();
      return true;
    }
    return false;
  }

  static boolean cdr_keyword() {
    if (nextToken.equals("cdr")) {
      token();
      return true;
    }
    return false;
  }

  static boolean arithmetic_keyword() {
    if (nextToken.matches("[-|+|*|/]")) {
      token();
      return true;
    }
    return false;
  }

  static boolean integer() {
    if (nextToken.matches("[-]?[0-9]+")) {
      token();
      return true;
    }
    return false;
  }

  static boolean left_paren() {
    if (nextToken.matches("[(]")) {
      token();
      return true;
    }
    return false;
  }

  static boolean right_paren() {
    if (nextToken.matches("[)]")) {
      token();
      return true;
    }
    return false;
  }

  static boolean single_quote() {
    if (nextToken.equals("'")) {
      token();
      return true;
    }
    return false;
  }

  static int do_arithmetic(String opr, int x, int y) {
    if (x == 0) return y;
    if (opr.equals("+")) return x + y;
    if (opr.equals("-")) return x - y;
    if (opr.equals("*")) return x * y;
    if (opr.equals("/")) return x / y;
    return 0;
  }

  static void error(String message) {
    System.out.println(message);
    System.exit(-1);
  }

  public static void main(String[] args) {
    System.out.println(lispInput);
    putInputIntoList();
    token();
    System.out.println(Arrays.toString(s_expression()));
  }
}
