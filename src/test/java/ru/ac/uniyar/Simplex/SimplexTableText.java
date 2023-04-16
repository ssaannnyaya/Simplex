package ru.ac.uniyar.Simplex;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class SimplexTableText {


    @Test
    public void normaliseTest(){
        int n = 2;
        int m = 2;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("1/2");
        func[1] = new Fraction("1");
        func[2] = new Fraction("0");
        Fraction[][] table = new Fraction[n + 1][m + 1];
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
        rowX[0] = -1;
        rowX[1] = -2;
        SimplexTable simplexTable = new SimplexTable(n, m, func, table, colX, rowX);
        simplexTable.normalize();
        assertThat(simplexTable.getTable()[1][0]).isEqualTo(new Fraction("-1/2"));
        assertThat(simplexTable.getTable()[1][1]).isEqualTo(new Fraction("-2"));
        assertThat(simplexTable.getTable()[1][2]).isEqualTo(new Fraction("1"));
        assertThat(simplexTable.getTable()[2][0]).isEqualTo(new Fraction("-1/2"));
        assertThat(simplexTable.getTable()[2][1]).isEqualTo(new Fraction("0"));
        assertThat(simplexTable.getTable()[2][2]).isEqualTo(new Fraction("-2"));
    }

}
