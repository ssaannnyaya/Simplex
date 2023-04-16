package ru.ac.uniyar.Simplex;

import java.util.Objects;

public class Fraction {
    private int num;
    private int denom;

    public Fraction(int num, int denom) throws NumberFormatException{
        this.num = num;
        this.denom = denom;
        cut();
    }

    public Fraction(String str) throws NumberFormatException{
        String[] data = str.split("/");
        this.num = Integer.parseInt(data[0]);
        if(data.length > 1){
            this.denom = Integer.parseInt(data[1]);
        }else {
            this.denom = 1;
        }
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
     * @throws NumberFormatException если в знаменателе ноль, возникает исключение "Dividing by zero"
     */
    public void cut() throws NumberFormatException{
        if (this.denom == 0)
            throw new NumberFormatException("Dividing by zero");
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

    public void plus(Fraction a) throws NumberFormatException{
        this.num = this.num * a.denom + this.denom * a.num;
        this.denom = this.denom * a.denom;
        cut();
    }

    public void minus(Fraction a) throws NumberFormatException{
        plus(a.negative());
    }

    public void multiply(Fraction a) throws NumberFormatException{
        this.num = this.num * a.num;
        this.denom = this.denom * a.denom;
        cut();
    }

    public void divide(Fraction a) throws NumberFormatException{
        multiply(a.flip());
    }

    public boolean moreThen(Fraction a){
        return this.num / this.denom > a.num / a.denom;
    }

    public boolean lessThen(Fraction a){
        return !moreThen(a);
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
