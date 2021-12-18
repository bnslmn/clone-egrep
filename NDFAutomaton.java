import java.util.ArrayList;

public class NDFAutomaton {
    private RegExTree ret;
    private int[][] transitionTable;
    private ArrayList<Integer>[] epsilonTransitionTable;

    public NDFAutomaton(RegExTree ret) {
        this.ret = ret;
        step2_AhoUllman();
    }

    public int[][] getTransitionTable() {
        return this.transitionTable;
    }

    public ArrayList<Integer>[] getEpsilonTransitionTable() {
        return epsilonTransitionTable;
    }

    private void step2_AhoUllman() {
        if (ret.getSubTrees().isEmpty()) {
            int[][] tTab = new int[2][256];
            ArrayList<Integer>[] eTab = new ArrayList[2];

            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();

            if (ret.getRoot() != Parser.DOT)
                tTab[0][ret.getRoot()] = 1;
            else
                for (int i = 0; i < 256; i++)
                    tTab[0][i] = 1;

            this.transitionTable = tTab;
            this.epsilonTransitionTable = eTab;

            return;
        }

        if (ret.getRoot() == Parser.CONCAT) {
            NDFAutomaton gauche = new NDFAutomaton(ret.getSubTrees().get(0));
            int[][] tTab_g = gauche.getTransitionTable();
            ArrayList<Integer>[] eTab_g = gauche.getEpsilonTransitionTable();
            NDFAutomaton droite = new NDFAutomaton(ret.getSubTrees().get(1));
            int[][] tTab_d = droite.getTransitionTable();
            ArrayList<Integer>[] eTab_d = droite.getEpsilonTransitionTable();
            int lg = tTab_g.length;
            int ld = tTab_d.length;
            int[][] tTab = new int[lg + ld][256];
            ArrayList<Integer>[] eTab = new ArrayList[lg + ld];

            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();

            eTab[lg - 1].add(lg);

            for (int i = 0; i < lg; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = tTab_g[i][col];
            for (int i = 0; i < lg; i++)
                eTab[i].addAll(eTab_g[i]);
            for (int i = lg; i < lg + ld - 1; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_d[i - lg][col] != -1)
                        tTab[i][col] = tTab_d[i - lg][col] + lg;
            for (int i = lg; i < lg + ld - 1; i++)
                for (int s : eTab_d[i - lg])
                    eTab[i].add(s + lg);

            this.transitionTable = tTab;
            this.epsilonTransitionTable = eTab;

            return;
        }

        if (ret.getRoot() == Parser.ALTERN) {
            NDFAutomaton gauche = new NDFAutomaton(ret.getSubTrees().get(0));
            int[][] tTab_g = gauche.getTransitionTable();
            ArrayList<Integer>[] eTab_g = gauche.getEpsilonTransitionTable();
            NDFAutomaton droite = new NDFAutomaton(ret.getSubTrees().get(1));
            int[][] tTab_d = droite.getTransitionTable();
            ArrayList<Integer>[] eTab_d = droite.getEpsilonTransitionTable();
            int lg = tTab_g.length;
            int ld = tTab_d.length;
            int[][] tTab = new int[2 + lg + ld][256];
            ArrayList<Integer>[] eTab = new ArrayList[2 + lg + ld];

            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();

            eTab[0].add(1);
            eTab[0].add(1 + lg);
            eTab[1 + lg - 1].add(2 + lg + ld - 1);
            eTab[1 + lg + ld - 1].add(2 + lg + ld - 1);

            for (int i = 1; i < 1 + lg; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_g[i - 1][col] != -1)
                        tTab[i][col] = tTab_g[i - 1][col] + 1;
            for (int i = 1; i < 1 + lg; i++)
                for (int s : eTab_g[i - 1])
                    eTab[i].add(s + 1);
            for (int i = 1 + lg; i < 1 + lg + ld - 1; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_d[i - 1 - lg][col] != -1)
                        tTab[i][col] = tTab_d[i - 1 - lg][col] + 1 + lg; // copy old transitions
            for (int i = 1 + lg; i < 1 + lg + ld; i++)
                for (int s : eTab_d[i - 1 - lg])
                    eTab[i].add(s + 1 + lg);

            this.transitionTable = tTab;
            this.epsilonTransitionTable = eTab;

            return;
        }

        if (ret.getRoot() == Parser.ETOILE) {
            NDFAutomaton fils = new NDFAutomaton(ret.getSubTrees().get(0));
            int[][] tTab_fils = fils.getTransitionTable();
            ArrayList<Integer>[] eTab_fils = fils.getEpsilonTransitionTable();
            int l = tTab_fils.length;
            int[][] tTab = new int[2 + l][256];
            ArrayList<Integer>[] eTab = new ArrayList[2 + l];

            for (int i = 0; i < tTab.length; i++)
                for (int col = 0; col < 256; col++)
                    tTab[i][col] = -1;
            for (int i = 0; i < eTab.length; i++)
                eTab[i] = new ArrayList<Integer>();

            eTab[0].add(1);
            eTab[0].add(2 + l - 1);
            eTab[2 + l - 2].add(2 + l - 1);
            eTab[2 + l - 2].add(1);

            for (int i = 1; i < 2 + l - 1; i++)
                for (int col = 0; col < 256; col++)
                    if (tTab_fils[i - 1][col] != -1)
                        tTab[i][col] = tTab_fils[i - 1][col] + 1;
            for (int i = 1; i < 2 + l - 1; i++)
                for (int s : eTab_fils[i - 1])
                    eTab[i].add(s + 1);

            this.transitionTable = tTab;
            this.epsilonTransitionTable = eTab;

            return;
        }
    }

    public String toString() {
        String str = "Initial state: 0\nFinal state: " + (this.transitionTable.length - 1) + "\nTransition list:\n";
        for (int i = 0; i < this.epsilonTransitionTable.length; i++)
            for (int state : this.epsilonTransitionTable[i])
                str += "  " + i + " -- epsilon --> " + state + "\n";
        for (int i = 0; i < this.transitionTable.length; i++)
            for (int col = 0; col < 256; col++)
                if (this.transitionTable[i][col] != -1)
                    str += "  " + i + " -- " + (char) col + " --> " + this.transitionTable[i][col] + "\n";
        return str;
    }
}
