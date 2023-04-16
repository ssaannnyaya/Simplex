package ru.ac.uniyar.Simplex;

public class SimplexTable {
    private int n;
    private int m;
    private Fraction[] func;
    private Fraction[][] table;
    private int[] colX;
    private int[] rowX;

    public SimplexTable(int n, int m, Fraction[] func, Fraction[][] table, int[] colX, int[] rowX){
        this.n = n;
        this.m = m;
        this.func = func;
        this.table = table;
        this.colX = colX;
        this.rowX = rowX;
    }

    public Fraction[][] getTable() {
        return table;
    }

    /**
     * Если константа в ограничении отрицательная, домножаем всю строку на -1
     * Пересчитывает нижнюю строчку
     */
    public void normalize(){
        for (int i = 0; i < m; i++){
            if (table[i][n].lessThen(Fraction.zero())){
                for (int j = 0; j <= n; j++){
                    table[i][j] = table[i][j].negative();
                }
            }
        }
        for (int j = 0; j <= n ;j++){
            Fraction sum = Fraction.zero();
            for (int i = 0; i < m; i++){
                sum.plus(table[i][j]);
            }
            table[m][j] = sum.negative();
        }
    }

}
