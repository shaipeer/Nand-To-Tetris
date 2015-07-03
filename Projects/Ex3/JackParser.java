import java.io.BufferedWriter;
import java.io.IOException;

/**
   |====================================================|
   |	  	Exercise #  :	 3							|
   |													|
   |   	   	File name   :	 JackParser.java			|
   |		Date		:	 14/04/2015    	      		|
   |		Author    	:	 Shai Pe'er        			|
   |		Email     	:	 shaip86@gmail.com 			|
   |====================================================|
 */

public class JackParser
{
	private final static int INTEGERCONSTANT = 0;
	private final static int KEYWORD		 = 1;
	private final static int IDENTIFIER		 = 2;	
	private final static int STRINGCONSTANT	 = 3;
	private final static int SYMBOL			 = 4;
	
	private JackTokenizer tok;
	private BufferedWriter bw;
	private int index;
	private int ifCounter;
	private int whileCounter;	
	
	private SymbolTable methodScope;
	private SymbolTable classScope;
	private SymbolTable subroutineScope;
	
	private String className;
	private String functionName;
	private String functionType;

	//============================================================================================================
	public JackParser()
	{
		index = 0;
		ifCounter = 0;
		whileCounter = 0;
	}
	

	//============================================================================================================
	public void parseTok(JackTokenizer _tok, BufferedWriter _bw)
	{
		tok = _tok;
		bw  = _bw;
		index = 0;
		
		scanForFunctions();
		addClass();
	}

	//============================================================================================================
	private void scanForFunctions()
	{
		methodScope = new SymbolTable();
		
		for(int i = 0 ; i < tok.tokens.length ; i++)
		{
			//if(tok.tokens[i].equals("constructor") || tok.tokens[i].equals("function") || tok.tokens[i].equals("method"))
			if(tok.tokens[i].equals("method"))
			{
				methodScope.addRaw(tok.tokens[i+2], tok.tokens[i+1], tok.tokens[i]);
			}
		}
	}
	
	//============================================================================================================
	private void addClass()
	{

		classScope = new SymbolTable();////////////////////////////////////////////////////////////////////////////
		
		advanceIndex(1);
		//addClassName(); 				//className
		className = getTok(); 
		advanceIndex(1);
		
		while(isClassVarDec())			// static | field
		{
			addClassVarDec();
		}
		
		while(IsSubroutineDec())		// constructor | function | method
		{
			subroutineScope = new SymbolTable();///////////////////////////////////////////////////////////////////////
			whileCounter = 0;
			ifCounter = 0;
			addSubroutineDec();
		}	
	}

	//============================================================================================================
	private void addClassVarDec()	// static | field
	{	
		String kind = getTok();
		String type = getTok();
		String name = getTok();
		
		classScope.addRaw(name, type, kind);
		while(tokEquals(","))
		{
			advanceIndex(1);	//skip ','
			name = getTok();
			classScope.addRaw(name, type, kind);
		}
		
		advanceIndex(1);	//skip ';'
	}

	//============================================================================================================
	private void addType()
	{
		
	}

	//============================================================================================================
	private void addSubroutineDec()
	{
		functionType = getTok();		//'constructor | function | method'
		String returnType   = getTok(); 	//'return type'
		functionName = getTok();
		
		if(functionType.equals("method"))
			subroutineScope.addRaw("this", className, "argument");
		
		advanceIndex(1);	//skip '('
			addParameterList();
		advanceIndex(1);	//skip ')'
		
		advanceIndex(1); 	//skip '{'
 			addSubroutineBody();
		advanceIndex(1); 	//skip '}'
	}

	//============================================================================================================
	private void addParameterList()
	{
		if (isType())
		{
			addToSubroutineScope("argument");
			while(tokEquals(","))
			{
				advanceIndex(1);     // skip ','
				addToSubroutineScope("argument");
			}
		}
	}

	//============================================================================================================
	private void addSubroutineBody()
	{
		int varCounter = 0;
		
		while(isVarDec())
		{
			varCounter += addVarDec();		// VarDec
		}
		writeLn("function " + className + "." + functionName + " " + varCounter);
		
		if(functionType.equals("constructor"))
		{
			writeLn("push constant " + classScope.getKindNumber("field"));
	        writeLn("call Memory.alloc 1");
	        writeLn("pop pointer 0");
		}
		else if(functionType.equals("method"))
		{
			writeLn("push argument 0");
	        writeLn("pop pointer 0");
		}
		
		addStatements();		// Statements
		
	}

	//============================================================================================================
	private int addVarDec()
	{
		int paramCounter = 0;
		
		advanceIndex(1); 	//skip 'var'
		if (isType())
		{
			addToSubroutineScope("var");
			paramCounter++;
			while(tokEquals(","))
			{
				addToSubroutineScope("var");
				paramCounter++;
			}
		}
		advanceIndex(1); 	//skip ';'
		
		return paramCounter;
	}

	//============================================================================================================
	private void addClassName()
	{
		// identifier
	}

	//============================================================================================================
	private void addSubroutineName()
	{
		// identifier
	}

	//============================================================================================================
	private void addVarName()
	{
		pushVarWrite(getTok());
	}

	//============================================================================================================
	private void addStatements()
	{
		while(isStatement(index))
			addStatement(); 	//Statement
	}

	//============================================================================================================
	private void addStatement()
	{
		switch(tok.tokens[index])	// ifStatement | doStatement | letStatement | whileStatement | returnStatement
		{
			case "if":		addIfStatement();		break;
			case "do":		addDoStatement();		break;
			case "let":		addLetStatement();		break;
			case "while":	addWhileStatement();	break;
			case "return":	addReturnStatement();	break;
		}
		if(tokEquals(";")) 
			advanceIndex(1); 		//skip ';'
	}

	//============================================================================================================
	private void addLetStatement()
	{	
		boolean isArray = false;
		advanceIndex(1);     //skip 'let'
		String varName = getTok();
		
		if(tokEquals("["))
		{
			advanceIndex(1); 		//skip '['
			
			addExpression();
			if(classScope.getKind(varName).equals("field"))
				writeLn("push this " + classScope.getNumber(varName));
			else
				pushVarWrite(varName);
			
			advanceIndex(1); 		//skip ']'
			
			writeLn("add");
			isArray = true;
		}
		
		advanceIndex(1); 	//skip '='
		addExpression();	//Expression
		
		//pop var
		if(isArray)
		{
			writeLn("pop temp 0");
			writeLn("pop pointer 1");
			writeLn("push temp 0");
			writeLn("pop that 0");
		}
		else
		{
			if(classScope.getKind(varName).equals("field"))
				writeLn("pop this " + classScope.getNumber(varName));
			else
				popVarWrite(varName);
		}
			
		
	}

	//============================================================================================================
	private void addIfStatement()
	{ 
		int ifLableNumber = ifCounter;
		ifCounter++;
		advanceIndex(2);						//skip 'if' and '('
		addExpression();						//if(Expression)
		advanceIndex(1);						//skip ')'
		
		writeLn("if-goto IF_TRUE" + ifLableNumber);
		writeLn("goto IF_FALSE" + ifLableNumber);
		writeLn("label IF_TRUE" + ifLableNumber);
		
		advanceIndex(1);						//skip '{'
		addStatements();	 					//Statement
		advanceIndex(1);						//skip '}'
		
		if(tokEquals("else"))
		{
			advanceIndex(2);						//skip 'else' and '{'
			writeLn("goto IF_END"    + ifLableNumber);
			writeLn("label IF_FALSE" + ifLableNumber);
				addStatements(); 					//Statement
				writeLn("label IF_END"   + ifLableNumber);
			advanceIndex(1);						//skip '}'
		}
		else
		{
			writeLn("label IF_FALSE" + ifLableNumber);
		}
		
	}

	//============================================================================================================
	private void addWhileStatement()
	{		
		int whileLableNumber = whileCounter;
		whileCounter++;
		
		advanceIndex(2);							//skip 'while' and '('
		writeLn("label WHILE_EXP" + whileLableNumber);
		
		addExpression();
		
		advanceIndex(2);							//skip ')' and '{'
		writeLn("not");
		writeLn("if-goto  WHILE_END" + whileLableNumber);		
		
		addStatements();							//Statements
		
		writeLn("goto  WHILE_EXP" + whileLableNumber);
		writeLn("label WHILE_END" + whileLableNumber);
		
		advanceIndex(1);							//skip '}'
		
		
	}

	//============================================================================================================
	private void addDoStatement()
	{
		
		advanceIndex(1);		//skip 'do'
		addSubroutineCall();
		writeLn("pop temp 0"); 
		
	}

	//============================================================================================================
	private void addReturnStatement()
	{		
		advanceIndex(1);		//skip 'return'
		if(tokEquals(";"))
		{
			writeLn("push constant 0");
		}
		else
		{
			addExpression();
		}

		writeLn("return");
	}

	//============================================================================================================
	private void addExpression()
	{
		String op;
		
		addTerm();				// term
		while(isOp(index))
		{
			op = getTok();
			addTerm();			// term
			opWrite(op);		// op
		}
 	}

	//============================================================================================================
	private void addTerm()
	{		
		String unaryOp, varName;
		
		if	   (isIntegerConstant())	addIntegerConstant();
		else if(isStringConstant()) 	addStringConstant();
		else if(isKeywordConstant())	addKeywordConstant();
		else if (tokEquals("("))	// '(expression)'
		{
			advanceIndex(1); 		//skip '('
				addExpression();    //  Expression
			advanceIndex(1); 		//skip ')'
		}
		else if(isUnaryOp())
		{
			unaryOp = getTok();
			addTerm();             //
			UnaryOpWrite(unaryOp);          //
			
		}
		else
		{
			if(nextTokEquals("["))				//varName[expression]
			{
				varName = getTok();
				advanceIndex(1); 	//skip '['
				addExpression();
				
				if(classScope.isVarInTable(varName) && classScope.getKind(varName).equals("field"))
				{
					writeLn("push this " + classScope.getNumber(varName));
				}
				else if(subroutineScope.isVarInTable(varName))
				{
					if(subroutineScope.getKind(varName).equals("local"))
						writeLn("push local " + subroutineScope.getNumber(varName));
					else if(classScope.getKind(varName).equals("static"))
						writeLn("push static " + classScope.getNumber(varName));
				}
					
				writeLn("add");
	            writeLn("pop pointer 1");
	            writeLn("push that 0");
				advanceIndex(1); 	//skip ']'
			}
			else if(nextTokEquals(".") || nextTokEquals("(") )	// 'varName()' | 'varName.'
				addSubroutineCall();
			else
			{
				varName = getTok();
				if(subroutineScope.isVarInTable(varName))
					pushVarWrite(varName);
				else if(classScope.isVarInTable(varName) && classScope.getKind(varName).equals("field"))
					writeLn("push this " + classScope.getNumber(varName));
				else
					pushVarWrite(varName);
			}
			
		} 
		
	}

	//============================================================================================================
	private void addSubroutineCall()
	{		
		String funcClassName;
		String subroutineName;
		int numOfArgs = 0;
		
		if(isSubroutineName() && nextTokEquals("("))
		{
			subroutineName = getTok(); 	// SubroutineName
			if(methodScope.isVarInTable(subroutineName))
			{
				writeLn("push pointer 0");
				numOfArgs++;
			}
			advanceIndex(1); 		//skip '('
			numOfArgs += addExpressionList();   	//  ExpressionList
			advanceIndex(1); 		//skip ')'
			
			writeLn("call " + className + "." + subroutineName + " " + numOfArgs);
		}
		else
		{
			funcClassName = getTok();			//className | varName
			advanceIndex(1); 					//skip '.'
			subroutineName = getTok();			// SubroutineName
			advanceIndex(1); 					//skip '('
			
			if(subroutineScope.isVarInTable(funcClassName) && subroutineScope.getKind(funcClassName).equals("argument"))
			{
				numOfArgs++;
				pushVarWrite(funcClassName);
			}
			else if(subroutineScope.isVarInTable(funcClassName) && !isStrType(subroutineScope.getType(funcClassName)) )
			{
				numOfArgs++;
				pushVarWrite(funcClassName);
			}
			else if(classScope.isVarInTable(funcClassName) && classScope.getKind(funcClassName).equals("field"))
			{
				numOfArgs++;
				writeLn("push this "+ classScope.getNumber(funcClassName));
			}
			else if(classScope.isVarInTable(funcClassName) && classScope.getKind(funcClassName).equals("static"))
			{
				numOfArgs++;
				pushVarWrite(funcClassName);
			}
			
			numOfArgs += addExpressionList();   //  ExpressionList
			advanceIndex(1); 					//skip ')'
			
			if(subroutineScope.isVarInTable(funcClassName) && subroutineScope.getKind(funcClassName).equals("argument"))
			{
				writeLn("call " + subroutineScope.getType(funcClassName) + "." + subroutineName + " " + numOfArgs);
			}
			else if(subroutineScope.isVarInTable(funcClassName) && !isStrType(subroutineScope.getType(funcClassName)) )
			{
				writeLn("call " + subroutineScope.getType(funcClassName) + "." + subroutineName + " " + numOfArgs);
			}
			else if(classScope.isVarInTable(funcClassName) && classScope.getKind(funcClassName).equals("field"))
			{
				writeLn("call " + classScope.getType(funcClassName) + "." + subroutineName + " " + numOfArgs);
			}
			else if(classScope.isVarInTable(funcClassName) && classScope.getKind(funcClassName).equals("static"))
			{
				writeLn("call " + classScope.getType(funcClassName) + "." + subroutineName + " " + numOfArgs);
			}
			else
				writeLn("call " + funcClassName + "." + subroutineName + " " + numOfArgs);
		}
	}

	//============================================================================================================
	private int addExpressionList()
	{
		int expressionCounter = 0;
		
		if(isExpression(index))
		{
			if(!tokEquals(")"))
			{
				expressionCounter++;
				addExpression();  		// Expression
				while(tokEquals(","))
				{
					advanceIndex(1); 	//skip ','
					addExpression();  	// Expression
					expressionCounter++;
				}
			}
		}
		return expressionCounter;
	}

	//============================================================================================================
	private void addOp()
	{
		opWrite(getTok());	// '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
		
	}

	//============================================================================================================
	private void opWrite(String opToPrint)
	{
		switch(opToPrint)	// '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
		{
			case "+":	writeLn("add");	break;
			case "-":	writeLn("sub");	break;
			case "|":	writeLn("or");	break;
			case "=":	writeLn("eq");	break;
			case "&amp;":	writeLn("and");	break;
			case "&lt;":	writeLn("lt");	break;
			case "&gt;":	writeLn("gt");	break;
			case "*":	writeLn("call Math.multiply 2");   break;
			case "/":	writeLn("call Math.divide 2");	   break;
			case "^":	writeLn("call Math.power 2");	   break;
		}
	}
	
	//============================================================================================================
	private void addUnaryOp()
	{
		UnaryOpWrite(getTok());	// '-' | '~' 
		
	}

	//============================================================================================================
		private void UnaryOpWrite(String unaryOp)
		{
			//addTerm();
			switch (unaryOp)	// '-' | '~' 
			{
				case "-":	writeLn("neg");		break;
				case "~":	writeLn("not");		break;
			}
		}

	//============================================================================================================
	private void addKeywordConstant()
	{
		
		switch(getTok())	// 'true' | 'false' | 'null' | 'this'
		{
			case "this":  writeLn("push pointer 0");	break;
			case "true":  writeLn("push constant 0");	
						  writeLn("not");				break;
			case "false": writeLn("push constant 0");	break;
			case "null":  writeLn("push constant 0");	break;
			
		}
	}

	//============================================================================================================
	private void addIntegerConstant()
	{
		writeLn("push constant " + getTok());
	}

	//============================================================================================================
	private void addStringConstant()
	{
		
		//advanceIndex(1); 	//skip '"'
		String str = getTok();
		
		writeLn("push constant " + str.length());
		writeLn("call String.new 1");
		for(int i = 0 ; i < str.length() ; i++)		//convert to ascii
		{
			writeLn("push constant " + (int)str.charAt(i) );
			writeLn("call String.appendChar 2");   
		}
		//advanceIndex(1); 	//skip '"'
	}

	//============================================================================================================
	
	
	//============================================================================
	//============================================================================
	
	
	
	

	//============================================================================================================
	private boolean isClassVarDec()
	{
		if(tokEquals("static") || tokEquals("field"))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean IsSubroutineDec()
	{
		if(tokEquals("constructor") || tokEquals("function") || tokEquals("method"))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isVarDec()
	{
		if(tokEquals("var"))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isType()
	{
		if(tokEquals("int") || tokEquals("char") || tokEquals("boolean") || isClassName())
			return true;
		return false;
	}

	private boolean isStrType(String str)
	{
		if(str.equals("int") || str.equals("char") || str.equals("boolean"))
			return true;
		return false;
	}
	
	//============================================================================================================
	private boolean isClassName()
	{
		return isIdentifier(index);
	}

	//============================================================================================================
	private boolean isSubroutineName()
	{
		return isIdentifier(index);
	}

	//============================================================================================================
	private boolean isVarName(int _index)
	{
		return isIdentifier(_index);
	}

	//============================================================================================================
	private boolean isIdentifier(int _index)
	{
		if(tokTypeIs(IDENTIFIER))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isExpression(int _index)
	{
		return isTerm();
	}

	//============================================================================================================
	private boolean isTerm()
	{
		
		if(isIntegerConstant() || isStringConstant() || isKeywordConstant() || isVarName(index) || isSubroutineCall() || tokEquals("(") ||isUnaryOp())
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isOp(int _index)
	{
		if(tokEquals("+") || tokEquals("-") || tokEquals("*") || tokEquals("/") || tokEquals("&amp;") || tokEquals("|") || tokEquals("&lt;") || tokEquals("&gt;") || tokEquals("=") || tokEquals("^"))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isStatement(int _index)
	{
		if(tokEquals("let") || tokEquals("if") || tokEquals("while") || tokEquals("do") || tokEquals("return"))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isUnaryOp()
	{
		if(tokEquals("-") || tokEquals("~"))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isSubroutineCall()
	{
		if((isSubroutineName() && tok.tokens[index+1] == "(" ) || ( (isClassName()  || isVarName(index)) &&  tok.tokens[index+1] == "." ) )
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isIntegerConstant()
	{
		int num;
		try 
		{
			num = Integer.parseInt(tok.tokens[index]);
		} 
		catch (NumberFormatException e) 
		{
		    return false;
		}
		
		if(num >= 0 && num <= 32767)
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isStringConstant()
	{
		if(tokTypeIs(STRINGCONSTANT))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean isKeywordConstant()
	{
		if(tokTypeIs(KEYWORD))
			return true;
		return false;
	}

	//============================================================================================================
	
	
	
	
	//=============================================================================
	//=============================================================================
	 

	//============================================================================================================
	private String getTok()
	{
		return tok.tokens[index++];
	}

	//============================================================================================================

	private void write(String line)
	{
		try 
		{	
			bw.write(line + "");
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
		System.out.print(line);//////******************************************<><><><><><><><><><>
	}

	private void writeLn(String line)
	{
		try 
		{	
			bw.write(line + "\n");
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
	}

	//============================================================================================================
	private String getTypeByNum(int tokType)
	{
		switch(tokType)
		{
			case INTEGERCONSTANT: 	return "integerConstant";
			case KEYWORD: 			return "keyword";
			case IDENTIFIER: 		return "identifier";	
			case STRINGCONSTANT: 	return "stringConstant";
			case SYMBOL: 			return "symbol";
			default: 				return "";
		}
		
	}

	//============================================================================================================
	private boolean tokEquals(String token)
	{
		if(tok.tokens[index].equals(token))
			return true;
		return false;
	}

	//============================================================================================================
	private boolean nextTokEquals(String token)
	{
		if(tok.tokens[index + 1].equals(token))
			return true;
		return false;
	}
	
	//============================================================================================================
	private boolean tokTypeIs(int tokType)
	{
		if(tok.tokens_type[index] == tokType)
			return true;
		return false;
	}

	//============================================================================================================
	private void advanceIndex(int advanceBy)
	{
		if(advanceBy > 0)
			index += advanceBy;
	}
	
	
	//============================================================================================================
	private void addToSubroutineScope(String kind)
	{
		String type = getTok();	// int | bool | cahr | var | 
		if(kind.equals("var"))	kind = "local";
		String name = getTok();	//var name
		
		subroutineScope.addRaw(name, type, kind);		
	}
	

	//============================================================================================================
	private void pushVarWrite(String varName)
	{
		if(subroutineScope.isVarInTable(varName))
			writeLn("push " +subroutineScope.getKind(varName) + " " + subroutineScope.getNumber(varName));
		else if(classScope.isVarInTable(varName))
			writeLn("push " +classScope.getKind(varName) + " " + classScope.getNumber(varName));
	}
	
	//============================================================================================================
	private void popVarWrite(String varName)
	{
		if(subroutineScope.isVarInTable(varName))
			writeLn("pop " +subroutineScope.getKind(varName) + " " + subroutineScope.getNumber(varName));
		else if(classScope.isVarInTable(varName))
			writeLn("pop " +classScope.getKind(varName) + " " + classScope.getNumber(varName));
	}
	
	
}
