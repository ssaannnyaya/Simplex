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

    public SimplexTable(int n, int m, Fraction[] func, Fraction[][] table){
        int[] colX = new int[n];
        int[] rowX = new int[m];
        for (int j = 0; j < n; j++){
            colX[j] = j + 1;
        }
        for (int i = 0; i < m; i++){
            rowX[i] = -(n + i + 1);
        }

        this.n = n;
        this.m = m;
        this.func = func;
        this.table = table;
        this.colX = colX;
        this.rowX = rowX;

        normalize();
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
     * Шаг симплекс метода
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

    /**
     * Находит столбец в котором находится наименьшее значение нижней строки
     * @return индекс столбца
     */
    public int colForSimplexStep(){
        int col = 0;
        for (int j = 0; j < n; j++){
            if (table[m][j].lessThen(table[m][col])){
                col = j;
            }
        }
        return col;
    }

    /**
     * Находит строку в которой находится элемент с наименьшим положительным соотношением (самый правый элемент этой строки)/(этот элемент)
     * @param col столбец в котором производится поиск
     * @return строка в которой находится опорный элемент для шага симплекс-метода
     */
    public int rowForSimplexStep(int col){
        int row = 0;
        for (int i = 0; i < m; i++){
            if (table[i][col].moreThen(Fraction.zero())) {
                Fraction a = table[i][n].divide(table[i][col]);
                Fraction b = table[row][n].divide(table[row][col]);
                if (a.lessThen(b))
                row = i;
            }
        }
        return row;
    }

    /**
     * Шаг симплекс метода
     */
    public void simplexStep(){
        if (!hasSolution() || isSolved())
            return;
        int col = colForSimplexStep();
        int row = rowForSimplexStep(col);
        simplexStep(row, col);
    }

    public void removeCol(int col){
        n--;
        Fraction[][] newTable = new Fraction[m][n];
        for (int j = 0; j < col; j++){
            for (int i = 0; i <= m; i++){
                newTable[i][j] = table[i][j];
            }
        }
        for (int j = col; j <= n; j++){
            for (int i = 0; i <= m; i++){
                newTable[i][j] = table[i][j + 1];
            }
        }
        table = newTable;
        int[] newColX = new int[n];
        System.arraycopy(colX, 0, newColX, 0, col);
        if (n - col >= 0) System.arraycopy(colX, col + 1, newColX, col, n - col);
        colX = newColX;
    }

    public boolean hasAdditionalVars(){
        return Arrays.stream(colX).filter(it -> it < 0).toArray().length > 0
                || Arrays.stream(rowX).filter(it -> it < 0).toArray().length > 0;
    }

    public int findAdditionalVarColumn(){
        for (int j = 0; j < n; j++){
            if (colX[j] < 0){
                return j;
            }
        }
        return -1;
    }

    public void toMainTask(){
        int[] rowIndexes = new int[m];
        for(int i = 0; i < m; i++){
            rowIndexes[rowX[i] - 1] = i;
        }
        for (int j = 0; j <= n; j++){
            Fraction a = func[j];
            for (int i = 0; i < m; i++){
                a = a.minus(func[rowIndexes[i]].multiply(table[rowIndexes[i]][j]));
            }
            table[m][j] = a;
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
        result = 31 * result + Arrays.deepHashCode(table);
        result = 31 * result + Arrays.hashCode(colX);
        result = 31 * result + Arrays.hashCode(rowX);
        return result;
    }
}
