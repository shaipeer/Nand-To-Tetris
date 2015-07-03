import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class JackCompiler {

	/**
	 * usage: JackAnalyzer <file> or JackAnalyzer <dir>
	 * @throws IOException 
	 * @throws MyException 
	 */
	public static void main(String[] args) throws IOException, Exception {
		File myfile = new File(args[0]);
		if(myfile.isDirectory()){
			CompileDir(myfile);
		}
		else{			
		    CompileFile("",myfile);
		}
	}
		


/**
* handles a single jack file
*/
    private static void CompileFile(String path,File filename) throws IOException, Exception{
		
		
		String inputfile = filename.getName();		
		if(inputfile.matches("(\\w)*.jack")){
			String myfilename = path+inputfile.substring(0, inputfile.length()-5)+".vm";
			File myfile = new File(myfilename);
			FileWriter fw = new FileWriter(myfile);
		    BufferedWriter bw = new BufferedWriter(fw);
		    JackTokenizer tok = new JackTokenizer(path+inputfile);
			//You should start working on the ex from here, and remove the following line of course:
			
		    JackParser parse = new JackParser();
		    parse.parseTok(tok, bw);
		    
		    //bw.write("This should be replaced with the required output\n");// remove this line!!!
			//The following lines from here to the end of the file shouldn't be removed:
			bw.close();
			fw.close();
			

		}
		else{
			throw new Exception("file extension is not .jack: "+inputfile);
		}
	}
/**
* handles a directory of jack files
*/
	private static void CompileDir(File folder) throws IOException, Exception{
		
		File[] listOfFiles = folder.listFiles(); 
		String filename,ext,arg;
		for(int i=0;i<listOfFiles.length;i++){
			filename=listOfFiles[i].getName();
			ext = filename.substring(filename.length()-5);
			if (ext.compareTo(".jack")==0){				
			    arg=folder.getPath()+"\\";				
				CompileFile(arg,listOfFiles[i]);			
			}
		}
	}
}

