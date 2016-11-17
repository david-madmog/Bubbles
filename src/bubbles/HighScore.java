/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bubbles;

import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author davidp
 */
public class HighScore {

        public class HighScoreEntry
            implements Comparator<HighScoreEntry>
        {
            public int Score ;
            public int xs, ys ;
            public int mode ;
            public String Name = "" ;
            public Date When ;

            public void Save(Preferences prefs, int i) {
                String sPath =  Size() ;
                Preferences SizeNode = prefs.node(sPath) ;
                SizeNode.putInt("Score" + Integer.toString(i), Score);
                SizeNode.putLong("When"+ Integer.toString(i), When.getTime()) ;
                SizeNode.put("Name" + Integer.toString(i), Name);
            }

            public void Load(Preferences prefs, int j) {
                Score = prefs.getInt("Score" + Integer.toString(j), 0);
                Name = prefs.get("Name" + Integer.toString(j), "");
                When  = new Date(prefs.getLong("When" + Integer.toString(j), 0)) ;

                // We have the size of the grid stored in the key, we need to parse it to x and y
                String N = prefs.name() ;
                int k = N.indexOf("-")  ;
                int i = N.indexOf("x")  ;
                mode = Integer.parseInt(N.substring(0, k)) ;
                xs = Integer.parseInt(N.substring(k+1, i)) ;
                ys = Integer.parseInt(N.substring(i+1)) ;
            }

            // Utility function to convert the grid size to a string
            private String Size() {
                return Integer.toString(mode) + "-" + Integer.toString(xs) + "x" + Integer.toString(ys) ;
            }

            // Utility to convert the grid size into a unique key for indexing into an array
        // Comparitor needed to allow sorting. Note: backwards, so sorts descending
        public int compare(HighScoreEntry o1, HighScoreEntry o2) {
            if (o1.Score < o2.Score)
                return 1 ;
            else if (o1.Score > o2.Score)
                return -1 ;
            else
                return 0 ;
        }
    }

    public static int SizeKey(int xs, int ys, int mode) {
        return( xs/8 + (ys/2) - 4)* 4 + mode ;
    }



    public final int MaxEntries = 20 ;

    public HighScoreEntry [][] HighScores ; // First index: mode & grid size, second index: rank
    public int UsedEntries []; // index: grid size. How much of the main array is used

    public HighScore() {
        // magic number 65 represents 64 grid sizes and one dustbin
        HighScores = new HighScoreEntry[65][MaxEntries] ;
        UsedEntries = new int[65] ;
        Load() ;
    }

    public int AddEntry(HighScoreEntry HSE, boolean bSave) {
        int InsertPos = -1 ;
        if (UsedEntries[SizeKey(HSE.xs, HSE.ys, HSE.mode)] < MaxEntries) {
            // free slots - just shove it in at the end
            InsertPos = UsedEntries[SizeKey(HSE.xs, HSE.ys, HSE.mode)]++ ;
        } else if (HSE.Score > HighScores[SizeKey(HSE.xs, HSE.ys, HSE.mode)][UsedEntries[SizeKey(HSE.xs, HSE.ys, HSE.mode)]-1].Score ) {
            // array is full - but we're better than the worst, so dump it in place of the worst
            InsertPos = UsedEntries[SizeKey(HSE.xs, HSE.ys, HSE.mode)]-1 ;
        }

        if (InsertPos >= 0) {
            // We've added a new entry - always at the end, so sort.
            HighScores[SizeKey(HSE.xs, HSE.ys, HSE.mode)][InsertPos] = HSE ;
            Sort(SizeKey(HSE.xs, HSE.ys, HSE.mode)) ;
            if (bSave)
                Save() ;
        }

        return LocateHSPosition(SizeKey(HSE.xs, HSE.ys, HSE.mode), HSE.Score) ;
    }

    public void Save() {
        Preferences prefs = Preferences.userNodeForPackage(BubblesView.class);
        for (int j = 0; j<= 64; j++) {
            for (int i = 0; i< HighScores[j].length; i++) {
                if (HighScores[j][i] != null)
                    HighScores[j][i].Save(prefs, i) ;
            }
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(HighScore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void Load() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(BubblesView.class);
            String[] Children = prefs.childrenNames();
            for (int i = 0; i<Children.length; i++) {
                // Loop of sizes
                Preferences SizeNode = prefs.node(Children[i]) ;
                for (int j=0; j< MaxEntries; j++) {
                    // Loop of score positions
                    if (!SizeNode.get("Score" + Integer.toString(j), "xyzzy").equals("xyzzy")) {
                        // score position exists in registry... so load it.
                        HighScoreEntry HSE = new HighScoreEntry() ;
                        HSE.Load(SizeNode, j) ;
                        AddEntry(HSE, false);
                    }
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(HighScore.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void Sort(int Key) {
        HighScoreEntry HSE = new HighScoreEntry() ;
        java.util.Arrays.sort(HighScores[Key], 0, UsedEntries[Key], HSE);
    }

    int LocateHSPosition(int key, int Score) {
        int Pos = -1 ;
        for (int i=0; i< UsedEntries[key]; i++) {
            if (HighScores[key][i].Score == Score) {
                Pos = i ;
            }
        }
        return Pos ;
    }

}
