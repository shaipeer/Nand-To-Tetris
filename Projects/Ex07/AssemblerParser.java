import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
	|====================================================|
	|	  	Exercise #  :	 7							 |
	|													 |
	|   	File name   :	 AssemblerParser.java		 |
	|		Date		:	 018/05/2015    	      	 |
	|		Author    	:	 Shai Pe'er        			 |
	|		Email     	:	 shaip86@gmail.com 			 |
	|====================================================|
	*/

public class AssemblerParser 
{
	private BufferedWriter bw;
	private Map<String, Integer> map; 
	private int mapCounter;
	
	
	public AssemblerParser()
	{
		map = new HashMap<String, Integer>();
		initialHashMap();
		mapCounter = 16;
	}
	
	//===========================================================================================================================
	
	public void Parse(String[] codeLines, BufferedWriter _bw)
	{
		bw = _bw;
		
		scanForLabels(codeLines);
		
		for(int i = 0 ; i < codeLines.length ; i++)
		{			
			codeLines[i] = codeLines[i].trim();
			
			if(!codeLines[i].contains("("))
			{
				if(codeLines[i].contains("@"))
					write(Ainstruction(codeLines[i].split("@")[1]));
				else
					write(Cinstruction(codeLines[i]));
			}
		}
	}
	
	//===========================================================================================================================

	private String Ainstruction(String op)
	{
		int num;
		try 
		{
		      num = Integer.parseInt(op);
		      return intToBin(num);
		} 
		catch (NumberFormatException e) 
		{
		      return symbol(op);
		}
		
	}
	
	private String symbol(String op)
	{
		if(map.containsKey(op))
		{
			return intToBin(map.get(op));
		}
		else
		{
			map.put(op	,	mapCounter);
			mapCounter++;
			return intToBin(mapCounter-1);
		}
	}
	
	private String intToBin(int num) throws NumberFormatException
	{
		try 
		{
		      return Integer.toBinaryString(0x10000 | num).substring(1);
		} 
		catch (NumberFormatException e) 
		{
		      return "";
		}
	}
	
	private String Cinstruction(String op)
	{
		String[] lineTok;
		
		String lineBin = "";
		
		if(op.contains("$"))
		{
			lineBin = "101";
			op = op.replace("$", "");
		}
		else					lineBin = "111";
			
		if(op.contains("="))
		{
			lineTok = op.split("=");
			
			lineBin += comp(lineTok[1]);
			lineBin += dest(lineTok[0]);
			lineBin += jump("null");
			
			return lineBin;
		}
		else if(op.contains(";"))
		{
			lineTok = op.split(";");
			
			lineBin += comp(lineTok[0]);
			lineBin += dest("null");
			lineBin += jump(lineTok[1]);
			
			return lineBin;
		}
		else
			return "";
	}
	
	private String comp(String op)
	{
		switch(op)
		{
		//a=0
		case "0":	return "0" + "101010";
		case "1":	return "0" + "111111";
		case "-1":	return "0" + "111010";
		case "D":	return "0" + "001100";
		case "A":	return "0" + "110000";
		case "!D":	return "0" + "001101";
		case "!A":	return "0" + "110001";
		case "-D":	return "0" + "001111";
		case "-A":	return "0" + "110011";
		case "1+D":
		case "D+1":	return "0" + "011111";
		case "1+A":
		case "A+1":	return "0" + "110111";
		case "D-1":	return "0" + "001110";
		case "A-1":	return "0" + "110010";
		case "A+D":
		case "D+A":	return "0" + "000010";
		case "D-A":	return "0" + "010011";
		case "A-D":	return "0" + "000111";
		case "A&D":
		case "D&A":	return "0" + "000000";
		case "A|D":
		case "D|A":	return "0" + "010101";
		
		//a=1
		case "M":	return "1" + "110000";
		case "!M":	return "1" + "110001";
		case "-M":	return "1" + "110011";
		case "1+M":
		case "M+1":	return "1" + "110111";
		case "M-1":	return "1" + "110010";
		case "M+D":
		case "D+M":	return "1" + "000010";
		case "D-M":	return "1" + "010011";
		case "M-D":	return "1" + "000111";
		case "M&D":
		case "D&M":	return "1" + "000000";
		case "M|D":
		case "D|M":	return "1" + "010101";
		}
		
		return "";
	}

	
	
	private String dest(String op)
	{
		switch(op)
		{
			case "null":	return "000";
			case "M":		return "001";
			case "D":		return "010";
			case "MD":		return "011";
			case "A":		return "100";
			case "AM":		return "101";
			case "AD":		return "110";
			case "AMD":		return "111";
		}
		
		return "";
	}
	
	
	private String jump(String op)
	{
		switch(op)
		{
			case "null":	return "000";
			case "JGT":		return "001";
			case "JEQ":		return "010";
			case "JGE":		return "011";
			case "JLT":		return "100";
			case "JNE":		return "101";
			case "JLE":		return "110";
			case "JMP":		return "111";
		}
		return "";
	}
	
	
	//===========================================================================================================================
	
	private void initialHashMap()
	{
		map.put("SP"	, 	0	);
		map.put("LCL"	, 	1	);
		map.put("ARG"	, 	2	);
		map.put("THIS"	,	3	);
		map.put("THAT"	,	4	);
		map.put("SCREEN",	16384);
		map.put("KBD"	,	24576);
		map.put("R0"	, 	0	);
		map.put("R1"	, 	1	);
		map.put("R2"	, 	2	);
		map.put("R3"	, 	3	);
		map.put("R4"	, 	4	);
		map.put("R5"	, 	5	);
		map.put("R6"	, 	6	);
		map.put("R7"	, 	7	);
		map.put("R8"	, 	8	);
		map.put("R9"	,	9	);
		map.put("R10"	,	10	);
		map.put("R11"	,	11	);
		map.put("R12"	,	12	);
		map.put("R13"	,	13	);
		map.put("R14"	,	14	);
		map.put("R15"	,	15	);
		
		
	}
	
	private void scanForLabels(String[] codeLines)
	{
		int index = 0;
		String[] tok;
		
		for(int i = 0 ; i < codeLines.length ; i++)
		{
			if(codeLines[i].contains("("))
			{
				tok = codeLines[i].split("\\(");
				
				tok = tok[1].split("\\)");
				
				
				map.put(tok[0] , index);
				
			}
			else
			{
				index++;
			}
		}
		
	}
	
	//===========================================================================================================================
	
	private void  write(String str)
	{
		System.out.println(str);
		try 
		{	
			bw.write(str + "\n");
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
