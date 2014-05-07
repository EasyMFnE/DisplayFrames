/*
 * This file is part of the DisplayFrames plugin by EasyMFnE.
 * 
 * DisplayFrames is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * 
 * DisplayFrames is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for details.
 * 
 * You should have received a copy of the GNU General Public License v3 along
 * with DisplayFrames. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Compile and execute this code to translate all *.lang files in the working
* directory and save the translations into the "locale" subfolder.
*/ 
public class LocaleTranslator {
    
    private static File currentDir;
    private static List<String> template;
    private static String outbox = File.separator + "locale" + File.separator;
    
    /** Main method for command-line usage, uses no arguments */
    public static void main(String[] args) throws IOException {
        setWorkingDir();
        setTemplate();
        if (!new File(currentDir.getAbsolutePath() + outbox).exists())
            new File(currentDir.getAbsolutePath() + outbox).mkdir();
        for (File file : currentDir.listFiles())
            if (file.getName().endsWith(".lang"))
                translate(file);
    }
    
    /** Save reference to the current working directory */
    private static void setWorkingDir() {
        Path currentRelativePath = Paths.get(".");
        currentDir = currentRelativePath.toFile();
    }
    
    /** Load template file and store as List<String> */
    private static void setTemplate() throws IOException {
        for (File file : currentDir.listFiles()) {
            if (file.getName().endsWith(".template")) {
                template = new ArrayList<String>();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = null;  
                while ((line = reader.readLine()) != null)   {
                    template.add(line);
                }
                reader.close();
            }
        }
    }
    
    /** Translate a Minecraft language file into a DisplayFrames locale */
    private static void translate(File file) throws IOException {
        Pattern tempPattern = Pattern.compile("^(.+:\\s)(.+)$");
        Pattern langPattern = Pattern.compile("^(.+)=(.+)$");
        BufferedWriter writer = new BufferedWriter(new FileWriter(currentDir.getAbsolutePath() + outbox + file.getName()));
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String langLine = null;
        Map<String,String> langMap = new HashMap<String,String>();
        while ((langLine = reader.readLine()) != null)   {
            Matcher langMatch = langPattern.matcher(langLine);
            if (langMatch.matches()) {
                langMap.put(langMatch.group(1), langMatch.group(2));
            }
        }
        for (String tempLine : template) {
            Matcher tempMatch = tempPattern.matcher(tempLine);
            if (tempMatch.find()) {
                if (langMap.containsKey(tempMatch.group(2))) {
                    writer.append(tempMatch.group(1) + "\"" +  langMap.get(tempMatch.group(2)) + "\"");
                    writer.newLine();
                }
                else {
                    writer.append("# " + tempLine);
                    writer.newLine();
                }
            }
            else {
                writer.append(tempLine);
                writer.newLine();
            }
        }
        writer.close();
        reader.close();
    }
    
}
