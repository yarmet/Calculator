import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorV2 {

    private final Map<String, Integer> priority = new HashMap<>();

    {
        priority.put("^", 3);
        priority.put("*", 2);
        priority.put("/", 2);
        priority.put("+", 1);
        priority.put("-", 1);
        priority.put("(", 0);
    }


    public String calculate(String expression) {

        List<String> opn = getOpn(expression);
        Stack<BigDecimal> digits = new Stack<>();

        for (String value : opn) {

            if (isOperator(value)) {

                BigDecimal secondValue = digits.pop();
                BigDecimal firstValue = digits.pop();

                switch (value) {
                    case "^" -> digits.push(firstValue.pow(secondValue.intValue(), MathContext.DECIMAL32));
                    case "*" -> digits.push(firstValue.multiply(secondValue));
                    case "/" -> digits.push(firstValue.divide(secondValue, 10, RoundingMode.HALF_EVEN));
                    case "+" -> digits.push(firstValue.add(secondValue));
                    case "-" -> digits.push(firstValue.subtract(secondValue));
                }
            } else if (value.startsWith("±")) {
                digits.push(BigDecimal.ZERO.subtract(new BigDecimal(value.substring(1))));
            } else {
                digits.push(new BigDecimal(value));
            }
        }

        return digits.pop().stripTrailingZeros().toString();
    }


    public List<String> getOpn(String expression) {
        Stack<String> operators = new Stack<>();
        List<String> result = new ArrayList<>();

        this.getSymbols(expression, symbol -> {
            if (isOperator(symbol)) {
                // Если на входе оператор и его приоритет ниже или равен чем у последнего в стеке, то достаем операторы из стека до тех пор,
                // пока последним элементом стека не будет оператор с более низким приоритетом или пока стек не окажется пуст.
                // Затем кладем в стек входящий оператор.
                while (!operators.isEmpty() && priority.get(symbol) <= priority.get(operators.peek())) {
                    result.add(operators.pop());
                }
                operators.push(symbol);
            } else if (Objects.equals("(", symbol)) {
                // Если символом является открывающая скобка, кладем ее в стек.
                operators.push(symbol);
            } else if (Objects.equals(")", symbol)) {
                // Если на вход пришла закрывающая скобка, то достаем все операторы из стека, пока не встретим открывающую скобку.
                for (String temp = operators.pop(); !Objects.equals("(", temp); temp = operators.pop()) {
                    result.add(temp);
                }
            } else {
                result.add(symbol);
            }
        });

        while (!operators.isEmpty()) result.add(operators.pop());

        return result;
    }


    boolean isOperator(String string) {
        return StringUtils.equalsAny(string, "+", "*", "/", "^", "-");
    }


    private void getSymbols(String string, Consumer<String> symbolConsumer) {
        String pattern = "[±]?([0-9]*[.])?[0-9]+|[*]|[+]|[/]|[(]|[)]|[\\^]|[-]";

        string = string
                .replaceAll("[\\s]+", "")
                .replaceAll(",", ".")
                .replaceAll("--", "+")
                .replaceAll("-+", "-")
                .replaceAll("\\(-", "(±");

        if (string.startsWith("-")) string = string.replaceFirst("-", "±");

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        while (m.find()) {
            symbolConsumer.accept(m.group(0));
        }
    }


}
