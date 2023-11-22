import java.util.Scanner;
import java.util.Stack;

class commandLineCalculator
{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter an expression (e.g., 2 + 3 * (4 - 1)) or 'exit' to quit:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Calculator exiting. Goodbye!");
                break;
            }

            try {
                double result = evaluateExpression(input);
                System.out.println("Result: " + result);
            } catch (Exception e) {
                System.out.println("Invalid expression. Please try again.");
            }
        }

        scanner.close();
    }

    private static double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "").toLowerCase();

        while (expression.contains("(")) {
            int openingIndex = expression.lastIndexOf("(");
            int closingIndex = expression.indexOf(")", openingIndex);

            if (openingIndex == -1 || closingIndex == -1) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }

            String subExpression = expression.substring(openingIndex + 1, closingIndex);
            double subResult = evaluateExpression(subExpression);
            expression = expression.substring(0, openingIndex) + subResult + expression.substring(closingIndex + 1);
        }

        String[] tokens = expression.split("(?<=[-+*/^])|(?=[-+*/^])");
        return evaluateWithPrecedence(tokens);
    }

    private static double evaluateWithPrecedence(String[] tokens) {
        Stack<Double> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (isNumeric(token)) {
                operandStack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && hasPrecedence(token, operatorStack.peek())) {
                    performOperation(operandStack, operatorStack);
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            performOperation(operandStack, operatorStack);
        }

        return operandStack.pop();
    }

    private static void performOperation(Stack<Double> operandStack, Stack<String> operatorStack) {
        double operand2 = operandStack.pop();
        double operand1 = operandStack.pop();
        String operator = operatorStack.pop();
        operandStack.push(applyOperator(operand1, operand2, operator));
    }

    private static double applyOperator(double operand1, double operand2, String operator) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                return operand1 / operand2;
            case "^":
                return Math.pow(operand1, operand2);
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private static boolean hasPrecedence(String op1, String op2) {
        return (getPrecedence(op1) <= getPrecedence(op2));
    }

    private static int getPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    private static boolean isOperator(String token) {
        return token.matches("[-+*/^]");
    }

    private static boolean isNumeric(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }
}
