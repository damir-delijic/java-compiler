import java.util.LinkedList;

public class Parser {
	private static final int  // token codes
	none	= 0, eof	= 1, let_	= 2, in_	= 3, end	= 4,
	
	if_		= 5, fi_	= 6, else_	= 7, while_  = 8, for_  = 9,
	
	break_	= 10, print_= 11, readint_	= 12, readstring_= 13,
	
	readbool_	= 14, readdouble_ = 15, semicolon_ = 16, ident_	= 17, 
	
	integer_	= 18, bool_	= 19, string_	= 20, plus_	= 21,
	
	minus_	= 22, asterisk_	= 23, slash_	= 24, mod_	= 25, 
	
	period_	= 26, less_	= 27, lessoreq_	= 28, greater_ 	= 29, 
	
	greateroreq_= 30,equal_	= 31, notequal_	= 32, and_	= 33, 
	
	or_	= 34, not_	= 35, intConstant_ = 36, doubleConstant_ = 37,
	
	boolConstant_ = 38, stringConstant_ = 39, lpar_	= 40, rpar_	= 41,
	
	assign_	= 42, comma_	= 43, double_	= 44, then_	= 45, endfor_ = 46, endwhile_ = 47, repeat_ = 48, until_ = 49;
	
	;
	private static final String[] name = { // token names for error messages
		"none", "eof", "let", "in", "end", "if", "fi", "else", "while","for",
		"break", "print", "readint", "readstring", "readbool", "readdouble", "semicolon", "ident", "integer",
		"bool", "string", "plus", "minus", "asterisk", "slash", "mod","period", "less", "lessoreq", "greater", "greateroreq",
		"equal", "notequal", "and", "or", "not", "intConstant", "doubleConstant", "boolConstant", "stringConstant",
		"lpar", "rpar", "assign", "comma", "double", "then", "endfor", "endwhile", "repeat", "until"
		};

	private static Token t;			// current token (recently recognized)
	private static Token la;		// lookahead token
	private static int sym;			// always contains la.kind ( token code for the lookahead token)
	public  static int errors;  	// error counter
	private static int errDist;		// no. of correctly recognized tokens since last error
	private static int insideLoop = 0;
	private static AST ast;

	//------------------- auxiliary methods ----------------------
	private static void scan() {
		t = la;
		la = Scanner.next();
		sym = la.kind;
		errDist++;
		/*
		System.out.print("line " + la.line + ", col " + la.col + ": " + name[sym]);
		if (sym == ident) System.out.print(" (" + la.string + ")");
		if (sym == number || sym == charCon) System.out.print(" (" + la.val + ")");
		System.out.println();*/
	}

	private static void check(int expected) {
		if (sym == expected) scan();
		else error(name[expected] + " expected");
	}

	public static void error(String msg) { // syntactic error at token la
		if (errDist >= 3) {
			System.out.println("-- line " + la.line + " col " + la.col + ": " + msg);
			errors++;
		}
		errDist = 0;
	}

	//-------------- parsing methods (in alphabetical order) -----------------

	private static AST.Program program() {
		AST.Program temp = new AST.Program();
		check(let_);
		temp.declarations = declarations();
		check(in_);
		temp.commandSequence = commandSequence();
		check(end);
		return temp; 
	}
	
	private static AST.Declarations declarations() {
		AST.Declarations temp = new AST.Declarations();
		temp.declarationsList = new LinkedList<AST.Declaration>();
		temp.declarationsList.add(decl());
		while(sym != in_) {
			temp.declarationsList.add(decl());
		}
		return temp;
	}
	
	private static AST.Declaration decl() {
		AST.Declaration temp = new AST.Declaration();
		temp.type = type();
		check(ident_);
		temp.id = new AST.Identifier(t.string);
		check(semicolon_);
		return temp;
	}
	/*private static void decl() {
		variableDecl();
	}
	
	private static void variableDecl() {
		variable();
		check(semicolon_);
	}
	
	private static void variable() {
		type();
		check(ident_);
	}*/
	
	private static AST.Type type() {
		if(sym == integer_) {
			scan();
			return new AST.Type("Integer");
		}else if(sym == double_) {
			scan();
			return new AST.Type("Double");
		}else if(sym == string_) {
			scan();
			return new AST.Type("String");
		}else if(sym == bool_) {
			scan();
			return new AST.Type("Boolean");
		}else {
			error("Unknown type of variable!");
			return null;
		}
	}
	
	private static AST.CommandSequence commandSequence() {
		AST.CommandSequence temp = new AST.CommandSequence();
		temp.commandList = new LinkedList<AST.Command>();
		while(sym != end && sym != endwhile_ && sym != endfor_ && sym != fi_ && sym != else_ && sym != until_) {
			temp.commandList.add(stmt());
		} 
		return temp;
	}
	
	private static AST.Command stmt() {
		AST.Command temp;
		if(sym == if_) {	
			temp = ifStmt();
		}else if(sym == while_) {
			temp = whileStmt();
		}else if(sym == for_) {
			temp = forStmt();
		}else if(sym == break_) {
			temp = breakStmt();
		}else if(sym == print_) {
			temp = printStmt();
		}else if(sym == repeat_) {
			temp = repeatStmt();
		}else if(sym == ident_) {
			temp = assignStmt();
			//TODO provjera da li je promjenljiva deklarisana?
		}else if((readint_ <= sym && sym <= readdouble_)){
			AST.Type tempType;
			if(sym == readint_) tempType = new AST.Type("Integer");
			else if(sym == readdouble_) tempType = new AST.Type("Double");
			else if(sym == readbool_) tempType = new AST.Type("Boolean");
			else tempType = new AST.Type("String");
			
			temp = new AST.ReadCommand(tempType);
		}else {
			error("Unknown statement!");
			temp = null;
		}
		check(semicolon_);
		return temp;
	}
	
	private static AST.IfCommand ifStmt() {
		AST.IfCommand temp = new AST.IfCommand();
		check(if_);
		check(lpar_);
		temp.expression = exprTypeOne();
		check(rpar_);
		check(then_);
		temp.ifCommandSequence = commandSequence();
		if(sym == fi_) {
			scan();
		}else {
			check(else_);
			temp.elseCommandSequence = commandSequence();
			check(fi_);
		}
		return temp;
	}
	
	private static AST.WhileCommand whileStmt() {
		AST.WhileCommand temp = new AST.WhileCommand();
		check(while_);
		check(lpar_);
		temp.expression = exprTypeOne();
		check(rpar_);
		insideLoop++;
		temp.whileCommandSequence = commandSequence();
		insideLoop--;
		check(endwhile_);
		return temp;
	}
	
	private static AST.ForCommand forStmt() {
		AST.ForCommand temp = new AST.ForCommand();
		check(for_);
		check(lpar_);
		temp.a1 = assignStmt();
		check(semicolon_);
		temp.expr = exprTypeOne();
		check(semicolon_);
		temp.a2 = assignStmt();
		check(rpar_);
		insideLoop++;
		temp.forCommandSequence = commandSequence();
		insideLoop--;
		check(endfor_);
		return temp;
	}
	
	private static AST.BreakCommand breakStmt() {
		AST.BreakCommand temp = new AST.BreakCommand();
		if(insideLoop > 0) {
			check(break_);
			temp.belongsToALoop = true;
		}else {
			error("Break statement does not belong to a loop.");
			temp.belongsToALoop = false;
		}
		return temp;
	}
	
	private static AST.RepeatCommand repeatStmt(){
		scan();
		check(semicolon_);
		AST.RepeatCommand temp = new AST.RepeatCommand();
		temp.repeatCommandSequence = commandSequence();
		check(until_);
		check(lpar_);
		temp.expression = exprTypeOne();
		check(rpar_);
		return temp;
	}
	
	private static AST.PrintCommand printStmt() {
		AST.PrintCommand temp = new AST.PrintCommand();
		check(print_);
		check(lpar_);
		temp.printExpression = exprTypeOne();
		check(rpar_);
		return temp;
	}
	
	private static AST.Expression exprTypeOne() {
		AST.Expression e2 = exprTypeTwo();
		if(e2 == null) return null;
		String operator = "";
		if(sym == and_) operator = "&&";
		if(sym == or_) operator = "||";
		AST.Expression e1 = restOfExprTypeOne();
		if(e1 == null) return e2;
		AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
		temp.firstExpression = e2;
		temp.secondExpression = e1;
		temp.binaryOperator = new String(operator);
		return temp;
	}
	
	private static AST.Expression restOfExprTypeOne() {
		if (sym == and_ || sym == or_) {
			scan();
			AST.Expression e2 = exprTypeTwo();
			if(e2 == null) return null;
			String operator = "";
			if(sym == and_) operator = "&&";
			if(sym == or_) operator = "||";
			AST.Expression e1 = restOfExprTypeOne();
			if(e1 == null) return e2;
			AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
			temp.firstExpression = e2;
			temp.secondExpression = e1;
			temp.binaryOperator = new String(operator);
			return temp;
		}else return null;
	}
	private static AST.Expression exprTypeTwo() {
		AST.Expression e2 = exprTypeThree();
		if(e2 == null) return null;
		String operator = "";
		if(sym == equal_) operator = "==";
		if(sym == notequal_) operator = "!=";
		AST.Expression e1 = restOfExprTypeTwo();
		if(e1 == null) return e2;
		AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
		temp.firstExpression = e2;
		temp.secondExpression = e1;
		temp.binaryOperator = new String(operator);
		return temp;
	}
	
	private static AST.Expression restOfExprTypeTwo() {
		if( sym == equal_ || sym == notequal_) {
			scan();
			return exprTypeThree();//?nisam siguran
		}else return null;
	}

	private static AST.Expression exprTypeThree() {
		AST.Expression e2 = exprTypeFour();
		if(e2 == null) return null;
		String operator = "";
		if(sym == less_) operator = "<";
		if(sym == lessoreq_) operator = "<=";
		if(sym == greater_) operator = ">";
		if(sym == greateroreq_) operator = ">=";
		AST.Expression e1 = restOfExprTypeThree();
		if(e1 == null) return e2;
		AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
		temp.firstExpression = e2;
		temp.secondExpression = e1;
		temp.binaryOperator = new String(operator);
		return temp;
	}
	
	private static AST.Expression restOfExprTypeThree() {
		if( sym == less_ || sym == lessoreq_ || sym == greater_ || sym == greateroreq_) {
			scan();
			return exprTypeFour();//? nisam siguran
		}else return null;
	}

	private static AST.Expression exprTypeFour() {
		AST.Expression e2 = exprTypeFive();
		if(e2 == null) return null;
		String operator = "";
		if(sym == plus_) operator = "+";
		if(sym == minus_) operator = "-";
		AST.Expression e1 = restOfExprTypeFour();
		if(e1 == null) return e2;
		AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
		temp.firstExpression = e2;
		temp.secondExpression = e1;
		temp.binaryOperator = new String(operator);
		return temp; 
	}

	private static AST.Expression restOfExprTypeFour() {
		if(sym == plus_ || sym == minus_) {
			scan();
			AST.Expression e2 = exprTypeFive();
			String operator = "";
			if(sym == plus_) operator = "+";
			if(sym == minus_) operator = "-";
			AST.Expression e1 = restOfExprTypeFour();
			if(e1 == null) return e2;
			AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
			temp.firstExpression = e2;
			temp.secondExpression = e1;
			temp.binaryOperator = new String(operator);
			return temp; 
		}else return null;
	}

	private static AST.Expression exprTypeFive() {
		AST.Expression e2 = exprTypeSix();
		if(e2 == null) return null;
		String operator = "";
		if(sym == asterisk_) operator = "*";
		if(sym == mod_) operator = "%";
		if(sym == slash_) operator = "/";
		AST.Expression e1 = restOfExprTypeFive();
		if(e1 == null) return e2;
		AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
		temp.firstExpression = e2;
		temp.secondExpression = e1;
		temp.binaryOperator = new String(operator);
		return temp;
	}

	private static AST.Expression restOfExprTypeFive() {
		if(sym == asterisk_ || sym == mod_ || sym == slash_) {
			scan();
			AST.Expression e2 = exprTypeSix();
			if(e2 == null) return null;
			String operator = "";
			if(sym == asterisk_) operator = "*";
			if(sym == mod_) operator = "%";
			if(sym == slash_) operator = "/";
			AST.Expression e1 = restOfExprTypeFive();
			if(e1 == null) return e2;
			AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
			temp.firstExpression = e2;
			temp.secondExpression = e1;
			temp.binaryOperator = new String(operator);
			return temp;
		}else return null;
	}
	
	private static AST.Expression exprTypeSix() {
		if(sym == not_ || sym == minus_) {
			scan();
			AST.UnaryOperatorExpression e = new AST.UnaryOperatorExpression();
			if(t.kind == not_) e.unaryOperator = "!";
			else e.unaryOperator = "-";
			e.expression = exprTypeSeven();
			return e;
		}else return exprTypeSeven();
	}
	
	private static AST.Expression exprTypeSeven() {
		AST.Expression temp;
		if(sym == intConstant_ || sym == boolConstant_ || sym == stringConstant_ || sym == doubleConstant_) {
			scan();
			AST.Type tempType;
			String v;
			if(t.kind == intConstant_) {
				tempType = new AST.Type("Integer");
				v = Integer.toString(t.val);
			}
			else if(t.kind == doubleConstant_) {
				tempType = new AST.Type("Double");
				v  = Double.toString(t.d_val);
			}
			else if(t.kind == boolConstant_) {
				tempType = new AST.Type("Boolean");
				v = Boolean.toString(t.b_val);
			}
			else {
				tempType = new AST.Type("String");
				v = t.s_val;
			}
			temp = new AST.Constant(tempType, v);
		}else if(sym == ident_) {
			scan();
			temp = new AST.Identifier(t.string);
		}else if(sym == lpar_) {
			scan();
			check(rpar_);
			temp = exprTypeOne();
		}else if(sym == readint_ || sym == readbool_ || sym == readstring_ || sym == readdouble_) {
			scan();
			AST.Type tempType;
			if(t.kind == readint_) tempType = new AST.Type("Integer");
			else if(t.kind == readdouble_) tempType = new AST.Type("Double");
			else if(t.kind == readbool_) tempType = new AST.Type("Boolean");
			else tempType = new AST.Type("String");
			check(lpar_);
			check(rpar_);
			temp = new AST.ReadExpression(tempType);
		}else {
			error("Pogresan terminalni char?");
			temp = null;
		}
		return temp;
	}
	//Prioriteti idu od 1 do 7, gdje je 1 najnizeg prioriteta
	
	private static AST.AssignCommand assignStmt() {
		AST.AssignCommand temp = new AST.AssignCommand();
		temp.id = new AST.Identifier(la.string);
		temp.expression = assignExpr();
		return temp;
	}
	
	private static AST.Expression assignExpr() {
		scan();
		check(assign_);
		return exprTypeOne();
	}

	/*private static void lValue() {
		check(ident_);
	}*/
	/*
	private static void constant() {
		if(sym == intConstant_) scan();
		else if(sym == doubleConstant_) scan();
		else if(sym == stringConstant_) scan();
		else if(sym == boolConstant_) scan();
		else error("Unknown constant!");
	}*/

	public static void parse() {	
		// start parsing
		errors = 0; errDist = 3;
		scan();
		ast = new AST();
		ast.program = program();
		System.out.println(ast);
		if (sym != eof) error("end of file found before end of program");
	}

}


