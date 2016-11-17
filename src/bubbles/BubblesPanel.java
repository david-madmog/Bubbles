/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bubbles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.Timer;

/**
 *
 * @author davidp
 */
public class BubblesPanel extends javax.swing.JPanel {
    public BubblesGrid Cells  ;
    public int CX = 0, CY = 0;
    public int DrawCellSize = 1;
    public boolean HSMode = false ;
    public HighScore HS ;
    public BubblesGrid.FallStyle FS ;

    private double PointsToPixels = 0;
    private Font HighScoreFont ;

    public class PopScore {
        int Counter = 0 ;
        Point Point ;
        int Score = 0 ;
        Timer Timer ;
        Font Font ;
        
        PopScore() {
            Font = new Font("Arial", Font.PLAIN, 20) ;            
        }
    } ;

    public PopScore Pop  ;

    public BubblesPanel(){
        
    }

 @Override
    protected void paintComponent(Graphics g)  
    {
//        Font F = new Font("Arial", Font.PLAIN, 20) ;
//        g.setFont(F) ;
//        g.setColor(Color.WHITE) ;
//        g.drawString("Hello World", 10, 10);

        Rectangle VisRect = new Rectangle() ;
        this.computeVisibleRect(VisRect);

        if (HSMode) {
            DrawHighScores(g);
        } else {
            g.setColor(Color.black) ;
            g.fillRect(VisRect.getLocation().x, VisRect.getLocation().y, VisRect.getSize().width, VisRect.getSize().height);
            int sx = VisRect.getSize().width / Cells.width()  ;
            int sy = VisRect.getSize().height / Cells.height() ;
            int sxy = Math.min(sx, sy) ;
            DrawCellSize = sxy ;

            for (int i = 0; i<  Cells.width() ; i++) {
                for (int j = 0; j< Cells.height() ; j++) {
                    DrawCell(g, i*sxy, j*sxy, sxy, Cells.Cells[i][j]) ;
                    if ((i==CX)&&(j==CY))
                    {
                        g.setColor(Color.DARK_GRAY) ;
                        g.drawRoundRect(i*sxy, j*sxy, sxy - 1, sxy - 1, sxy/5, sxy/5) ;
                    }
                }
            }

            if (Pop != null ) {
                if (Pop.Counter > 0)
                    if (Pop.Point != null) {
                        float a = (float)Pop.Counter / 30 ;
                        Color NewColor = new Color(1, 1, 1, a);
                        g.setColor(NewColor);
                        g.drawString(Integer.toString(Pop.Score), Pop.Point.x, Pop.Point.y);
                    }
            }
        }
    }

    private void DrawCell(Graphics g, int x, int y, int dxy, int c) {
        Color baseColor ;
        switch (c) {
            case 0:
                baseColor = Color.black ; break ;
            case 1:
                baseColor = Color.red ; break ;
            case 2:
                baseColor = Color.green ; break ;
            case 3:
                baseColor = Color.blue ; break ;
            case 4:
                baseColor = Color.yellow ; break ;
            case 5:
                baseColor = Color.MAGENTA ; break ;
            default:
                baseColor = Color.white ; break ;
        }

        if (c != 0) {
            for (int i=dxy; i>0; i--) {
                g.setColor(ScaleColor(baseColor, i, dxy));
                g.fillOval(x + ((dxy-i)/3), y + ((dxy-i)/3), i, i);
            }
        }
        
    }

    private Color ScaleColor(Color BaseColor, int index, int Max) {
        float[] hsb = new float[3] ;
        Color.RGBtoHSB(BaseColor.getRed(), BaseColor.getGreen(), BaseColor.getBlue(), hsb);
        //hsb[1] = hsb[1] * index / Max ;
        hsb[2] = (float)0.25 + (hsb[2] * 3 * (Max - index) / (4 * Max)) ;
        Color NewColor ;
        NewColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]) ;
        return NewColor ;
    }

    private void DrawHighScores(Graphics g) {
        Rectangle VisRect = new Rectangle() ;
        this.computeVisibleRect(VisRect);
        String S ;

        if (PointsToPixels == 0)
            PointsToPixels = GetFontRatio(g) ;

        g.setColor(Color.lightGray) ;
        g.fillRect(0, 0, VisRect.width, VisRect.height) ;

        int ReqFontSize = (int)(VisRect.height /(PointsToPixels * (HS.MaxEntries + 2.0))) ;

        if (HighScoreFont == null) {
            HighScoreFont= new Font("Helvetica", Font.PLAIN, ReqFontSize) ;
        } else if (HighScoreFont.getSize() != ReqFontSize) {
            HighScoreFont= new Font("Helvetica", Font.PLAIN, ReqFontSize) ;
        }
        g.setFont(HighScoreFont) ;

        S = "High Scores:" ;
        g.setColor(Color.WHITE) ;
        g.drawString( S, 6,  g.getFontMetrics().getHeight()+1 );
        g.setColor(Color.BLACK) ;
        g.drawString( S, 5,  g.getFontMetrics().getHeight() );
        for (int i = 0; i<HS.UsedEntries[HighScore.SizeKey(Cells.width(), Cells.height(), FS.ordinal())]; i++)
        {
            S = String.format("  %2d. %6d    %3$tR %3$te %3$tb %3$tY",
                    i + 1,
                    HS.HighScores[HighScore.SizeKey(Cells.width(), Cells.height(), FS.ordinal())][i].Score,
                    HS.HighScores[HighScore.SizeKey(Cells.width(), Cells.height(), FS.ordinal())][i].When) ;
            g.setColor(Color.WHITE) ;
            g.drawString( S, 6, ((i+2)* g.getFontMetrics().getHeight())+1 );
            g.setColor(Color.BLACK) ;
            g.drawString( S, 5, ((i+2)* g.getFontMetrics().getHeight()) );
        }
   }

   private double GetFontRatio(Graphics g) {
        Font MeasureFont = new Font("Helvetica", Font.PLAIN, 100) ;
        return (g.getFontMetrics(MeasureFont).getHeight()  / 100.0) ;
   }
}
