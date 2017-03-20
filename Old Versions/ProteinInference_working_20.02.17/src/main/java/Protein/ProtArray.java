
package Protein;

import Grouping.Group;
import Grouping.GroupArray;
import Peptide.Peptide;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtArray {
    
    private ArrayList<Protein> proteins;
    private ProtHash protMap;
    
    public ProtArray() {        
        proteins = new <Protein>ArrayList(); 
        protMap = new ProtHash();
    }    
    public void addProt(Protein pr) {
        proteins.add(pr);
    }
    public boolean checkProts(String prn){
        for (Protein pr : proteins) {
            if (pr.getProtName().equals(prn)) 
                return true;                
        }
        return false;
    }
    public Protein newProt(String pt) {
        Protein tmpProt = new Protein(pt);
        // Add it to the block of proteins
        addProt(tmpProt);
        return tmpProt;
    }
    public Protein retProt(String pt) {
        Protein tmpProt = null;
        for (Protein pr : proteins) {
            if (pr.getProtName().equals(pt)) 
                tmpProt = pr;            
        }
    return tmpProt;
    }        
    public void orderProts() {        
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
    public void setProtHash() {
        for (Protein p: proteins) {
            protMap.addToMap(p, p.getPepList());
        }
        protMap.saveHash("ProtHash.csv");
    }
    public void setDistinctProts(GroupArray pg) {        
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if(pep.isUnique && !p.isDistinct) { 
                    p.makeDistinct();
                    Group gp = new Group(p);
                    pg.addGroup(gp);
                    for (Peptide pp : peps) {
                        pp.makeClaimed();
                        pp.fromDistinct();
//                        if(!pp.isUnique)
//                            pp.makeConflicted();
                    }
                }
            }            
        }        
    }
    public void setSameSetProts(GroupArray pg) {
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if(!pep.isClaimed && !p.isAssigned) { 
                    p.makeSameSet();
                    Group gp = new Group(p);
                    pg.addGroup(gp);
                    for (Peptide pp : peps) {                        
                        pp.makeSameSet();
                        pp.makeClaimed();
                        if(!pp.fromDistinct)
                            pp.makeResolved();
                    }
                }                
                if(pep.fromSameSet && !p.isAssigned) {
                    //Find the group head
                    Group gp = pg.getHeadPeps(pep);
                    if(gp.samePeps(peps));
                        gp.addProt(p);
                    p.makeSubSet();
                    for (Peptide pp : peps) {
                        pp.makeClaimed();
                        pp.makeSubSet();
                    }                    
                }  
            }            
        }        
    }
    public void setMutSubProts(GroupArray pg){
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {                            
                if (pep.isClaimed && !p.isAssigned) {
                    List<Group> gps = new ArrayList<>();
                    for (Peptide pp : peps) {
                        pp.makeMutSub();
                        gps.add(pg.getHeadPeps(pp));
                        for (Group gp : gps) {
                            gp.addProt(p);
                        }
                    }      
                    p.makeMutSub();                    
                }                
            }
        }
    } 
    public void discardProts(GroupArray pg) {
        Group gp = null;
        for (Protein p: proteins) {
            if(p.isSameSet) {
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
                                //pep.unDistinct();
                            }    
                        }                            
                    }
                }                    
            }
            if(p.isMutSub) {
                p.makeDiscarded();
                List<Peptide> peps = p.getPepList();
                for (Peptide pep : peps) {
                    if (pep.isClaimed) {
                        pep.makeResolved();
                        pep.unDistinct();
                        //pep.unConflict();
                    }
                }
            }
        }
    }
    public void setConflicted() {
        for (Protein p : proteins) {
            if(!p.isDiscarded && p.isSubset) {
                List<Peptide> peps = p.getPepList();
                for (Peptide pep : peps) {
                    if(pep.fromDistinct && !pep.isResolved)
                        pep.makeConflicted();
                }
            }
            
            
//            if(p.isDistinct) {
//                List<Peptide> peps = p.getPepList();
//                for (Peptide pep : peps) {
//                    List<Protein> prots = pep.getProtList();
//                    for (Protein prot : prots) {
//                        if(!prot.isDiscarded && prot.isMutSub && !pep.isUnique)
//                            pep.makeConflicted();
//                    }
//                }    
//            }
        }        
    }            
    public void saveProts(String fname) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Protein ,PepNo ,ProtType ,Discarded? ,Peptides");
            for (Protein p : proteins) {
                outFile.print(p.getProtName()+ "," + p.getPepNo() + "," + p.protType() + "," + p.isDiscarded + ",");                
                List<Peptide> peps = p.getPepList();
                for (Peptide ps : peps) {
                    outFile.print(ps.getPepName() + "," + ps.getProtNo() + ",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
}
