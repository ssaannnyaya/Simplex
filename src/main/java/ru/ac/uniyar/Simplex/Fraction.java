package ru.ac.uniyar.Simplex;

import java.util.Objects;

public class Fraction {
    private transient int num;
    private transient int denom;

    /**
     * Конструктор дроби из двух целых чисел
     * @param num целочисленный числитель дроби
     * @param denom целочисленный знаменатель дроби
     * @throws NumberFormatException если в знаменателе ноль, возникает исключение "Dividing by zero"
     */
    public Fraction(int num, int denom){
        if (denom == 0)
            throw new NumberFormatException("Dividing by zero");
        this.num = num;
        this.denom = denom;
        cut();
    }

    /**
     * Конструктор дроби из строки
     * @param str Строка с формате "NUMERATOR/DENOMINATOR" или "INTEGER_NUMBER"
     * @throws NumberFormatException если в знаменателе ноль или если неверный формат строки
     */
    public Fraction(String str){
        String[] data = str.split("/");
        this.num = Integer.parseInt(data[0]);
        if(data.length > 1){                        //NOPMD
            this.denom = Integer.parseInt(data[1]);
        }else {
            this.denom = 1;
        }
        if (this.denom == 0)
            throw new NumberFormatException("Dividing by zero");
        cut();
    }

    public static Fraction zero(){
        return new Fraction(0,1);
    }

    public static Fraction one(){
        return new Fraction(1,1);
    }

    public int gcd(int a, int b){
        return b==0 ? a : gcd(b, a % b);
    }

    /**
     * Сокращает дробь, если минус стоит в знаменателе, переносит его в числитель
     */
    public final void cut(){
        int d = gcd(num, denom);
        this.num /= d;
        this.denom /= d;
        if(this.denom < 0){
            this.num *= -1;
            this.denom *= -1;
        }
    }

    public Fraction negative(){
        return new Fraction(-num, denom);
    }

    public Fraction flip(){
        return new Fraction(denom, num);
    }

    public Fraction plus(Fraction a){
        Fraction sum = new Fraction(this.num * a.denom + this.denom * a.num, this.denom * a.denom);
        sum.cut();
        return sum;
    }

    public Fraction minus(Fraction a){
        return plus(a.negative());
    }

    public Fraction multiply(Fraction a){
        Fraction prod = new Fraction(this.num * a.num, this.denom * a.denom);
        prod.cut();
        return prod;
    }

    public Fraction divide(Fraction a){
        return multiply(a.flip());
    }

    public boolean moreThen(Fraction a){
        return (double) this.num / this.denom > (double) a.num / a.denom;
    }

    public boolean lessThen(Fraction a){
        return (double) this.num / this.denom < (double) a.num / a.denom;
    }

    @Override
    public String toString(){
        return denom == 1 ? String.valueOf(num) : num + "/" + denom;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return num == fraction.num && denom == fraction.denom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, denom);
    }
}
