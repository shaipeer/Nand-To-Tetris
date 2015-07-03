/**
   |====================================================|
   |	  	Exercise #  :	 2							|
   |													|
   |   	   	File name   :	 JackParser.java			|
   |		Date		:	 23/03/2015    	      		|
   |		Author    	:	 Shai Pe'er        			|
   |		Email     	:	 shaip86@gmail.com 			|
   |====================================================|
 */

import java.io.BufferedWriter;
import java.io.IOException;


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
	
	public JackParser()
	{
		index = 0;
	}
	
	
	public void parseTok(JackTokenizer _tok, BufferedWriter _bw)
	{
		tok = _tok;
		bw  = _bw;
		index = 0;
		
		addClass();
	}
	
	
	private void addClass()
	{
		openTag("class");
			addTok();						// class
			addClassName(); 				//className
			addTok();						// {
				while(isClassVarDec())		{	addClassVarDec();	}
				while(IsSubroutineDec())	{	addSubroutineDec();	}
			addTok();						// }
		closeTag("class");
	}
	
	private void addClassVarDec()
	{
		openTag("classVarDec");
			addTok();					// static | field
			addType();					// type
			addVarName();				// varName
			while(tokEquals(","))
			{
				addTok();				//,
				addVarName();			// varName
			}
			addTok();					// ;
		closeTag("classVarDec");
	}
	
	private void addType()
	{
		openTag("type");
			if(tokEquals("int") | tokEquals("char") | tokEquals("boolean"))
				addTok();		// int | char | boolean | className
			else
				addClassName();
		closeTag("type");
	}
	
	private void addSubroutineDec()
	{
		openTag("subroutineDec");
			addTok();		  	// constructor | function | method
			if(tokEquals("void"))
				addTok();		// void
			else
				addType();			// type
			addSubroutineName();	// SubroutineName
			addTok();			// (
				addParameterList(); //  ParameterList
			addTok();		  	// )
				addSubroutineBody();// SubroutineBody
		closeTag("subroutineDec");
	}
	
	private void addParameterList()
	{
		openTag("parameterList");
			if (isType()) addType();				// type
			if(isVarName(index)) addVarName();			// VarName
			while(tokEquals(","))
			{
				addTok();		// ,
				addType();			// type
				addVarName();		// VarName
			}
		closeTag("parameterList");
	}
	private void addSubroutineBody()
	{
		openTag("subroutineBody");
			addTok();				// {
			while(isVarDec())
				addVarDec();		// VarDec
			addStatements();		// Statements
			addTok();				// }
		closeTag("subroutineBody");
	}
	private void addVarDec()
	{
		openTag("varDec");
			addTok();				// var
			addType();    			// type
			addVarName();			// VarName
			while(tokEquals(","))
			{
				addTok();			// ,
				addVarName(); 		// VarName
			}
			addTok();				// ;
		closeTag("varDec");
	}
	private void addClassName()
	{
		addTok();					// identifier
	}
	private void addSubroutineName()
	{
		openTag("subroutineName");
			addTok();				// identifier
		closeTag("subroutineName");
	}
	private void addVarName()
	{
			addTok();				// identifier
	}
	private void addStatements()
	{
		openTag("statements");
			while(isStatement(index))
				addStatement(); 	//Statement
		closeTag("statements");
	}
	private void addStatement()
	{
		openTag("statement");
			switch(tok.tokens[index])	// ifStatement | doStatement | letStatement | whileStatement | returnStatement
			{
				case "if":		addIfStatement();		break;
				case "do":		addDoStatement();		break;
				case "let":		addLetStatement();		break;
				case "while":	addWhileStatement();	break;
				case "return":	addReturnStatement();	break;
			}
		closeTag("statement");
	}
	
	private void addLetStatement()
	{
		openTag("letStatement");
			addTok();				// let
			addVarName();			// VarName
			if(tokEquals("["))
			{
				addTok();		// [
					addExpression();
				addTok();			// ]
			}
			addTok();				// =
			addExpression();		//Expression
			addTok();				// ;
		closeTag("letStatement");
	}
	private void addIfStatement()
	{
		openTag("ifStatement");
			addTok();				// if
			addTok();				// (
				addExpression();	//  Expression
			addTok();				// )
			addTok();				// {
				addStatements(); 	//  Statement
			addTok();				// }
			if(tokEquals("else"))
			{
				addTok();		 		// else
				addTok();		 		// {
					addStatements();	//  Statements 	
				addTok();		 		// }
			}
		closeTag("ifStatement");
	}
	private void addWhileStatement()
	{
		openTag("whileStatement");
			addTok();				// while
			addTok();				// (
				addExpression();
			addTok();				// )
			addTok();				// {
				addStatements();
			addTok();				// }
		closeTag("whileStatement");
	}
	private void addDoStatement()
	{
		openTag("doStatement");
			addTok();				// do
			addSubroutineCall();
			addTok();				// ;
		closeTag("doStatement");
	}
	private void addReturnStatement()
	{
		openTag("returnStatement");
			addTok();				// return
			if(isExpression(index))	addExpression();
			addTok();				// ;
		closeTag("returnStatement");
	}
	
	private void addExpression()
	{
		openTag("expression");
			addTerm();				// term
			while(isOp(index))
			{
				addOp();			// op
				addTerm();			// term
			}
		closeTag("expression");	
	}
	private void addTerm()
	{
		openTag("term");
			if	   (isIntegerConstant())	addIntegerConstant();
			else if(isStringConstant()) 	addStringConstant();
			else if(isKeywordConstant())	addKeywordConstant();
			else if(isVarName(index))
			{
				if(nextTokEquals("["))
				{
					addVarName();
					addTok();	//[
						addExpression();
					addTok();	//]
				}
				else if(nextTokEquals(".") || nextTokEquals("(") )
					addSubroutineCall();
				else
					addVarName();
		
			} 
			//(isClassName() || isVarName(index)) &&
			else if (tokEquals("("))
			{
				addTok();				// (
					addExpression();    //  Expression  
				addTok();				// )
			}
			else if(isUnaryOp())
			{
				addUnaryOp();          //
				addTerm();             //
			}
		
		
		closeTag("term");
	}
	

	private void addSubroutineCall()
	{
		openTag("subroutineCall");
			if(isSubroutineName() && nextTokEquals("("))
			{
				addSubroutineName();	// SubroutineName
				addTok();				// (
				addExpressionList();   	//  ExpressionList
				addTok();				// )
			}
			else
			{
				if	   (isClassName())		addClassName();			//ClassName
				else if(isVarName(index))	addVarName();			// VarName
				
				addTok();					// .
				addSubroutineName();   		// 
				addTok();					// (
				addExpressionList();   		//  ExpressionList
				addTok();					// )
			}
		closeTag("subroutineCall");
	}
	private void addExpressionList()
	{
		openTag("expressionList");
			if(isExpression(index))
			{
				addExpression();  		// Expression
				while(tokEquals(","))
				{
					addTok();			// ,
					addExpression();  	// Expression
				}
			}
		closeTag("expressionList");
	}
	private void addOp()
	{
		openTag("op");
			addTok();					// '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
		closeTag("op");
	}
	private void addUnaryOp()
	{
		openTag("unaryOp");
			addTok();					// '-' | '~'
		closeTag("unaryOp");
	}
	private void addKeywordConstant()
	{
		openTag("keywordConstant");
			addTok();					// 'true' | 'false' | 'null' | 'this'
		closeTag("keywordConstant");
	}
	
	
	private void addIntegerConstant()
	{
		addTok();
	}
	
	private void addStringConstant()
	{
		addTok();
	}
	
	
	//=============================================================================
	//=============================================================================
	
	
	private boolean isClassVarDec()
	{
		if(tokEquals("static") || tokEquals("field"))
			return true;
		return false;
	}
	
	private boolean IsSubroutineDec()
	{
		if(tokEquals("constructor") || tokEquals("function") || tokEquals("method"))
			return true;
		return false;
	}
	
	private boolean isVarDec()
	{
		if(tokEquals("var"))
			return true;
		return false;
	}
	
	private boolean isType()
	{
		if(tokEquals("int") || tokEquals("char") || tokEquals("boolean") || isClassName())
			return true;
		return false;
	}
	
	private boolean isClassName()
	{
		return isIdentifier(index);
	}
	
	private boolean isSubroutineName()
	{
		return isIdentifier(index);
	}
	
	private boolean isVarName(int _index)
	{
		return isIdentifier(_index);
	}
	
	private boolean isIdentifier(int _index)
	{
		if(tokTypeIs(IDENTIFIER))
			return true;
		return false;
	}
	
	private boolean isExpression(int _index)
	{
		return isTerm();
	}
	
	private boolean isTerm()
	{
		
		if(isIntegerConstant() || isStringConstant() || isKeywordConstant() || isVarName(index) || isSubroutineCall() || tokEquals("(") ||isUnaryOp())
			return true;
		return false;
	}
	private boolean isOp(int _index)
	{
		if(tokEquals("+") || tokEquals("-") || tokEquals("*") || tokEquals("/") || tokEquals("&amp;") || tokEquals("|") || tokEquals("&lt;") || tokEquals("&gt;") || tokEquals("=") || tokEquals("^"))
			return true;
		return false;
	}
	
	private boolean isStatement(int _index)
	{
		if(tokEquals("let") || tokEquals("if") || tokEquals("while") || tokEquals("do") || tokEquals("return"))
			return true;
		return false;
	}
	
	private boolean isUnaryOp()
	{
		if(tokEquals("-") || tokEquals("~"))
			return true;
		return false;
	}
	private boolean isSubroutineCall()
	{
		if((isSubroutineName() && tok.tokens[index+1] == "(" ) || ( (isClassName()  || isVarName(index)) &&  tok.tokens[index+1] == "." ) )
			return true;
		return false;
	}
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
	private boolean isStringConstant()
	{
		if(tokTypeIs(STRINGCONSTANT))
			return true;
		return false;
	}
	private boolean isKeywordConstant()
	{
		if(tokTypeIs(KEYWORD))
			return true;
		return false;
	}
	
	
	
	//=============================================================================
	//=============================================================================
	
	private void openTag(String tag)
	{
		writeToFile("<" + tag + ">");
	}
	
	private void closeTag(String tag)
	{
		writeToFile("</" + tag + ">");
	}
	
	private void addTok()
	{
		String s = "<" +  getTagByNum(tok.tokens_type[index]) + ">";
		
		s+=tok.tokens[index];
		
		s+= "</" + getTagByNum(tok.tokens_type[index]) + ">";
		
		writeToFile(s);
		index++;
	} 
	
	private void writeToFile(String line)
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
	
	private String getTagByNum(int tagType)
	{
		switch(tagType)
		{
			case INTEGERCONSTANT: 	return "integerConstant";
			case KEYWORD: 			return "keyword";
			case IDENTIFIER: 		return "identifier";	
			case STRINGCONSTANT: 	return "stringConstant";
			case SYMBOL: 			return "symbol";
			default: 				return "";
		}
		
	}
	
	private boolean tokEquals(String token)
	{
		if(tok.tokens[index].equals(token))
			return true;
		return false;
	}
	
	private boolean nextTokEquals(String token)
	{
		if(tok.tokens[index + 1].equals(token))
			return true;
		return false;
	}
	
	private boolean tokTypeIs(int tokType)
	{
		if(tok.tokens_type[index] == tokType)
			return true;
		return false;
	}
}
