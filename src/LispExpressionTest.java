import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LispExpressionTest {

  @Test
  void arithmetic_1() {
    LispExpression.lispInput = "(+ 5 5)";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[10]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_2() {
    LispExpression.lispInput = "(- 12.123 6 18)";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[-11.877]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_3() {
    LispExpression.lispInput = "(+ -5 5)";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[0]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_4() {
    LispExpression.lispInput = "(+ (- 5 5) 3 3)";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[6]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_5() {
    LispExpression.lispInput = "(+ (/ 5 5) 5 5)";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[11]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_6() {
    LispExpression.lispInput = "(- (* 10 10 10) (/ 5 5))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[999]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_7() {
    LispExpression.lispInput = "(+ (- (+ 5 5)) (- (+ 10 10 10)))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[-40]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_8() {
    LispExpression.lispInput = "(+ 5 5 (+ 5 5))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[20]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void arithmetic_9() {
    LispExpression.lispInput = "(+ 5 5 (+ 5 5) 5 (- 5 10))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[20]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void car_1() {
    LispExpression.lispInput = "(car '(a b c))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[a]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void car_2() {
    LispExpression.lispInput = "(car '(1 2 3))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[1]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void car_3() {
    LispExpression.lispInput = "(car '(+ 1 2 3))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[+]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void car_4() {
    LispExpression.lispInput = "(car '(cdr car 1 2 3))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[cdr]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void cdr_1() {
    LispExpression.lispInput = "(cdr '(aa1 bb2 cc3))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[bb2, cc3]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void cdr_2() {
    LispExpression.lispInput = "(cdr '(10 20 30))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[20, 30]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void cdr_3() {
    LispExpression.lispInput = "(cdr (cdr '(a b c)))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[c]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void car_cdr_1() {
    LispExpression.lispInput = "(car (cdr '(a b c)))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[b]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void car_cdr_2() {
    LispExpression.lispInput = "(car (cdr (cdr '(a b c d e f))))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[c]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void mix_1() {
    LispExpression.lispInput = "(+ (car '(1 2 3)) 3 4 )";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[8]", Arrays.toString(LispExpression.s_expression()));
  }

  @Test
  void mix_2() {
    LispExpression.lispInput = "(+ (car '(1 2 3)) (- 3 4) (* 5 5))";
    LispExpression.putInputIntoList();
    LispExpression.token();
    assertEquals("[25]", Arrays.toString(LispExpression.s_expression()));
  }
}
