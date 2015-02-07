package com.company;


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

class Calculator {
    
    private Map<Character, Integer> priority = new HashMap<Character, Integer>();

    {
        priority.put('^', 3);
        priority.put('*', 2);
        priority.put('/', 2);
        priority.put('+', 1);
        priority.put('-', 1);
        priority.put('(', 0);
    }


    
    private void processingOperators(Character inputSymbol, List<String> outString1, Stack<Character> operatorStack1) {
        List<String> outString = outString1;
        Stack<Character> operatorStack = operatorStack1;
        // если если стек оператов пуст или символом является открывающая скобка, кладем ее в стек.
        if (inputSymbol == '(' || operatorStack.isEmpty()) {
            operatorStack.push(inputSymbol);
            return;
        }
        // если на вход пришла закрывающая скобка, то достаем все операторы из стека, пока не встретим открывающую скобку.
        if (inputSymbol == ')') {
            while (operatorStack.peek() != '(') {
                outString.add(operatorStack.pop().toString());
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
                outString.add(operatorStack.pop().toString());
            }
            operatorStack.push(inputSymbol);
        }
    }

    
    
    /**
     * метод возвращает обратную польскую запись.
     */
   public List getOPN(String inputString) {
       //стэк для хранения операторов
        Stack<Character> operatorStack = new Stack();
       // выходной массив
        List<String> out = new ArrayList<String>();
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
                //минус перед которым нет другого числа является унарным. за исключением случаев
                // когда перед ним закрывающая скобка
                if (!isDigit(previousSymbol) && curentSymbol == '-' && previousSymbol != ')') curentSymbol = '±';
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
                if (tempString.length() > 0) {
                    out.add(tempString.toString());
                }
                tempString.delete(0, tempString.length());
                processingOperators(curentSymbol,out,operatorStack);
            }
        }
        //кладем в выходную строку последнее число из входной строки, если оно есть
        if (tempString.length() > 0)
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


    /**
     *выводит ответ выражения
     */
   public BigDecimal getAnswer(String inputSymbol) {

        BigDecimal firstDigit, secondDigit;
        Stack<BigDecimal> stack = new Stack<BigDecimal>();
        
        //получаем обратную польскую запись.
       List<String> out = getOPN(inputSymbol);
        
        try {
            for (String t : out) {
                // меняем значок унарного минуса на обычный значок.
                if (t.contains("±")) {
                    t = t.replace("±", "-");
                }
                // если на входе оператор ^,/,*,+,-  то вытаскиваем из стека два числа и производим над ними действие.
                if (isOperator(t)) {
                    secondDigit = stack.pop();
                    firstDigit = stack.pop();

                    if (t.equals("^")) {
                        stack.push(firstDigit.pow(secondDigit.intValue(), MathContext.DECIMAL32));
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
                    // если на входе число, то кладем его в стек.
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