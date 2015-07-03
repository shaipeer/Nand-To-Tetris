import java.util.ArrayList;

/**
   |====================================================|
   |	  	Exercise #  :	 3							|
   |													|
   |   	   	File name   :	 SymbolTable.java			|
   |		Date		:	 14/04/2015    	      		|
   |		Author    	:	 Shai Pe'er        			|
   |		Email     	:	 shaip86@gmail.com 			|
   |====================================================|
 */

public class SymbolTable 
{

	private final static int NAME	= 0;
	private final static int TYPE	= 1;	
	private final static int KIND	= 2;
	private final static int NUMBER	= 3;
	
	
	private ArrayList<String[]> symbolTable = new ArrayList<String[]>();
	
	public void addRaw(String name, String type, String kind)
	{
		int number = getKindNumber(kind);
		String[] raw = {name, type, kind, number + ""};
		symbolTable.add(raw);
	}
	
	public boolean isVarInTable(String varName)
	{
		String[] row = getRowWherVarNameIs(varName);
		if(row != null)
			return true;
		else
			return false;
	}

	public String getNumber(String varName)
	{
		String[] row = getRowWherVarNameIs(varName);
		if(row != null)
			return row[NUMBER];
		else
			return "";
	}
	
	public String getKind(String varName)
	{
		String[] row = getRowWherVarNameIs(varName);
		if(row != null)
			return row[KIND];
		else
			return "";
	}
	
	public String getType(String varName)
	{
		String[] row = getRowWherVarNameIs(varName);
		if(row != null)
			return row[TYPE];
		else
			return "";
		
	}
	
	private String[] getRowWherVarNameIs(String varName)
	{
		for (String[] row : symbolTable) 
		{
			if(row[NAME].equals(varName))
			{
				return row;
			}
		}
		return null;
	}
	
	public int getKindNumber(String kind)
	{
		int kindCounter = 0;
		for (String[] row : symbolTable) 
		{
			if(row[KIND].equals(kind))
			{
				kindCounter++;
			}
		}
		return kindCounter;
	}
}
