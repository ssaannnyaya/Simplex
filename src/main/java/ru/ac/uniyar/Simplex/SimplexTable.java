package ru.ac.uniyar.Simplex;

import java.util.Arrays;
import java.util.Objects;

public class SimplexTable {
    private int n;
    private int m;
    private Fraction[] func;
    private Fraction[][] table; // m, i - rows; n, j - columns
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
                sum = sum.plus(table[i][j]);
            }
            table[m][j] = sum.negative();
        }
    }

    /**
     * Если нижняя строчка неотрицательна, матрица решена
     * @return Решена ли матрица
     */
    public boolean isSolved(){
        for (int j = 0; j < n; j++){
            if (table[m][j].lessThen(Fraction.zero())){
                return false;
            }
        }
        return true;
    }

    /**
     * Если есть столбец, состоящий полностью из отрицательных чисел, то матрица нерешаема
     * @return Возможно ли дальнейшее решение
     */
    public boolean hasSolution(){
        for (int j = 0; j < n; j++){
            boolean isColBad = true;
            for (int i = 0; i <= m; i++){
                if (table[i][j].moreThen(Fraction.zero())){
                    isColBad = false;
                }
            }
            if (isColBad){
                return false;
            }
        }
        return true;
    }

    /**
     * шаг симплекс метода
     * @param row ряд, где находится опорный элемент
     * @param col столбец, где находится опорный элемент
     */
    public void simplexStep(int row, int col){
        int box = colX[col];
        colX[col] = rowX[row];
        rowX[row] = box;

        table[row][col] = table[row][col].flip();

        for (int j = 0; j < col; j++){
            table[row][j] = table[row][j].multiply(table[row][col]);
        }
        for (int j = col + 1; j <= n; j++){
            table[row][j] = table[row][j].multiply(table[row][col]);
        }

        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                table[i][j] = table[i][j].minus(table[i][col].multiply(table[row][j]));
            }
            for (int j = col + 1; j <= n; j++){
                table[i][j] = table[i][j].minus(table[i][col].multiply(table[row][j]));
            }
        }
        for (int i = row + 1; i <= m; i++){
            for (int j = 0; j < col; j++){
                table[i][j] = table[i][j].minus(table[i][col].multiply(table[row][j]));
            }
            for (int j = col + 1; j <= n; j++){
                table[i][j] = table[i][j].minus(table[i][col].multiply(table[row][j]));
            }
        }

        for (int i = 0; i < row; i++){
            table[i][col] = table[i][col].multiply(table[row][col].negative());
        }
        for (int i = row + 1; i <= n; i++){
            table[i][col] = table[i][col].multiply(table[row][col].negative());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplexTable that = (SimplexTable) o;
        return n == that.n &&
                m == that.m &&
                Arrays.equals(func, that.func) &&
                Arrays.deepEquals(table, that.table) &&
                Arrays.equals(colX, that.colX) &&
                Arrays.equals(rowX, that.rowX);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(n, m);
        result = 31 * result + Arrays.hashCode(func);
        result = 31 * result + Arrays.hashCode(table);
        result = 31 * result + Arrays.hashCode(colX);
        result = 31 * result + Arrays.hashCode(rowX);
        return result;
    }
}
