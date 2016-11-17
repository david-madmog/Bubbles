package bubbles;

import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author davidp
 */
public class BubblesGrid {
    public int [][] Cells  ;

    public enum FallStyle {
       Normal ,
       Refil,
       FallLeft,
       Neverending
    } ;


    BubblesGrid(int xs, int ys)
    {
        Cells = new int[xs][ys] ;
    }

    int width() {
        return Cells.length ;
    }

    int height() {
        return Cells[0].length ;
    }

    void Randomise() {
        Random R = new Random() ;

        for (int i = 0; i< width(); i++) {
            for (int j = 0; j< height(); j++) {
                Cells[i][j] = R.nextInt(5) + 1;
            }
        }
    }

    int GetScore(int x, int y) {
        int Total = 0 ;
        if ((x<0)||(y<0)||(x>=width())||(y>=height()))
            return 0 ;
         
        int me = 0;
        me = Cells[x][y] ;

        if (me != 0) {
            Total = GetScoreRecurse(x, y) ;
        }
        // Clear this one, so we don't count it again
        for (int i = 0; i< width(); i++) {
            for (int j = 0; j< height(); j++) {
                if (Cells[i][j] == -1)
                    Cells[i][j] = me ;
            }
        }

        if (Total == 1) Total = 0 ;
        
        return Total * Total ;
    }

    int ActionScore(int x, int y) {
        int Total = 0 ;
        if ((x<0)||(y<0)||(x>=width())||(y>=width()))
            return 0 ;

        int me = Cells[x][y] ;
        if (me != 0) {
            Total = GetScoreRecurse(x, y) ;
        }

        if (Total > 1) {
            // Clear this one, so we don't count it again
            for (int i = 0; i< width(); i++) {
                for (int j = 0; j< height(); j++) {
                    if (Cells[i][j] == -1)
                        Cells[i][j] = 0 ;
                }
            }
        } else {
            Cells[x][y] = me ;
        }
        if (Total == 1) Total = 0 ;
        return Total * Total ;
    }

    int ActionFall(FallStyle FS) {
        boolean bOneFell ;
        Random R = new Random() ;

        // Fall down towards bottom
        for (int i = 0; i< width(); i++) {
            bOneFell = true ;
            while (bOneFell) {
                bOneFell = false ;
                for (int j = height() - 1; j > 0 ; j--) {
                    if (Cells[i][j] == 0) {
                        for (int k = j ; k > 0 ; k--) {
                            Cells[i][k] = Cells[i][k-1] ;
                            if (Cells[i][k] != 0)
                                bOneFell = true ;
                        }
                        Cells[i][0] = 0 ;
                    }
                }
            }
        }

        // Empty columns of the grid
        bOneFell = true;
        while (bOneFell) {
            bOneFell = false ;
            for (int i = 0; i< width(); i++) {
                if (isColumnEmpty(i)) {
                    // Empty in the middle of the grid
                    for (int j=i; j<width()-1; j++) {
                        System.arraycopy(Cells[j + 1], 0, Cells[j], 0, height());
                        if (! isColumnEmpty(j))
                            bOneFell = true ;
                    }
                    // Clear out the end
                    for (int j=0; j<height(); j++ ) {
                        Cells[width()-1][j] = 0 ;
                    }
                    // And build a new one... if needed
                    if ((FS == FallStyle.Neverending) || (FS == FallStyle.Refil)) {
                        int H = R.nextInt(height()) ;
                        for (int j=H; j<height(); j++ ) {
                            Cells[width()-1][j] = R.nextInt(5) + 1;
                        }
                    }
                }
            }
        }

        // And fall left...
        if ((FS == FallStyle.FallLeft) || (FS == FallStyle.Neverending)) {
            for (int i = width() - 1;  i>=0 ; i--) {
                for (int j = height()-1 ; j >= 0 ; j--) {
                    if (Cells[i][j] == 0) {
                        for (int k = i ; k < width()-1 ; k++) {
                            Cells[k][j] = Cells[k+1][j] ;
                            Cells[k+1][j] = 0 ;
                        }
                    }
                }
            }
        }
        return 0 ;
    }

    private int GetScoreRecurse(int x, int y) {
        int Total = 1 ;

        int me = Cells[x][y] ;
        // Clear this one, so we don't count it again
        Cells[x][y] = -1 ;

        if (x > 0) {
            // Check Left
            if (Cells[x-1][y] == me)
                Total += GetScoreRecurse(x-1, y) ;
        }

        if (x < width()-1) {
            // Check Left
            if (Cells[x+1][y] == me)
                Total += GetScoreRecurse(x+1, y) ;
        }

        if (y > 0) {
            // Check Left
            if (Cells[x][y-1] == me)
                Total += GetScoreRecurse(x, y-1) ;
        }

        if (y < height()-1) {
            // Check Left
            if (Cells[x][y+1] == me)
                Total += GetScoreRecurse(x, y+1) ;
        }

        return Total ;
    }

    private boolean isColumnEmpty(int i) {
        for (int j = height() - 1; j > 0 ; j--) {
            if (Cells[i][j] > 0)
                return false ;
        }
        return true ;
    }

    boolean isAnotherMove() {
        for (int i = 0; i< width()-1; i++) {
            for (int j = 0; j< height(); j++) {
                if ((Cells[i][j] == Cells[i+1][j])&&Cells[i][j]!= 0)
                    return true ;
            }
        }
        for (int i = 0; i< width(); i++) {
            for (int j = 0; j< height()-1; j++) {
                if ((Cells[i][j] == Cells[i][j+1])&&Cells[i][j]!= 0)
                    return true ;
            }
        }
        return false ;
    }

    void CopyFrom(BubblesGrid other) {
        for (int i = 0; i< width(); i++) {
            System.arraycopy(other.Cells[i], 0, Cells[i], 0, height());
        }
    }

}
