import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DFAutomaton {
    private NDFAutomaton ndf;
    private HashMap<Integer, ArrayList<Integer>> transitionsMap = new HashMap<>();
    private ArrayList<Integer>[][] transitionTable;
    private ArrayList<Integer> finals = new ArrayList<>();
    private static int cpt = 1;

    public DFAutomaton(NDFAutomaton ndf) {
        this.ndf = ndf;
        this.transitionTable = new ArrayList[1][this.ndf.getTransitionTable()[0].length];
        for (int i = 0; i < this.transitionTable.length; i++) {
            for (int j = 0; j < this.transitionTable[0].length; j++) {
                this.transitionTable[i][j] = new ArrayList<>();
            }
        }
        determine();
        System.out.println(" >> DFA construction:\n\nBEGIN DFA\n" + this);
        minimize();
        System.out.println(" >> DFA minimization:\n" + this);
        System.out.println("END DFA.\n");
    }

    private void enlargeTransitionTable() {
        ArrayList<Integer>[][] newTransitionTable = new ArrayList[this.transitionTable.length
                * 2][this.transitionTable[0].length];
        for (int i = 0; i < this.transitionTable.length; i++) {
            for (int j = 0; j < this.transitionTable[i].length; j++) {
                newTransitionTable[i][j] = this.transitionTable[i][j];
            }
        }

        for (int i = this.transitionTable.length; i < newTransitionTable.length; i++) {
            for (int j = 0; j < newTransitionTable[i].length; j++) {
                newTransitionTable[i][j] = new ArrayList<>();
            }
        }
        this.transitionTable = newTransitionTable;
    }

    private void determine() {
        determine_step1();
        determine_step2();
        determine_step3();
    }

    private void determine_step1() {
        ArrayList<Integer> epsilons = new ArrayList<>();
        epsilons.add(0);
        epsilons.addAll(getEpsilons(0));
        for (Integer eps : epsilons) {
            for (int i = 0; i < this.ndf.getTransitionTable()[eps].length; i++) {
                if (this.ndf.getTransitionTable()[eps][i] != -1) {
                    this.transitionTable[0][i].add(this.ndf.getTransitionTable()[eps][i]);
                    this.transitionTable[0][i].addAll(getEpsilons(this.ndf.getTransitionTable()[eps][i]));
                }
                Boolean notToPut = false;
                for (ArrayList<Integer> value : this.transitionsMap.values()) {
                    ArrayList<Integer> removed = new ArrayList<>();
                    removed.addAll(value);
                    removed.removeAll(this.transitionTable[0][i]);
                    if (value.containsAll(this.transitionTable[0][i]) && removed.size() == 0) {
                        notToPut = true;
                        break;
                    }
                }

                if (!this.transitionTable[0][i].isEmpty() && !notToPut) {
                    this.transitionsMap.put(cpt, this.transitionTable[0][i]);
                    cpt++;
                }
            }
        }
        this.transitionsMap.put(0, epsilons);
    }

    private void determine_step2() {
        int cptWhere = 1;
        while (cptWhere < cpt) {
            ArrayList<Integer> epsilons = new ArrayList<>();
            for (int n : this.transitionsMap.get(cptWhere)) {
                if (!epsilons.contains(n))
                    epsilons.add(n);
                ArrayList<Integer> resultGetEpsilons = getEpsilons(n);
                epsilons.removeAll(resultGetEpsilons); // This line avoid duplicates.
                epsilons.addAll(resultGetEpsilons);
            }

            if (this.transitionTable.length < cptWhere + 1)
                enlargeTransitionTable();

            for (Integer eps : epsilons) {
                for (int i = 0; i < this.ndf.getTransitionTable()[eps].length; i++) {
                    if (this.ndf.getTransitionTable()[eps][i] != -1) {
                        this.transitionTable[cptWhere][i].add(this.ndf.getTransitionTable()[eps][i]);
                        this.transitionTable[cptWhere][i].addAll(getEpsilons(this.ndf.getTransitionTable()[eps][i]));
                    }

                    Boolean notToPut = false;
                    for (ArrayList<Integer> value : this.transitionsMap.values()) {
                        ArrayList<Integer> removed = new ArrayList<>();
                        removed.addAll(value);
                        removed.removeAll(this.transitionTable[cptWhere][i]);
                        if (value.containsAll(this.transitionTable[cptWhere][i]) && removed.size() == 0) {
                            notToPut = true;
                            break;
                        }
                    }

                    if (!this.transitionTable[cptWhere][i].isEmpty()
                            && !this.transitionsMap.values().contains(this.transitionTable[cptWhere][i]) && !notToPut) {
                        this.transitionsMap.put(cpt, this.transitionTable[cptWhere][i]);
                        cpt++;
                    }
                }
            }
            cptWhere++;
        }

    }

    private void determine_step3() {
        int i = 0;
        for (ArrayList<Integer> transition : this.transitionsMap.values()) {
            if (transition.contains(this.ndf.getTransitionTable().length - 1))
                this.finals.add(i, 1);
            else
                this.finals.add(i, 0);
            i++;
        }
    }

    private void minimize() {

        // séparer états finaux de non finaux
        HashMap<Integer, ArrayList<Integer>> sets = new HashMap<>();
        ArrayList<Integer> finals = new ArrayList<>();
        ArrayList<Integer> others = new ArrayList<>();
        int cpt = 0;

        for (Integer key : this.transitionsMap.keySet()) {
            if (this.finals.get(key) == 1)
                finals.add(key);
            else
                others.add(key);
        }

        sets.put(0, finals);
        sets.put(1, others);

        int size = (new ArrayList<>(sets.keySet())).size();

        while (cpt < size) {
            if (sets.get(cpt).size() > 1) {
                ArrayList<ArrayList<Integer>> newSet = new ArrayList<>();
                for (int i = 0; i < sets.get(cpt).size() - 1; i++) {
                    ArrayList<Integer> set = new ArrayList<>();
                    for (int j = i; j < sets.get(cpt).size(); j++) {
                        Boolean equivalent = true;
                        for (int k = 0; k < this.transitionTable[0].length; k++) {
                            Integer key1 = this.getKeyFromValues(this.transitionTable[sets.get(cpt).get(i)][k]);
                            Integer key2 = this.getKeyFromValues(this.transitionTable[sets.get(cpt).get(j)][k]);
                            if (key1 != key2) {
                                for (Integer key : sets.keySet()) {
                                    if ((sets.get(key).contains(key1) && !sets.get(key).contains(key2))
                                            || (!sets.get(key).contains(key1) && sets.get(key).contains(key2))) {
                                        equivalent = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (equivalent) {
                            if (!set.contains(sets.get(cpt).get(i)))
                                set.add(sets.get(cpt).get(i));
                            if (!set.contains(sets.get(cpt).get(j)))
                                set.add(sets.get(cpt).get(j));
                        }
                    }
                    newSet.add(set);
                    if (!set.contains(sets.get(cpt).get(sets.get(cpt).size() - 1))) {
                        ArrayList<Integer> array = new ArrayList<>();
                        array.add(sets.get(cpt).get(sets.get(cpt).size() - 1));
                        newSet.add(array);
                    }
                }
                for (int i = newSet.size() - 1; i > 0; i--) {
                    for (int j = i - 1; j > -1; j--) {
                        if (newSet.get(j).containsAll(newSet.get(i))) {
                            newSet.remove(i);
                            break;
                        }
                    }
                }
                Boolean toAdd = true;
                for (ArrayList<Integer> set : newSet) {
                    for (ArrayList<Integer> value : sets.values()) {
                        if (set.equals(value))
                            toAdd = false;
                    }
                    if (toAdd) {
                        sets.put(sets.size(), set);
                    }
                }
                if (toAdd)
                    sets.remove(cpt);
            }
            cpt++;
            size = (new ArrayList<>(sets.keySet())).size();
        }

        for (ArrayList<Integer> set : sets.values()) {
            Collections.sort(set);
            if (set.size() > 1) {
                ArrayList<Integer> newTransitions = new ArrayList<>();
                newTransitions.addAll(this.transitionsMap.get(set.get(0)));
                for (int i = 1; i < set.size(); i++) {
                    if (!this.transitionsMap.get(set.get(i)).isEmpty()) {
                        for (Integer t : this.transitionsMap.get(set.get(i))) {
                            if (!newTransitions.contains(t)) {
                                newTransitions.add(t);
                            }
                        }
                    }

                    for (int j = 0; j < this.transitionTable.length; j++) {
                        for (int k = 0; k < this.transitionTable[0].length; k++) {
                            if (this.transitionTable[j][k].equals(this.transitionsMap.get(set.get(i)))
                                    || this.transitionTable[j][k].equals(this.transitionsMap.get(set.get(0)))) {
                                this.transitionTable[j][k] = newTransitions;
                            }
                        }
                    }

                    for (int j = 0; j < this.transitionTable[0].length; j++)
                        this.transitionTable[set.get(i)][j] = new ArrayList<>();

                    this.transitionsMap.remove(set.get(i));
                    this.transitionsMap.put(set.get(0), newTransitions);
                }
            }
        }
    }

    public int getKeyFromValues(ArrayList<Integer> values) {
        for (Integer key : this.transitionsMap.keySet()) {
            ArrayList<Integer> removed = new ArrayList<>();
            removed.addAll(this.transitionsMap.get(key));
            removed.removeAll(values);
            if (this.transitionsMap.get(key).containsAll(values) && removed.isEmpty())
                return key;
        }
        return -1;
    }

    private ArrayList<Integer> getEpsilons(int n) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        ArrayList<Integer> resultTmp = new ArrayList<Integer>();
        if (this.ndf.getEpsilonTransitionTable()[n].size() != 0) {
            for (Integer eps : this.ndf.getEpsilonTransitionTable()[n]) {
                if (!result.contains(eps))
                    result.add(eps);
            }
            for (int e : result)
                for (Integer eps : getEpsilons(e))
                    resultTmp.add(eps);
        }
        result.addAll(resultTmp);
        return result;
    }

    public ArrayList<Integer>[][] getTransitionTable() {
        return this.transitionTable;
    }

    public ArrayList<Integer> getFinals() {
        return this.finals;
    }

    public String toString() {

        String str = "{\n  0 : " + this.transitionsMap.get(0) + " : S";
        if (this.finals.get(0) == 1)
            str += " | F";
        for (Integer key : this.transitionsMap.keySet()) {
            if (key == 0)
                continue;
            str += "\n  " + key.toString() + " : " + this.transitionsMap.get(key);
            if (this.finals.get(key) == 1)
                str += " : F";
        }

        str += "\n}\n\nTransition list:\n";
        for (int i = 0; i < this.transitionTable.length; i++) {
            for (int j = 0; j < this.transitionTable[i].length; j++) {
                if (!this.transitionTable[i][j].isEmpty())
                    str += i + " -- " + (char) j + " --> " + getKeyFromValues(this.transitionTable[i][j]) + "\n";
            }
        }
        return str;
    }
}
