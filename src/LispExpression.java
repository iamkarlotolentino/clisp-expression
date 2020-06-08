import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LispExpression {

  /* Input of CLisp expression */
  public static String lispInput = "(+ 5 5)";

  /* Parsing variables */
  static LinkedList<String> lispInputList = new LinkedList<>();

  /* Evaluation variables */
  static String currentToken;
  static String nextToken;

  /* Validates the parentheses by count and places into queque
   * Placing it in a queue will allows us to peek the next token */
  static void putInputIntoList() {
    int parentheses = 0;

    Pattern pattern =
        Pattern.compile("[()]|([a-zA-Z]+([0-9]+)?)|(([-]?[0-9]([.][0-9]+)?)+)|['+\\-/*]");
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
      float result = arithmetic_expr();
      if (result % 1 == 0) return new String[] {String.valueOf(((int) result))};
      else return new String[] {String.valueOf(result)};
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
          if (stack.length < 1) error("Cannot have car after car. It should be a list.");
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

  static float arithmetic_expr() {
    float result = 0;
    int operandCount = 0;
    String operator = "";

    if (currentToken.equals("(")) {
      if (arithmetic_keyword()) {
        operator = currentToken;

        while (!right_paren()) {
          // CASE: New expression
          if (left_paren()) {
            if (result == 0) result = arithmetic_list(operator, s_expression());
            else
              result = do_arithmetic(operator, result, arithmetic_list(operator, s_expression()));
            operandCount++;
          }
          // CASE: Integer
          else if (integer()) {
            if (result == 0) result = Float.parseFloat(currentToken);
            else result = do_arithmetic(operator, result, Float.parseFloat(currentToken));
            operandCount++;
          } else {
            error("Invalid arithmetic expression.");
          }
        }
      }
    }

    // CASE: Single operand in an expression
    //       e.g. (-10) should be treated as negative 10.
    if (operandCount == 1 && operator.equals("-")) result = Float.parseFloat("-" + result);

    return result;
  }

  static float arithmetic_list(String operator, String[] list) {
    if (list.length == 1) return Float.parseFloat(list[0]);

    float total = 0;
    for (String s : list) {
      if (s.isEmpty()) continue;
      total = do_arithmetic(operator, total, Float.parseFloat(s));
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
    if (nextToken.matches("[-+*/]")) {
      token();
      return true;
    }
    return false;
  }

  static boolean integer() {
    if (nextToken.matches("([-]?[0-9]([.][0-9]+)?)+")) {
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

  static float do_arithmetic(String opr, float x, float y) {
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
