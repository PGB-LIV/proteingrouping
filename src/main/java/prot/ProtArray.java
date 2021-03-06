package prot;

import pep.PepArray;
import pep.Peptide;
import group.Group;
import group.GroupArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

/**
 * Creates an array of Protein objects.
 */
public class ProtArray {

    private ArrayList<Protein> proteins;

    /**
     *
     */
    public ProtArray() {

        proteins = new <Protein>ArrayList(); 
    }

    /**
     *
     * @param input
     */
    public void buildProtArrayFromProgenesisPepIon(List<String> input) {
        Protein tempProt = null;
        String[] pepProperties = null;
        String splitBy = ",";
        String prot = "";
        
        for (String line : input) {
            pepProperties = line.split(splitBy);
            prot = pepProperties[10];
            tempProt = checkProt(prot);
        }
    }
    /**
     *
     */
    public void discardNonIdent() {
        Iterator<Protein> iter = proteins.iterator();
        while (iter.hasNext()) {
            Protein p = iter.next();
            if (p.getPepNo() == 0) {
                iter.remove();
                //System.out.println(p.getProtName());
            }
        }
    }
    /**
     *
     */
    public void orderProtsByPepCount() {        
        // Sort proteins by number of peptides mapped to them
        // Descending
        Collections.sort(proteins,
            (protein1, protein2) -> protein2.getPepNo()
                    - protein1.getPepNo());
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            
            Collections.sort(peps,
                    (pep1, pep2) -> pep1.getProtNo()
                    - pep2.getProtNo());
        }
    }

    /**
     *
     * @param pg
     */
    public void setDistinctProts(GroupArray pg) {
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if (pep.isUnique && !p.isDistinct) {
                    p.makeDistinct();
                    Group gp = new Group(p);
                    pg.addGroup(gp);
                    for (Peptide pp : peps) {
                        pp.makeClaimed();
                        pp.fromDistinct();
                    }
                }
            }
        }
    }

    /**
     *
     * @param pg
     */
    public void setSameSetProts(GroupArray pg) {
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if (!pep.isClaimed && !p.isAssigned) { 
                    p.makeSameSet();
                    Group gp = new Group(p);
                    pg.addGroup(gp);
                    for (Peptide pp : peps) {
                        pp.makeSameSet();
                        pp.makeClaimed();
                        if (!pp.fromDistinct) {
                            pp.makeResolved();
                        }
                    }
                }                
                if (pep.fromSameSet && !p.isAssigned) {
                    //Find the group head
                    Group gp = pg.getHeadPeps(pep);
                    if (gp.samePeps(peps)) {
                        gp.addProtToGroup(p);
                    }    
                    p.makeSubSet();
                    for (Peptide pp : peps) {
                        pp.makeClaimed();
                        pp.makeSubSet();
                    }
                }
            }
        }
    }

    /**
     *
     * @param pg
     */
    public void setMutSubProts(GroupArray pg) {
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if (pep.isClaimed && !p.isAssigned) {
                    List<Group> gps = new ArrayList<>();
                    for (Peptide pp : peps) {
                        pp.makeMutSub();
                        Group g = pg.getHeadPeps(pp);
                        gps.add(g);
                        //System.out.println(g.getGroupHead().getProtName());
                        //if(gps != null) {
                        for (Group gp : gps) {
                            gp.addProtToGroup(p);
                        }
                        //}
                    }
                    p.makeMutSub();
                }
            }
        }
    }

    /**
     *
     * @param pg
     */
    public void discardProts(GroupArray pg) {
        Group gp = null;
        for (Protein p: proteins) {
            if (p.isSameSet) {
                int headPepNo = p.getPepNo();
                gp = pg.getGroup(p);
                if (gp != null) {
                    List<Protein> prots = gp.getProtGroupList();
                    for (Protein pr : prots) {
                        List<Peptide> peps = pr.getPepList();
                        if (headPepNo > pr.getPepNo()) {
                            pr.makeDiscarded();
                            for (Peptide pep : peps) {
                                pep.unClaim();
                                pep.makeResolved();
                            }
                        }
                    }
                }
            }
            if (p.isMutSub) {
                p.makeDiscarded();
                List<Peptide> peps = p.getPepList();
                for (Peptide pep : peps) {
                    if (pep.isClaimed) {
                        pep.makeResolved();
                        pep.unDistinct();
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void discardSingleIdent() {
        for (Protein p : proteins) {
            if (p.getPepNo() <= 1) {
                p.makeDiscarded();
                List<Peptide> peps = p.getPepList();
            }
        }
    }

    /**
     *
     * @param method
     * @param num
     */
    public void setQuants(int num) {        
        for (Protein p: proteins) {
            if (p.isHeadProt && !p.isDiscarded) {                
                p.setQuantSum(num);
                p.setQuantHiN(num);
                p.setQuantHi3NonConSameAccrossRuns(num);                    
            }
        }
    }
    public void setQuantProgenesisHi3NonConflicting(int num) {
        for (Protein p: proteins) {
            //System.out.println(p.getProtName());
            p.setQuantProgenesisHi3NonConflicting(num);
        }
    }
    /**
     *
     * @param prn
     * @return
     */
    public boolean checkProts(String prn){
        for (Protein pr : proteins) {
            if (prn.equals(pr.getProtName())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param pt
     * @return
     */
    public Protein retProt(String pt) {
        for (Protein pr : proteins) {
            if (pt.equals(pr.getProtName())) {
                return pr;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return proteins.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public Protein getProt(int index) {
        Protein tempProt = null;
        for (int i = 0; i < proteins.size(); i++) {
            tempProt = proteins.get(index);
        }
        return tempProt;
    }
    private Protein checkProt(String prot) {
        Protein tempProt = null;
        if (!checkProts(prot)) {
            tempProt = newProt(prot);
            //System.out.println("new prot: " + prot);
        }
        else {
            tempProt = retProt(prot);
            //System.out.println("ret prot: " + prot);
        }
        return tempProt;
    }
    private Protein newProt(String pt) {
        Protein tmpProt = new Protein(pt);
        // Add it to the block of proteins
        proteins.add(tmpProt);
        return tmpProt;
    }    
}
