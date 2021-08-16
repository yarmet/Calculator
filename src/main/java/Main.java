/**
 * Created by yarmet on 07.02.2015.
 */
public class Main {
    public static void main(String[] args) {


        CalculatorV2 c = new CalculatorV2();

//        System.out.println(c.getOPN("10^2*3"));
//        System.out.println(c.getAnswer("10^(2)*3"));
//
//        System.out.println(c.getOPN("3+6+90"));
//        System.out.println(c.getAnswer("3+6+90"));
//
//        System.out.println(c.getOPN("34+5+90-3"));
//        System.out.println(c.getAnswer("34+5+90-3"));

        System.out.println(c.getOpn("3 - 4 * 2 / (-1 - 5)^2"));
        System.out.println(c.calculate("3 - 4 * 2 / (-1 - 1)^2"));    }
}
