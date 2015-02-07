package com.company;

/**
 * Программа калькулятора использующего обратную польскую запись.
 * https://ru.wikipedia.org/wiki/%D0%9E%D0%B1%D1%80%D0%B0%D1%82%D0%BD%D0%B0%D1%8F_%D0%BF%D0%BE%D0%BB%D1%8C%D1%81%D0%BA%D0%B0%D1%8F_%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C
 */

import java.math.BigDecimal;
import java.util.*;

class Calculator {

    private Stack<Character> operatorStack = new Stack();
    private List<String> out = new ArrayList<String>();

    private Map<Character, Integer> priority = new HashMap<Character, Integer>();

    {
        priority.put('^', 3);
        priority.put('*', 2);
        priority.put('/', 2);
        priority.put('+', 1);
        priority.put('-', 1);
        priority.put('(', 0);
    }


    private void processingOperators(Character inputSymbol) {
        // если если стек оператов пуст или символом является открывающая скобка, кладем ее в стек.
        if (inputSymbol == '(' || operatorStack.isEmpty()) {
            operatorStack.push(inputSymbol);
            return;
        }
        // если на вход пришла закрывающая скобка, то достаем все операторы из стека, пока не встретим открывающую скобку.
        if (inputSymbol == ')') {
            while (operatorStack.peek() != '(') {
                out.add(operatorStack.pop().toString());
            }
            operatorStack.pop();
            return;
        }
        // если на входе опертор, и его приоритет выше, чем приоритет последнего оператора в стеке, то кладем его в стек.
        if (priority.get(inputSymbol) > priority.get(operatorStack.peek())) {
            operatorStack.push(inputSymbol);
        } else {
            // Если на входе оператор и его приоритет ниже или равен чем у последнего в стеке, то достаем операторы из стека до тех пор,
            //пока последним елементом стека не будет оператор с более низким приоритетом или пока стек не окажется пуст.
            // Затем кладем в стек входящий оператор.
            while (!operatorStack.isEmpty() && priority.get(inputSymbol) <= priority.get(operatorStack.peek())) {
                out.add(operatorStack.pop().toString());
            }
            operatorStack.push(inputSymbol);
        }
    }


    List getOPN(String inputString) {

        StringBuilder tempString = new StringBuilder();
        Character previousSymbol = null;

        for (Character curentSymbol : inputString.toCharArray()) {
            // пропускаем пробелы
            if (curentSymbol == ' ') continue;
            // заменим запятую точкой
            if (curentSymbol == ',') curentSymbol = '.';
            //если наш символ первый в строке.
            if (previousSymbol == null) {
                // и если он минус, то заменяем его унарным минусом ±
                if (curentSymbol == '-') curentSymbol = '±';
                // если наш символ не первый в строке
            } else {
                //минус перед которым нет другого числа является унарным.
                if (!isDigit(previousSymbol) && curentSymbol == '-') curentSymbol = '±';
                //  минус на плюс  дает минус
                if (previousSymbol == '-' && curentSymbol == '+') continue;
            }
            // приравниваем предыдущий символ к текущему
            previousSymbol = curentSymbol;
            //  если нам попадает подряд несколько цифр, кладем их во временную строку, т.к. это одно большое число.
            if (isDigit(curentSymbol)) {
                tempString.append(curentSymbol);
                // если нам попадается оператор, значит цифры числа кончились и мы имеем во временной строке полное число.
                // Перекидываем число из временной строки в выходную,
                // обнуляем временную строку и начинаем работать с оператором.
            } else {
                if (tempString.length() != 0) {
                    out.add(tempString.toString());
                }
                tempString.delete(0, tempString.length());
                processingOperators(curentSymbol);
            }
        }
        //кладем в выходную строку последнее число из входной строки
        out.add(tempString.toString());
        // после того как входная строка закончилась , выталкиваем все операторы из стека.
        while (!operatorStack.isEmpty()) {
            out.add(operatorStack.pop().toString());
        }
        return out;
    }


    private boolean isDigit(Character t) {
        return t >= '0' && t <= '9' || t == '.' || t == '±';
    }


    private boolean isOperator(String s) {
        return s.equals("^") || s.equals("*") || s.equals("/") || s.equals("+") || s.equals("-");
    }


    BigDecimal calculate() {
        BigDecimal firstDigit, secondDigit;
        Stack<BigDecimal> stack = new Stack<BigDecimal>();
        try {
            for (String t : out) {
                if (isOperator(t)) {
                    secondDigit = stack.pop();
                    firstDigit = stack.pop();

                    if (t.equals("^")) {
                        stack.push(firstDigit.pow(secondDigit.intValue()).setScale(8, BigDecimal.ROUND_HALF_EVEN));
                    }
                    if (t.equals("*")) {
                        stack.push(firstDigit.multiply(secondDigit).setScale(8, BigDecimal.ROUND_HALF_EVEN));
                    }
                    if (t.equals("/")) {
                        stack.push(firstDigit.divide(secondDigit, 8, BigDecimal.ROUND_HALF_EVEN));
                    }
                    if (t.equals("+")) {
                        stack.push(firstDigit.add(secondDigit).setScale(8, BigDecimal.ROUND_HALF_EVEN));
                    }
                    if (t.equals("-")) {
                        stack.push(firstDigit.subtract(secondDigit).setScale(8, BigDecimal.ROUND_HALF_EVEN));
                    }
                } else {
                    t = t.replace("±", "-");
                    stack.push(new BigDecimal(t));
                }
            }
        } catch (Exception e) {
            System.err.print("ошибка при вычислении");
            System.exit(0);
        }
        return stack.pop();
    }
}