
package Protein;

import Grouping.Group;
import Grouping.GroupArray;
import Peptide.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;


/**
 * Class to create an array of Protein objects
 */

public class ProtArray {
    
    private ArrayList<Protein> proteins;
    private Protein tempProt = null;
    
    public ProtArray() {
        
        proteins = new <Protein>ArrayList(); 
    }
    // Method to build array from Progenesis peptideIon .csv 
    public void buildFromCSV(String prot, String pep) {
        tempProt = checkProt(prot);
        if(!tempProt.pepInList(pep)){
            tempProt.addPepNames(pep);
        }
    }
    // Method to build array from .mzq file
    public void buildFromMZQ(String prot, String[] peps) {
        tempProt = checkProt(prot);
        for (String pep : peps) {
            tempProt.addPepNames(pep.replace(" ", ""));                    
        }
    }
    // Maps peptide objects to protein objects in array
    public void assignPepList(PepArray peps) {
        for (Protein p : proteins) {
            //System.out.println(p.getProtName() + ": ");
            List<String> pepNames = p.getPepNames();
            //System.out.println(pepNames);
            for (String name : pepNames) {
                Peptide peptide = peps.retPep(name);
                //System.out.println(peptide.getPepName());
                p.addPepList(peptide);                
            }
        }
    } 
    // Removes proteins 
    public void discardNonIdent() {
        Iterator<Protein> iter = proteins.iterator();
        while (iter.hasNext()) {
            Protein p = iter.next();
            if(p.getPepNo() == 0) {
                iter.remove();
            }
        }
    }    
    public void orderProts() {        
        // Sort proteins by number of peptides mapped to them
        // Descending
        Collections.sort(proteins,
            (protein1, protein2) -> protein2.getPepNo()
                    - protein1.getPepNo());
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList(); // this is problem line - XML only
            Collections.sort(peps,
                    (pep1, pep2) -> pep1.getProtNo()
                    - pep2.getProtNo());
        }
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
                        Group g = pg.getHeadPeps(pp);
                        gps.add(g);
                        //System.out.println(g.getGroupHead().getProtName());
                        //if(gps != null) {
                            for (Group gp : gps) {
                                gp.addProt(p);
                            }
                        //}
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
                    }
                }
            }
        }
    }
    public void discardSingleIdent() {
        for (Protein p : proteins) {
            if(p.getPepNo() <= 1) {
                p.makeDiscarded();
                List<Peptide> peps = p.getPepList();
            }
        }
    }
    public void setQuants(String method, int num) {        
        for (Protein p: proteins) {
            if(p.isHeadProt && !p.isDiscarded) {
                if ("hi3".equals(method))
                    p.setQuantHi3(num);
                if ("sum".equals(method))
                    p.setQuantSum(num);
            }
        }
    }
    public void saveProts(String fname) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Protein ,PepNo ,ProtType ,Discarded? ,Peptides");
            for (Protein p : proteins) {
                outFile.print(p.getProtName()+ "," + p.getPepNo() + "," + p.protType() + "," + p.isDiscarded + ",");                
                List<Peptide> peps = new ArrayList<>();
                peps = p.getPepList();
                for (Peptide ps : peps) {
                    outFile.print(ps.getPepName() + "," + ps.getProtNo() + ",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
    public boolean checkProts(String prn){
        for (Protein pr : proteins) {
            if (prn.equals(pr.getProtName()))              
                return true;                
        }
        return false;
    }
    public Protein retProt(String pt) {
        for (Protein pr : proteins) {
            if (pt.equals(pr.getProtName())) 
                return pr;            
        }
        return null;
    }
    private Protein checkProt(String prot) {
        if(!checkProts(prot)) { 
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
