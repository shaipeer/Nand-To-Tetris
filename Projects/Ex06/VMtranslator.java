import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
|====================================================|
|	  	Exercise #  :	 6							 |
|													 |
|   	File name   :	 VMtranslator.java			 |
|		Date		:	 05/05/2015    	      		 |
|		Author    	:	 Shai Pe'er        			 |
|		Email     	:	 shaip86@gmail.com 			 |
|====================================================|
*/

public class VMtranslator {

	/**
	 * usage: VMtranslator <file> or VMtranslator <dir>
	 * @throws IOException 
	 * @throws MyException 
	 */
	
	private static Translator translator;
	protected static int ifCounter = 0;
	protected static int functionLabelCounter = 0;
	
	
	public static void main(String[] args) throws IOException, Exception {
		File myfile = new File(args[0]);
		FileWriter fw;
		BufferedWriter bw;
		translator = new Translator();
		if(myfile.isDirectory()){
			fw=new FileWriter(new File(args[0]+".asm"));
			bw = new BufferedWriter(fw);		
			
			if( !(args.length > 1)  || !(args[1].equals("-noinit") ) )		//NOINIT FLAG CHECK!
			{
				init(bw);
			}
			TranslateDir(myfile,bw);
		}
		else{
			if(args[0].substring(args[0].length()-3).matches("\\.vm")){
				fw=new FileWriter(new File(args[0].substring(0, args[0].length()-3)+".asm"));
				bw = new BufferedWriter(fw);
				TranslateFile(myfile,bw);
			}
			else{
				throw new Exception("not a vm file: " + args[0]);
			}
		}
		bw.close();
		fw.close();		
	}
		


/**
* handles a single vm file
*/
    private static void TranslateFile(File filename,BufferedWriter bw) throws IOException, Exception{
		   VMcode vmc = new VMcode(filename.getPath());
			while(vmc.hasMoreCommands()){				
				vmc.advance();
			}
			//You should start working on the ex from here, and remove the following line of course:
			
			translator.translate(vmc.getLinesArray(), bw, filename.toString());
			
			//The following lines from here to the end of the file shouldn't be removed:			
	}
	
/**
* handles a directory of vm files
*/
	private static void TranslateDir(File folder,BufferedWriter bw) throws IOException, Exception{
		
		File[] listOfFiles = folder.listFiles(); 
		String filename,ext,arg;
		for(int i=0;i<listOfFiles.length;i++){
			filename=listOfFiles[i].getName();
			ext = filename.substring(filename.length()-3);			
			if (ext.matches(".vm$")){			    			
				TranslateFile(listOfFiles[i],bw);			
			}
		}
	}
	
	
	
	private static void init(BufferedWriter bw)  throws IOException, Exception 
	{
		bw.write("@256"  + "\n");
		bw.write("D=A"  + "\n");
		bw.write("@SP"  + "\n");
		bw.write("M=D"  + "\n");
		
		bw.write("@RETURN_sys.init" + "\n");
		bw.write("D=A"  + "\n");
		pushD(bw);
		
		bw.write("@LCL"  + "\n");
		bw.write("D=M"   + "\n");
		pushD(bw); 					//push LCL
		bw.write("@ARG"  + "\n");
		bw.write("D=M"   + "\n");
		pushD(bw); 					//push ARG
		bw.write("@THIS" + "\n");
		bw.write("D=M"   + "\n");
		pushD(bw); 					//push THIS
		bw.write("@THAT" + "\n");
		bw.write("D=M"   + "\n");
		pushD(bw); 					//push THAT
		
		bw.write("@SP"   + "\n");
		bw.write("D=M"   + "\n");
		
		
		bw.write("@5"    + "\n");
		bw.write("D=D-A" + "\n");
		bw.write("@ARG"  + "\n");
		bw.write("M=D"   + "\n");	//ARG = SP-n-5 
		
		bw.write("@SP"   + "\n");
		bw.write("D=M"   + "\n");
		bw.write("@LCL"  + "\n");
		bw.write("M=D"   + "\n");	//LCL = SP
		
		bw.write("@Sys.init" + "\n");
		bw.write("0; JMP"    + "\n");		//goto f
		
		bw.write("(RETURN_sys.init)" + "\n");
		
	}
	
	private static void pushD(BufferedWriter bw) throws IOException, Exception 
	{
		//Ram[Ram[SP]] = D
		bw.write("@SP"   + "\n");
		bw.write("A=M"   + "\n");
		bw.write("M=D"   + "\n");
		//Ram[SP]++
		bw.write("@SP"   + "\n");
		bw.write("M=M+1" + "\n");
	}
	
	
	
	
	
	
}

class VMcode{
		private String [] arr;
		private int  curr_index;
		public VMcode(String filename) throws IOException, Exception{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line,str="";
			while ((line = br.readLine()) != null) {
				curr_index=line.indexOf("//");
				if(curr_index!=-1){
					line=line.substring(0, curr_index);
				}			
				if(!line.matches("^\\s*$")){			
					str=str.concat(line + "\n");
				}
			}			
			arr = str.split("\n");	
			br.close();
			curr_index=0;		
		}
		public boolean hasMoreCommands(){
			return (arr.length>(curr_index));
		}
		
		public void advance(){
			curr_index++;
		}
		
		public String nextCommand(){
			return arr[curr_index];
		}
		
		
		public String[] getLinesArray()
		{
			return arr;
		}
		
		
		
		
		
	}

