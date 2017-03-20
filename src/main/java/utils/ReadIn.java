/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pep.Peptide;

/**
 * Provides methods to read in data.
 * Methods provided for Progenesis peptide ion and peptide output
 * and mzq file.
 * 
 * @param   fileIn  the name and location of the input file
 */
public class ReadIn {

    public ReadIn() {
        
    }

    public static List<String> readInCSV(String fileName) {
        int count = 1;
        String tempLine = "";
        String splitBy = ",";
        List<String> data = new ArrayList<String>();
        String[] pepProperties = null;
        
        try {
            BufferedReader inputFile =
                    new BufferedReader(new FileReader(fileName));
            while ((tempLine = inputFile.readLine()) != null) {
                if (count <= 3 ) {
                    count++;
                }
                else {
                    data.add(tempLine);
                    //System.out.println(tempLine);
                }
            }
        } catch (IOException | NumberFormatException e) {}
    return data;
}

    
}
