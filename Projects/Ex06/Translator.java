import java.io.BufferedWriter;
import java.io.IOException;


/**
|====================================================|
|	  	Exercise #  :	 6							 |
|													 |
|   	File name   :	 Translator.java			 |
|		Date		:	 05/05/2015    	      		 |
|		Author    	:	 Shai Pe'er        			 |
|		Email     	:	 shaip86@gmail.com 			 |
|====================================================|
*/

public class Translator 
{

	private BufferedWriter bw;
	private String className;
	
	public Translator()
	{

	}
	
	//===========================================================================================================================
	
	public void translate(String[] codeLines, BufferedWriter _bw, String _className)
	{
		bw = _bw;
		String[] lineTok;
		className = corpClassName(_className);
		
		for(int i = 0 ; i < codeLines.length ; i++)
		{			
			lineTok = codeLines[i].split("\\s");
			
			
			
			switch(lineTok[0])
			{
				case "push":	regDhandler(lineTok);	// (lineTok[0], lineTok[1], lineTok[2]);
								pushD();
								break;
				case "pop":		pop();
								regDhandler(lineTok);	// (lineTok[0], lineTok[1], lineTok[2]);
								break;
				case "label":	addLabel(lineTok);
								break;
				case "goto":	addGoto(lineTok[1]);	// label
								break;
				case "if-goto":	addIfGoto(lineTok[1]);	// label
								break;
				case "function":addFunction(lineTok);
								break;
				case "call":	addCall(lineTok);
								break;
				case "return":	addReturn();
								break;
				default: 		executeOp(lineTok[0]);
								break;
			}
		}

	}
	
	//===========================================================================================================================
	
	private void regDhandler(String[] lineTok)
	{
		String op = "", type = "", val = "";
		
		op   = lineTok[0];
		type = lineTok[1];
		val  = lineTok[2];

		if(op.equals("pop"))
		{
			write("D=M");
			write("@TEMP_REG_2");
			write("M=D");
		}
		
		
		if(type.equals("local") || type.equals("argument") || type.equals("this") || type.equals("that"))
		{
			write("@" +val);
			write("D=A");
			switch(type)
			{					
				case "local":		write("@LCL");		break;
				case "argument":	write("@ARG");		break;
				case "this":		write("@THIS");		break;
				case "that":		write("@THAT");		break;
			}
			write("A=M+D");	//Ram[this]+3
		}
		else
		{
			switch(type)
			{	
				case "temp":	if(Integer.parseInt(val) >= 8)				//TWIST 3
								{
									write("TWIST 3 ERROR!");
									System.out.println("TWIST 3 ERROR!\n");
								}
								else
								{
									write("@5");
									write("D=A");
									write("@" + val);
									write("A=A+D");
								}
									break;
				case "pointer":		write("@3");
									write("D=A");
									write("@" + val);
									write("A=A+D");
									break;
				case "constant":	write("@" + val);
									if(op.equals("push"))
									{
										write("D=A");	
										return;
									}
									if(op.equals("pop"))
									{
										write("D=A");	
										break;
									}
				case "static":		write("@" + className + "." + val);
									if(op.equals("push"))
									{
										write("D=M");	
										return;
									}
									if(op.equals("pop"))
									{
										break;
									}
				case "nothing":		for(int i = 1 ; i < Integer.parseInt(val) ; i++)	//TWIST 2
									{
										if(op.equals("push"))
											pushD();
										else if(op.equals("pop"))
											pop();
									}
									return;
									
			}
		}
		
		switch(op)
		{	
			case "push":	write("D=M");	break;		//D = Ram[Ram[this]+3]
			
			case "pop":		write("D=A");
							write("@TEMP_REG_1");
							write("M=D");		//save A
							
							write("@TEMP_REG_2");
							write("D=M");		//retrieve D
							
							write("@TEMP_REG_1");
							write("A=M");		//retrieve A
							
							write("M=D");			//Ram[Ram[this]+3] = D
							
							break;
		}
		
		
	}

	//===========================================================================================================================
	
	//pop from stack - pointer A to location M to content 
	private void pop()
	{		
		//Ram[SP]--
		write("@SP");
		write("M=M-1");		
		write("A=M");
	}	

	//===========================================================================================================================
	
	//push D to the head of the stack
	private void pushD()
	{
		//Ram[Ram[SP]] = D
		write("@SP");
		write("A=M");
		write("M=D");
		//Ram[SP]++
		write("@SP");
		write("M=M+1");
	}

	
	
	//===========================================================================================================================
	
	
	
	
	private void addLabel(String[] lineTok)
	{
		write("(" + lineTok[1] + ")");
	}

	private void addGoto(String label)
	{
		write("@" + label);
		write("0; JMP");
	}
	
	private void addIfGoto(String label)
	{
		pop();
		write("D=M");
		write("@IF_GOTO_" + VMtranslator.ifCounter);
		write("D;JEQ");
		write("@" + label);
		write("0;JEQ");
		write("(IF_GOTO_" + VMtranslator.ifCounter +")");
		VMtranslator.ifCounter++;
		
	}
	
	private void addFunction(String[] lineTok)
	{
		write("(" + lineTok[1] + ")");
		write("@0");
		write("D=A");
		for(int i = 0 ; i < Integer.parseInt(lineTok[2]) ; i++)
		{
			pushD();			
		}
	}
	
	private void addCall(String[] lineTok)
	{
		write("@RETURN_" + lineTok[1] + "_" + VMtranslator.functionLabelCounter);
		write("D=A");
		pushD();
		
		write("@LCL");
		write("D=M");
		pushD(); 		//push LCL
		write("@ARG");
		write("D=M");
		pushD(); 		//push ARG
		write("@THIS");
		write("D=M");
		pushD(); 		//push THIS
		write("@THAT");
		write("D=M");
		pushD(); 		//push THAT
		
		write("@SP");
		write("D=M");
		write("@" + lineTok[2]);
		write("D=D-A");
		write("@5");
		write("D=D-A");
		write("@ARG");
		write("M=D");	//ARG = SP-n-5 
		
		write("@SP");
		write("D=M");
		write("@LCL");
		write("M=D");	//LCL = SP
		
		addGoto(lineTok[1]);	//goto f
		
		write("(RETURN_" + lineTok[1] + "_" + VMtranslator.functionLabelCounter + ")");
		
		VMtranslator.functionLabelCounter++;
	}
	
	private void addReturn()
	{
		write("@LCL");
		write("D=M");
		write("@frame");
		write("M=D");		//frame = LCL
		
		write("@frame");
		write("D=M");
		write("@5");
		write("A=D-A");		//A = frame-5
		write("D=M");		//D = *(frame-5)
		write("@return_addr");
		write("M=D");		//RET = *(frame-5)
		
		pop();
		write("D=M");		//D = pop()
		write("@ARG");
		write("A=M");
		write("M=D");		//*ARG = pop()
		
		write("@1");
		write("D=A");
		write("@ARG");
		write("D=D+M");		//D = ARG+1
		write("@SP");
		write("M=D");		//SP = ARG+1
		
		write("@frame");
		write("D=M");
		write("@1");
		write("A=D-A");
		write("D=M");
		write("@THAT");
		write("M=D");		//THAT = *(frame-1)
		
		write("@frame");
		write("D=M");
		write("@2");
		write("A=D-A");
		write("D=M");
		write("@THIS");
		write("M=D");		//THIS = *(frame-2)
		
		write("@frame");
		write("D=M");
		write("@3");
		write("A=D-A");
		write("D=M");
		write("@ARG");
		write("M=D");		//ARG = *(frame-3)
		
		write("@frame");
		write("D=M");
		write("@4");
		write("A=D-A");
		write("D=M");
		write("@LCL");
		write("M=D");		//LCL = *(frame-4)
		
		write("@return_addr");
		write("A=M");
		write("0; JMP");	//GOTO return address
	}
	
	
	//===========================================================================================================================
	
	//handle operations
	private void executeOp(String op)
	{
		switch(op)
		{
		 
			case "neg": case "not":										unaryOp(op);	break;
			case "eq":	case "gt":	case "lt":							booleanOp(op);	break;
			case "add":	case "sub":	case "and":	case "or": case "mult2":binaryOp(op);	break;		
			
		}
	}
	

	//===========================================================================================================================
	
	private void unaryOp(String op)		// operations (neg, not) --> don’t change stack size.
	{
		switch(op)
		{
			case "neg":	pop();
						write("D=-M");
						pushD();
						break;
			case "not":	pop();
						write("D=A");
						ifZeroD();
						pushD();
						break;

		}
	}
	


	//===========================================================================================================================
	
	private void booleanOp(String op)	// operations (eq,gt,lt) result in true(-1) or false (0) values in the stack.	Boolean operations require label usage.
	{
		pop();
		write("D=A");	//17
		pop();
		write("D=A-D");	//17

		write("@IF" + VMtranslator.ifCounter );
		
		switch(op)
		{
			case "eq":	write("D;JEQ");	break;
			case "gt":	write("D;JGT");	break;
			case "lt":	write("D;JLT");	break;
			
		}
		
		write("D=-1");
		write("@ELSE" + VMtranslator.ifCounter);
		write("0;JMP");
		write("(IF" + VMtranslator.ifCounter +")");
		write("D=0");
		write("(ELSE" + VMtranslator.ifCounter +")");
		VMtranslator.ifCounter++;
		
		pushD();
	}

	//===========================================================================================================================
	
	private void binaryOp(String op)		//operations (all but neg,not) --> change stack size in -1.
	{
		switch(op)
		{
			case "add":	pop();
						write("D=M");	//equals to Ram[SP]
						pop();
						write("D=M+D");
						pushD();
						break;
			case "sub":	pop();
						write("D=M");
						pop();
						write("D=M-D");
						pushD();
						break;
			case "and":	pop();
						write("D=M");	//point to SP
						pop();
						write("D=M&D");
						pushD();
						break;
			case "or":	pop();
						write("D=M");	//point to SP
						pop();
						write("D=M|D");
						pushD();
						break;
			case "mult2":	
						pop();
						write("D=M");
						write("D=D+M");
						pushD();
						break;
						
		}
	}

	//===========================================================================================================================
	
	private void ifZeroD()
	{
		write("@IF" + VMtranslator.ifCounter );
		write("D;JEQ");
		write("M=-1");
		write("D=M");
		write("@ELSE" + VMtranslator.ifCounter);
		write("0;JMP");
		write("(IF" + VMtranslator.ifCounter +")");
		write("M=0");
		write("D=M");
		write("(ELSE" + VMtranslator.ifCounter +")");
		VMtranslator.ifCounter++;
	}

	//===========================================================================================================================
	
	private String corpClassName(String className)
	{
		String[] classPathTok = className.split("\\\\");
		String temp = classPathTok[classPathTok.length-1];
		classPathTok = temp.split("\\.");
		temp = classPathTok[0];
		return temp;
		//return (classPathTok[classPathTok.length-1].split("\\."))[0];
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
