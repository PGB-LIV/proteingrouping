package group;

import pep.Peptide;
import prot.Protein;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * Creates an array of Group objects
 * 
 */
public class GroupArray {

    private ArrayList<Group> groups;

    public GroupArray() {

        groups = new ArrayList<>();
    }

    public void addGroup(Group gp) {
        groups.add(gp);
    }
    public Group getHeadPeps(Peptide pep) {
        Group gp = null;
        for (Group g : groups) {
            List<Peptide> list = g.getHeadProtPepList();
            for (Peptide p : list) {
                if (p.equals(pep))
                    gp = g;
            }
        }
        return gp;
    }
    public Group getGroup (Protein p) {
        Group gp = null;
        for (Group g : groups) {
            if (g.getGroupHead().equals(p)) {
                gp = g;
            }
        }
        return gp;
    }
    public int getSize() {
        return groups.size();
    }
    public Group getGroup(int index) {
        Group tempGroup = null;
        for (int i = 0; i < groups.size(); i++) {
            tempGroup = groups.get(index);
        }
        return tempGroup;
    }
}