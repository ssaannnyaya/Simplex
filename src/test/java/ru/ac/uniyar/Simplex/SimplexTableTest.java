package ru.ac.uniyar.Simplex;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class SimplexTableTest {


    @Test
    public void normaliseTest(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1");
        table[0][1] = new Fraction("2");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("1/2");
        table[1][1] = new Fraction("2");
        table[1][2] = new Fraction("-1");
        table[2][0] = new Fraction("-3/2");
        table[2][1] = new Fraction("-4");
        table[2][2] = new Fraction("0");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        simplexTable.normalize();
        assertThat(simplexTable.getTable()[1][0]).isEqualTo(new Fraction("-1/2"));
        assertThat(simplexTable.getTable()[1][1]).isEqualTo(new Fraction("-2"));
        assertThat(simplexTable.getTable()[1][2]).isEqualTo(new Fraction("1"));
        assertThat(simplexTable.getTable()[2][0]).isEqualTo(new Fraction("-1/2"));
        assertThat(simplexTable.getTable()[2][1]).isEqualTo(new Fraction("0"));
        assertThat(simplexTable.getTable()[2][2]).isEqualTo(new Fraction("-2"));
    }

    @Test
    public void yesSolved(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("-1");
        table[0][1] = new Fraction("-2");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("1/2");
        table[1][1] = new Fraction("-2");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("3/2");
        table[2][1] = new Fraction("4");
        table[2][2] = new Fraction("0");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.isSolved()).isTrue();
    }

    @Test
    public void yesSolvedNegativeAnswer(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("-1");
        table[0][1] = new Fraction("-2");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("1/2");
        table[1][1] = new Fraction("-2");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("3/2");
        table[2][1] = new Fraction("4");
        table[2][2] = new Fraction("-8");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.isSolved()).isTrue();
    }

    @Test
    public void noSolved(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("-1");
        table[0][1] = new Fraction("2");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("-1/2");
        table[1][1] = new Fraction("2");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-3/2");
        table[2][1] = new Fraction("-4");
        table[2][2] = new Fraction("0");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.isSolved()).isFalse();
    }

    @Test
    public void yesSolution(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1");
        table[0][1] = new Fraction("2");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("1/2");
        table[1][1] = new Fraction("2");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-3/2");
        table[2][1] = new Fraction("-4");
        table[2][2] = new Fraction("0");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.hasSolution()).isTrue();
    }

    @Test
    public void noSolution(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("-1");
        table[0][1] = new Fraction("2");
        table[0][2] = new Fraction("-1");
        table[1][0] = new Fraction("-1/2");
        table[1][1] = new Fraction("2");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-3/2");
        table[2][1] = new Fraction("-4");
        table[2][2] = new Fraction("0");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.hasSolution()).isFalse();
    }

    @Test
    public void equalTest(){
        int n1 = 2;
        int m1 = 2;
        Fraction[] func1 = new Fraction[n1 + 1];
        func1[0] = new Fraction("1/2");
        func1[1] = new Fraction("1");
        func1[2] = new Fraction("0");
        Fraction[][] table1 = new Fraction[m1 + 1][n1 + 1];
        table1[0][0] = new Fraction("-1");
        table1[0][1] = new Fraction("2");
        table1[0][2] = new Fraction("1");
        table1[1][0] = new Fraction("-1/2");
        table1[1][1] = new Fraction("2");
        table1[1][2] = new Fraction("1");
        table1[2][0] = new Fraction("-3/2");
        table1[2][1] = new Fraction("-4");
        table1[2][2] = new Fraction("0");
        int[] colX1 = new int[2];
        colX1[0] = 1;
        colX1[1] = 2;
        int[] rowX1 = new int[2];
        rowX1[0] = 3;
        rowX1[1] = 4;
        SimplexTable simplexTable1 = new SimplexTable(n1, m1, func1, table1, colX1, rowX1);

        int n2 = 2;
        int m2 = 2;
        Fraction[] func2 = new Fraction[n2 + 1];
        func2[0] = new Fraction("1/2");
        func2[1] = new Fraction("1");
        func2[2] = new Fraction("0");
        Fraction[][] table2 = new Fraction[m2 + 1][n2 + 1];
        table2[0][0] = new Fraction("-1");
        table2[0][1] = new Fraction("2");
        table2[0][2] = new Fraction("1");
        table2[1][0] = new Fraction("-1/2");
        table2[1][1] = new Fraction("2");
        table2[1][2] = new Fraction("1");
        table2[2][0] = new Fraction("-3/2");
        table2[2][1] = new Fraction("-4");
        table2[2][2] = new Fraction("0");
        int[] colX2 = new int[2];
        colX2[0] = 1;
        colX2[1] = 2;
        int[] rowX2 = new int[2];
        rowX2[0] = 3;
        rowX2[1] = 4;
        SimplexTable simplexTable2 = new SimplexTable(n2, m2, func2, table2, colX2, rowX2);
        assertThat(simplexTable1.equals(simplexTable2)).isTrue();
    }

    @Test
    public void simplexStepTest(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table1 = new Fraction[m + 1][n + 1];
        table1[0][0] = new Fraction("1/3");
        table1[0][1] = new Fraction("1/3");
        table1[0][2] = new Fraction("1");
        table1[1][0] = new Fraction("2/3");
        table1[1][1] = new Fraction("-1/3");
        table1[1][2] = new Fraction("1");
        table1[2][0] = new Fraction("-1/3");
        table1[2][1] = new Fraction("-1/3");
        table1[2][2] = new Fraction("4");
        int[] colX1 = new int[2];
        colX1[0] = 1;
        colX1[1] = 2;
        int[] rowX1 = new int[2];
        rowX1[0] = 3;
        rowX1[1] = 4;
        SimplexTable simplexTable1 = new SimplexTable(n, m, func, table1, colX1, rowX1);

        Fraction[][] table2 = new Fraction[m + 1][n + 1];
        table2[0][0] = new Fraction("-1/2");
        table2[0][1] = new Fraction("1/2");
        table2[0][2] = new Fraction("1/2");
        table2[1][0] = new Fraction("3/2");
        table2[1][1] = new Fraction("-1/2");
        table2[1][2] = new Fraction("3/2");
        table2[2][0] = new Fraction("1/2");
        table2[2][1] = new Fraction("-1/2");
        table2[2][2] = new Fraction("9/2");
        int[] colX2 = new int[2];
        colX2[0] = 4;
        colX2[1] = 2;
        int[] rowX2 = new int[2];
        rowX2[0] = 3;
        rowX2[1] = 1;
        SimplexTable simplexTable2 = new SimplexTable(n, m, func, table2, colX2, rowX2);

        simplexTable1.simplexStep(1, 0);
        assertThat(simplexTable1.equals(simplexTable2)).isTrue();
    }

    @Test
    public void elementForSimplexStep(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1/3");
        table[0][1] = new Fraction("1/3");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("2/3");
        table[1][1] = new Fraction("-1/3");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-1/3");
        table[2][1] = new Fraction("-1/3");
        table[2][2] = new Fraction("4");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 2;
        int[] rowX = new int[2];
        rowX[0] = 3;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        int col = simplexTable.colForSimplexStep();
        int row = simplexTable.rowForSimplexStep(col);
        assertThat(col).isEqualTo(0);
        assertThat(row).isEqualTo(1);
    }

    @Test
    public void nonParametrisedSimplexStepTest(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table1 = new Fraction[m + 1][n + 1];
        table1[0][0] = new Fraction("1/3");
        table1[0][1] = new Fraction("1/3");
        table1[0][2] = new Fraction("1");
        table1[1][0] = new Fraction("2/3");
        table1[1][1] = new Fraction("-1/3");
        table1[1][2] = new Fraction("1");
        table1[2][0] = new Fraction("-1/3");
        table1[2][1] = new Fraction("-1/3");
        table1[2][2] = new Fraction("4");
        int[] colX1 = new int[2];
        colX1[0] = 1;
        colX1[1] = 2;
        int[] rowX1 = new int[2];
        rowX1[0] = 3;
        rowX1[1] = 4;
        SimplexTable simplexTable1 = new SimplexTable(n, m, func, table1, colX1, rowX1);

        Fraction[][] table2 = new Fraction[m + 1][n + 1];
        table2[0][0] = new Fraction("-1/2");
        table2[0][1] = new Fraction("1/2");
        table2[0][2] = new Fraction("1/2");
        table2[1][0] = new Fraction("3/2");
        table2[1][1] = new Fraction("-1/2");
        table2[1][2] = new Fraction("3/2");
        table2[2][0] = new Fraction("1/2");
        table2[2][1] = new Fraction("-1/2");
        table2[2][2] = new Fraction("9/2");
        int[] colX2 = new int[2];
        colX2[0] = 4;
        colX2[1] = 2;
        int[] rowX2 = new int[2];
        rowX2[0] = 3;
        rowX2[1] = 1;
        SimplexTable simplexTable2 = new SimplexTable(n, m, func, table2, colX2, rowX2);

        simplexTable1.simplexStep();
        assertThat(simplexTable1.equals(simplexTable2)).isTrue();
    }

    @Test
    public void anotherCreatingTest(){
        int n1 = 2;
        int m1 = 2;
        Fraction[] func1 = new Fraction[n1 + 1];
        func1[0] = new Fraction("1/2");
        func1[1] = new Fraction("1");
        func1[2] = new Fraction("0");
        Fraction[][] table1 = new Fraction[m1 + 1][n1 + 1];
        table1[0][0] = new Fraction("-1");
        table1[0][1] = new Fraction("2");
        table1[0][2] = new Fraction("-1");
        table1[1][0] = new Fraction("-1/2");
        table1[1][1] = new Fraction("2");
        table1[1][2] = new Fraction("1");
        SimplexTable simplexTable1 = new SimplexTable(n1, m1, func1, table1);

        int n2 = 2;
        int m2 = 2;
        Fraction[] func2 = new Fraction[n2 + 1];
        func2[0] = new Fraction("1/2");
        func2[1] = new Fraction("1");
        func2[2] = new Fraction("0");
        Fraction[][] table2 = new Fraction[m2 + 1][n2 + 1];
        table2[0][0] = new Fraction("1");
        table2[0][1] = new Fraction("-2");
        table2[0][2] = new Fraction("1");
        table2[1][0] = new Fraction("-1/2");
        table2[1][1] = new Fraction("2");
        table2[1][2] = new Fraction("1");
        table2[2][0] = new Fraction("-1/2");
        table2[2][1] = new Fraction("0");
        table2[2][2] = new Fraction("-2");
        int[] colX2 = new int[2];
        colX2[0] = 1;
        colX2[1] = 2;
        int[] rowX2 = new int[2];
        rowX2[0] = -3;
        rowX2[1] = -4;
        SimplexTable simplexTable2 = new SimplexTable(n2, m2, func2, table2, colX2, rowX2);
        assertThat(simplexTable1.equals(simplexTable2)).isTrue();
    }

    @Test
    public void yesAdditionalVars(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1/3");
        table[0][1] = new Fraction("1/3");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("2/3");
        table[1][1] = new Fraction("-1/3");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-1/3");
        table[2][1] = new Fraction("-1/3");
        table[2][2] = new Fraction("4");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 3;
        int[] rowX = new int[2];
        rowX[0] = 2;
        rowX[1] = -4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.hasAdditionalVars()).isTrue();
    }

    @Test
    public void noAdditionalVars(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1/3");
        table[0][1] = new Fraction("1/3");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("2/3");
        table[1][1] = new Fraction("-1/3");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-1/3");
        table[2][1] = new Fraction("-1/3");
        table[2][2] = new Fraction("4");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 3;
        int[] rowX = new int[2];
        rowX[0] = 2;
        rowX[1] = 4;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.hasAdditionalVars()).isFalse();
    }

    @Test
    public void findAdditionalColumn(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1/3");
        table[0][1] = new Fraction("1/3");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("2/3");
        table[1][1] = new Fraction("-1/3");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-1/3");
        table[2][1] = new Fraction("-1/3");
        table[2][2] = new Fraction("4");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = -4;
        int[] rowX = new int[2];
        rowX[0] = 2;
        rowX[1] = 3;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.findAdditionalVarColumn()).isEqualTo(1);
    }

    @Test
    public void cantFindAdditionalColumn(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1/3");
        table[0][1] = new Fraction("1/3");
        table[0][2] = new Fraction("1");
        table[1][0] = new Fraction("2/3");
        table[1][1] = new Fraction("-1/3");
        table[1][2] = new Fraction("1");
        table[2][0] = new Fraction("-1/3");
        table[2][1] = new Fraction("-1/3");
        table[2][2] = new Fraction("4");
        int[] colX = new int[2];
        colX[0] = 1;
        colX[1] = 4;
        int[] rowX = new int[2];
        rowX[0] = 2;
        rowX[1] = 3;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        assertThat(simplexTable.findAdditionalVarColumn()).isEqualTo(-1);
    }

    @Test
    public void toMainTest(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + m + 1];
        func[0] = new Fraction("1");
        func[1] = new Fraction("-5");
        func[2] = new Fraction("-1");
        func[3] = new Fraction("1");
        func[4] = new Fraction("0");
        Fraction[][] table1 = new Fraction[m + 1][n + 1];
        table1[0][0] = new Fraction("1/2");
        table1[0][1] = new Fraction("1/2");
        table1[0][2] = new Fraction("1/3");
        table1[1][0] = new Fraction("3/2");
        table1[1][1] = new Fraction("-1/2");
        table1[1][2] = new Fraction("2");
        table1[2][0] = new Fraction("0");
        table1[2][1] = new Fraction("0");
        table1[2][2] = new Fraction("0");
        int[] colX1 = new int[2];
        colX1[0] = 3;
        colX1[1] = 4;
        int[] rowX1 = new int[2];
        rowX1[0] = 2;
        rowX1[1] = 1;
        SimplexTable simplexTable1 = new SimplexTable(n, m, func, table1, colX1, rowX1);

        Fraction[][] table2 = new Fraction[m + 1][n + 1];
        table2[0][0] = new Fraction("1/2");
        table2[0][1] = new Fraction("1/2");
        table2[0][2] = new Fraction("1/3");
        table2[1][0] = new Fraction("3/2");
        table2[1][1] = new Fraction("-1/2");
        table2[1][2] = new Fraction("2");
        table2[2][0] = new Fraction("0");
        table2[2][1] = new Fraction("-4");
        table2[2][2] = new Fraction("1/3");
        int[] colX2 = new int[2];
        colX2[0] = 3;
        colX2[1] = 4;
        int[] rowX2 = new int[2];
        rowX2[0] = 2;
        rowX2[1] = 1;
        SimplexTable simplexTable2 = new SimplexTable(n, m, func, table2, colX2, rowX2);

        simplexTable1.toMainTask();
        assertThat(simplexTable1).isEqualTo(simplexTable2);
    }

    @Test
    public void removeColumnTest(){
        int n1 = 2;
        int m1 = 2;
        Fraction[] func1 = new Fraction[n1 + 1];
        func1[0] = new Fraction("1/2");
        func1[1] = new Fraction("1");
        func1[2] = new Fraction("0");
        Fraction[][] table1 = new Fraction[m1 + 1][n1 + 1];
        table1[0][0] = new Fraction("-1");
        table1[0][1] = new Fraction("2");
        table1[0][2] = new Fraction("1");
        table1[1][0] = new Fraction("-1/2");
        table1[1][1] = new Fraction("2");
        table1[1][2] = new Fraction("1");
        table1[2][0] = new Fraction("-3/2");
        table1[2][1] = new Fraction("-4");
        table1[2][2] = new Fraction("0");
        int[] colX1 = new int[2];
        colX1[0] = 1;
        colX1[1] = 2;
        int[] rowX1 = new int[2];
        rowX1[0] = 3;
        rowX1[1] = 4;
        SimplexTable simplexTable1 = new SimplexTable(n1, m1, func1, table1, colX1, rowX1);

        int n2 = 2;
        int m2 = 2;
        Fraction[] func2 = new Fraction[n2 + 1];
        func2[0] = new Fraction("1/2");
        func2[1] = new Fraction("1");
        func2[2] = new Fraction("0");
        Fraction[][] table2 = new Fraction[m2 + 1][n2];
        table2[0][0] = new Fraction("-1");
        table2[0][1] = new Fraction("1");
        table2[1][0] = new Fraction("-1/2");
        table2[1][1] = new Fraction("1");
        table2[2][0] = new Fraction("-3/2");
        table2[2][1] = new Fraction("0");
        int[] colX2 = new int[1];
        colX2[0] = 1;
        int[] rowX2 = new int[2];
        rowX2[0] = 3;
        rowX2[1] = 4;
        SimplexTable simplexTable2 = new SimplexTable(n2 - 1, m2, func2, table2, colX2, rowX2);

        simplexTable1.removeCol(1);
        assertThat(simplexTable1).isEqualTo(simplexTable2);
    }
}
