package ru.ac.uniyar.Simplex.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class SimplexTable {
    private transient int n;
    private transient int m;
    private transient Fraction[] func;
    private transient Fraction[][] table; // m, i - rows; n, j - columns
    private transient int[] colX;
    private transient int[] rowX;
    boolean isMinimisation;
    static Logger log = Logger.getLogger(SimplexTable.class.getName());

    /**
     * Конструктор со всеми параметрами
     * @param n количество переменных в целевой функции / количество столбцов
     * @param m количество ограничений / количество строк
     * @param func целевая функция
     * @param table таблица ограничений
     * @param colX переменные в заголовках столбцов
     * @param rowX переменные в заголовках строк
     * @param isMinimisation если true - задача минимизации, если false - максимизации
     */
    public SimplexTable(int n, int m, Fraction[] func, Fraction[][] table, int[] colX, int[] rowX, boolean isMinimisation){
        this.n = n;
        this.m = m;
        this.func = func.clone();
        this.table = table.clone();
        this.colX = colX.clone();
        this.rowX = rowX.clone();
        this.isMinimisation = isMinimisation;
    }

    /**
     * Конструктор с указанием размерностей, функции и таблицы ограничений.
     * После создания матрица приводится в вид для поиска базиса:
     * в строки записываются дополнительные переменные,
     * строки с отрицательными константами в ограничениях домножаются на -1,
     * вычисляется нижний столбец
     * @param n количество переменных в целевой функции / количество столбцов
     * @param m количество ограничений / количество строк
     * @param func целевая функция
     * @param table таблица ограничений
     * @param isMinimisation если true - задача минимизации, если false - максимизации
     */
    public SimplexTable(int n, int m, Fraction[] func, Fraction[][] table, boolean isMinimisation){
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
        this.func = func.clone();
        this.table = table.clone();
        this.colX = colX.clone();
        this.rowX = rowX.clone();
        this.isMinimisation = isMinimisation;
        normalize();
    }

    public SimplexTable(int n, int m, Fraction[] func, Fraction[][] table, int[] vars, boolean isMinimisation) {
        this.n = n - m;
        this.m = m;
        this.func = func.clone();
        Gauss gauss = new Gauss(n, m, table.clone());
        gauss.calculateByVars(vars.clone());
        this.table = gauss.getTableWithoutVars(vars.clone());
        colX = new int[n - m];
        rowX = new int[m];
        int col = 0;
        int row = 0;
        for (int j = 1; j <= n; j++) {
            int J = j;
            if (Arrays.stream(vars).filter(it -> it == J).toArray().length != 0) {
                rowX[row] = j;
                row++;
            } else {
                colX[col] = j;
                col++;
            }
        }
        this.isMinimisation = isMinimisation;
        toMainTask();
    }

    /**
     * Создать симплекс-таблицу, считав информацию из файла
     * @param filePath путь к файлу
     */
    public SimplexTable(String filePath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String[] data = reader.readLine().split(" ");
            this.n = Integer.parseInt(data[0]);
            this.m = Integer.parseInt(data[1]);
            this.isMinimisation = Boolean.parseBoolean(data[2]);

            table = new Fraction[m+1][n+1];
            rowX = new int[m];
            colX = new int[n];
            data = reader.readLine().split(" ");
            func = new Fraction[data.length];
            for (int j = 0; j  < data.length; j++){
                func[j] = new Fraction(data[j]);
            }
            data = reader.readLine().split(" ");
            for (int j = 0; j  < n; j++){
                colX[j] = Integer.parseInt(data[j]);
            }
            for (int i = 0; i < m; i++){
                data = reader.readLine().split(" ");
                rowX[i] = Integer.parseInt(data[0]);
                for (int j = 0; j  <= n; j++){
                    table[i][j] = new Fraction(data[j+1]);
                }
            }
            data = reader.readLine().split(" ");
            for (int j = 0; j  <= n; j++){
                table[m][j] = new Fraction(data[j]);
            }
        }catch (IOException e){
            log.info(e.getMessage());
        }
    }

    /**
     * Записать симплекс таблицу в файл
     * @param filePath путь к файлу
     */
    public void writeToFile(String filePath){
        try( Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(this.toString());
        }catch (IOException e){
            log.info(e.getMessage());
        }
    }

    public SimplexTable clone(){
        Fraction[][] newTable = new Fraction[m + 1][n + 1];     //копируем данные в новую таблицу без целевого столбца
        for (int j = 0; j <= n; j++){
            for (int i = 0; i <= m; i++){
                newTable[i][j] = table[i][j];
            }
        }
        return new SimplexTable(
                n,
                m,
                func.clone(),
                newTable,
                colX.clone(),
                rowX.clone(),
                isMinimisation
        );
    }

    public Fraction[][] getTable() {
        return table.clone();
    }

    public Fraction[] getFunc() {
        return func;
    }

    public int[] getColX() {
        return colX;
    }

    public int[] getRowX() {
        return rowX;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public boolean isMinimisation() {
        return isMinimisation;
    }

    /**
     * Если константа в ограничении отрицательная, домножаем всю строку на -1
     * Пересчитывает нижнюю строчку
     */
    public final void normalize(){
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
    public void simplexStep(int row, int col){  //NOPMD
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
        for (int i = row + 1; i <= m; i++){
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
                if (!table[row][col].moreThen(Fraction.zero())) {
                    row = i;
                } else {
                    Fraction a = table[i][n].divide(table[i][col]);
                    Fraction b = table[row][n].divide(table[row][col]);
                    if (a.lessThen(b) || b.lessThen(Fraction.zero())) {
                        row = i;
                    }
                }
            }
        }
        return row;
    }

    /**
     * Находит координаты всех возможных опорных элементов
     * @return Список массивов, первый элемент массива - индекс сроки, второй - индекс столбца
     */
    public ArrayList<Integer[]> getPossibleElementsForStep() {
        ArrayList<Integer[]> possibleElements = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            if (table[m][j].lessThen(Fraction.zero())) {
                int row = 0;
                for (int i = 0; i < m; i++) {
                    if (table[i][j].moreThen(Fraction.zero())) {
                        if (!table[row][j].moreThen(Fraction.zero())) {
                            row = i;
                        } else {
                            Fraction a = table[i][n].divide(table[i][j]);
                            Fraction b = table[row][n].divide(table[row][j]);
                            if (a.lessThen(b) || b.lessThen(Fraction.zero())) {
                                row = i;
                            }
                        }
                    }
                }
                for (int i = 0; i < m; i++) {
                    if (table[i][j].moreThen(Fraction.zero()) && table[row][j].moreThen(Fraction.zero())) {
                        Integer[] element = new Integer[2];
                        Fraction a = table[i][n].divide(table[i][j]);
                        Fraction b = table[row][n].divide(table[row][j]);
                        if (a.equals(b)) {
                            element[0] = i;
                            element[1] = j;
                            possibleElements.add(element);
                        }
                    }
                }
            }
        }
        return possibleElements;
    }

    /**
     * Шаг симплекс метода.
     * Поиск опорного элемента, выполняется симплекс-шаг вокруг него
     */
    public void simplexStep(){
        if (!hasSolution() || isSolved())
            return;
        int col = colForSimplexStep();
        int row = rowForSimplexStep(col);
        simplexStep(row, col);
    }

    /**
     * Удаление столбца из таблицы, уменьшает размерность матрицы
     * @param col индекс столбца, который нужно удалить
     */
    public void removeCol(int col){
        n--;                                                    //уменьшаем размерность
        Fraction[][] newTable = new Fraction[m + 1][n + 1];     //копируем данные в новую таблицу без целевого столбца
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
        int[] newColX = new int[n];                             //копируем список переменных в заглавии столбцов без целевого
        System.arraycopy(colX, 0, newColX, 0, col);
        if (n - col >= 0) System.arraycopy(colX, col + 1, newColX, col, n - col);
        colX = newColX;
    }

    /**
     * Есть ли в матрице дополнительные переменные(для поиска базиса)
     * @return Наличие дополнительных переменных
     */
    public boolean hasAdditionalVars(){
        return Arrays.stream(colX).filter(it -> it < 0).toArray().length > 0
                || Arrays.stream(rowX).filter(it -> it < 0).toArray().length > 0;
    }

    /**
     * Находит индекс столбца с дополнительной переменной(для поиска базиса)
     * @return индекс столбца, если подходящего нет, то -1
     */
    public int findAdditionalVarColumn(){
        for (int j = 0; j < n; j++){
            if (colX[j] < 0){
                return j;
            }
        }
        return -1;
    }

    /**
     * Переход к основной задаче, когда базис найден, из имеющейся матрицы и функции вычисляется нижняя строчка
     * Если есть дополнительные переменные ничего не происходит
     */
    public void toMainTask(){
        if (hasAdditionalVars())
            return;
        Fraction[] newFunc = new Fraction[func.length];
        for (int i = 0; i < func.length; i++) {
            newFunc[i] = isMinimisation? func[i]: func[i].negative();
        }
        for (int j = 0; j < n; j++){                                        //считаем значения в нижней строке для переменных в заглавии столбцов
            Fraction a = newFunc[colX[j] - 1];
            for (int i = 0; i < m; i++){
                a = a.minus(newFunc[rowX[i]-1].multiply(table[i][j]));
            }
            table[m][j] = a;
        }
        Fraction a = newFunc[m + n];                                       //считаем значение в нижней левой ячейке
        for (int i = 0; i < m; i++){
            a = a.plus(newFunc[rowX[i]-1].multiply(table[i][n]));
        }
        table[m][n] = a.negative();
    }

    /**
     * Получение ответа на задачу линейного программирования
     * @return Значение нижнего правого элемента умноженное на -1
     */
    public Fraction getAnswer(){
        return isMinimisation? table[m][n].negative(): table[m][n];
    }

    /**
     * @param isDecimal Если true выводит дробные значения в десятичном виде, false - в виде обыкновенных дробей
     * @return Если задача решена выводит ответ в виде f(x1, ... xn) = a
     * Если задача не решена, но возможно продолжение, выводит пустую строку
     * Если в задаче остались дополнительные переменные выводит сообщение о том, что задача несовместна
     * Если дальнейшее решение невозможно, выводит сообщение о том, что функция неограниченна
     */
    public String getAnswerAsString(boolean isDecimal) {
        if (!isSolved()) {
            return "";
        }
        if (hasAdditionalVars()) {
            return "Задача несовместна";
        }
        if (!hasSolution()) {
            return "Функция неограниченна";
        }
        StringBuilder str = new StringBuilder("f(");
        Fraction[] vars = getAnswerAsArray();
        str.append(vars[0].getFrString(isDecimal));
        for (int i = 1; i < n + m; i++) {
            str.append(", ").append(vars[i].getFrString(isDecimal));
        }
        str.append(") = ").append(getAnswer().getFrString(isDecimal));
        return str.toString();
    }

    public Fraction[] getAnswerAsArray() {
        if (!isSolved() || hasAdditionalVars() || !hasSolution()) {
            return new Fraction[0];
        }
        Fraction[] answer = new Fraction[n + m];
        for (int j = 0; j < n; j++) {
            answer[colX[j] - 1] = Fraction.zero();
        }
        for (int i = 0; i < m; i++) {
            answer[rowX[i] -1] = table[i][n];
        }
        return answer;
    }

    public String getFuncAsString(boolean isDecimal) {
        StringBuilder str = new StringBuilder();
        if (!func[0].equals(Fraction.zero())) {
            if (func[0].equals(Fraction.one().negative())) {
                str.append("-");
            } else {
                if (!func[0].equals(Fraction.one())) {
                    str.append(func[0].getFrString(isDecimal));
                }
            }
            str.append("x").append("\u2081");
        }
        for (int i = 1; i < func.length-1; i++) {
            if (func[i].moreThen(Fraction.zero())) {
                if (!func[i-1].equals(Fraction.zero())) {
                    str.append("+");
                }
                if (!func[i].equals(Fraction.one())) {
                    str.append(func[i].getFrString(isDecimal));
                }
                str.append("x").append(i > 10 ? "\u2081": "").append((char) ('\u2080' + ((i + 1) % 10)));
            }
            if (func[i].lessThen(Fraction.zero())) {
                if (func[i].equals(Fraction.one().negative())) {
                    str.append("-");
                } else {
                    str.append(func[i].getFrString(isDecimal));
                }
                str.append("x").append(i > 10 ? "\u2081": "").append((char) ('\u2080' + ((i + 1) % 10)));
            }
        }
        if (func[func.length - 1].moreThen(Fraction.zero())) {
            if (func.length > 1 && !func[func.length-2].equals(Fraction.zero())) {
                str.append("+");
            }
            str.append(func[func.length - 1].getFrString(isDecimal));
        }
        if (func[func.length - 1].lessThen(Fraction.zero())) {
            str.append(func[func.length - 1].getFrString(isDecimal));
        }
        str.append(" --> ");
        str.append(isMinimisation() ? "min" : "max");
        return str.toString();
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

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder(n + " " + m + " " + isMinimisation + "\n");
        for (int j = 0; j < func.length - 1; j++){
            str.append(func[j].toString()).append(" ");
        }
        str.append(func[func.length - 1].toString()).append("\n");
        for (int j = 0; j < n - 1; j++){
            str.append(colX[j]).append(" ");
        }
        str.append(colX[n - 1]).append("\n");
        for (int i = 0; i < m; i++){
            str.append(rowX[i]).append(" ");
            for (int j = 0; j < n; j++){
                str.append(table[i][j]).append(" ");
            }
            str.append(table[i][n]).append("\n");
        }
        for (int j = 0; j < n; j++){
            str.append(table[m][j]).append(" ");
        }
        str.append(table[m][n]).append("\n");
        return str.toString();
    }

    /**
     * Проверяет файл на соответствие формату хранения симплекс таблицы
     * @param filePath Путь к проверяемому файлу
     * @return true - если файл соответствует формату, false - иначе
     */
    public static boolean isOkFile(String filePath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String[] data = reader.readLine().split(" ");
            int n = Integer.parseInt(data[0]);
            int m = Integer.parseInt(data[1]);
            Boolean.parseBoolean(data[2]);

            data = reader.readLine().split(" ");
            if (data.length < n || data.length > n + m + 1) {
                return false;
            }
            for (String datum : data) {
                new Fraction(datum);
            }
            data = reader.readLine().split(" ");
            for (int j = 0; j  < n; j++){
                Integer.parseInt(data[j]);
            }
            for (int i = 0; i < m; i++){
                data = reader.readLine().split(" ");
                Integer.parseInt(data[0]);
                for (int j = 0; j  <= n; j++){
                    new Fraction(data[j+1]);
                }
            }
            data = reader.readLine().split(" ");
            for (int j = 0; j  <= n; j++){
                new Fraction(data[j]);
            }
        }catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e){
            return false;
        }
        return true;
    }
}