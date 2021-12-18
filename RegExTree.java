import java.util.ArrayList;

public class RegExTree {
    private int root;
    private ArrayList<RegExTree> subTrees;

    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    public String toString() {
        if (this.subTrees.isEmpty())
            return rootToString();
        String str = rootToString() + "(" + this.subTrees.get(0).toString();
        for (int i = 1; i < this.subTrees.size(); i++)
            str += "," + this.subTrees.get(i).toString();
        return str + ")";
    }

    private String rootToString() {
        if (this.root == Parser.CONCAT || this.root == Parser.DOT)
            return ".";
        if (this.root == Parser.ETOILE)
            return "*";
        if (this.root == Parser.ALTERN)
            return "|";
        return Character.toString((char) this.root);
    }

    public int getRoot() {
        return this.root;
    }

    public ArrayList<RegExTree> getSubTrees() {
        return this.subTrees;
    }
}
