
import java.io.*;
import java.util.Arrays;

public class Scanner {
	private static final char eofCh = '\u0080';
	private static final char eol = '\n';
	private static final int  // token codes
		none	  = 0,
		eof       = 1,
		let_      = 2,
		in_	  	  = 3,
		end_	  = 4,
		//start of keywords
		if_		  = 5,
		fi_		  = 6,
		else_	  = 7,
		while_    = 8,
		for_      = 9,
		break_    = 10,
		
		print_    = 11,
		readint_  = 12,
		readstring_ = 13,
		readbool_   = 14,
		readdouble_ = 15,
		//end of keywords
		semicolon_ = 16,
		ident_     = 17,
		integer_   = 18,
		bool_      = 19,
		string_    = 20,
		plus_      = 21,
		minus_ 	   = 22,
		asterisk_  = 23,
		slash_     = 24,
		mod_       = 25,
		period_    = 26,
		less_      = 27,
		lessoreq_  = 28,
		greater_   = 29,
		greateroreq_= 30,
		equal_     = 31,
		notequal_  = 32,
		and_       = 33,
		or_        = 34,
		not_       = 35,
		intConstant_ = 36,
		doubleConstant_ = 37,
		boolConstant_ = 38,
		stringConstant_ = 39,
		lpar_      = 40,
		rpar_      = 41,
		assign_    = 42,
		comma_	   = 43,
		double_    = 44,
		then_      = 45,
		endfor_    = 46,//moraju da bi se mogla realizvota rekruzija funkcije commandsequeence i jos po nesto kao npr break
		endwhile_  = 47,
		repeat_ = 48,
		until_ = 49
		;
	
	static final String key[] = { // sorted list of keywords
		"BREAK", "ELSE", "END", "ENDFOR", "ENDWHILE", "FI", "FOR",
		"IF", "IN", "LET", "PRINT", "READBOOL",
		"READDOUBLE", "READINT", "READSTRING",
		"REPEAT", "FALSE", "THEN", "TRUE", "UNTIL", "WHILE"
	};
	private static final int keyVal[] = {
		break_, else_, end_, endfor_, endwhile_, fi_, for_,
		if_, in_, let_, print_, readbool_,
		readdouble_, readint_, readstring_,
		repeat_, boolConstant_, then_, boolConstant_, until_, while_//mora ovako, zbog nejasnoca u zadatku?(prvobitno ej trebalo da bude true false)
	};

	private static char lookaheadch;			// lookahead character
	public  static int col;			// current column
	public  static int line;		// current line
	@SuppressWarnings("unused")
	private static int pos;			// current position from start of source file
	private static Reader in;    	// source file reader
	@SuppressWarnings("unused")
	private static char[] lex;	    // current lexeme (token string)

	//----- nextCh = next input character
	private static void nextCh() {
		try {
			lookaheadch = (char)in.read(); col++; pos++;
			if (lookaheadch == eol) {line++; col = 0;}
			else if (lookaheadch == '\uffff') lookaheadch = eofCh;
		} catch (IOException e) {
			lookaheadch = eofCh;
		}
	}

	//--------- Initialize scanner
	public static void init(Reader r) {
		in = new BufferedReader(r);
		lex = new char[64];
		line = 1; col = 0;
		nextCh();
	}

	//---------- Return next input token
	public static Token next() {
		// add your code here
		while (lookaheadch <= ' ') nextCh(); 
		Token t = new Token(); t.line = line; t.col = col;
		switch (lookaheadch) {
				//names, keywords
				case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i': case 'j':
				case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': case 's': case 't':
				case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
				case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J':
				case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T':
				case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z':
					t = readName(t);
					break;
				//numbers
				case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
					t = readNumber(t);
					break;
				//simple tokens
				case ';': nextCh(); t.kind = semicolon_; break;
				case '.': nextCh(); t.kind = period_; break;
				case ',': nextCh(); t.kind = comma_; break;
				case '+': nextCh(); t.kind = plus_; break;
				case '-': nextCh(); t.kind = minus_; break;
				case '*': nextCh(); t.kind = asterisk_; break;
				case '%': nextCh(); t.kind = mod_; break;
				case '(': nextCh(); t.kind = lpar_; break;
				case ')': nextCh(); t.kind = rpar_; break;
				case '"': nextCh();	t = readString(t); break;
				case eofCh: t.kind = eof; break;
				//compound tokens
				case '=': nextCh(); 
					if(lookaheadch == '=') {
						nextCh();
						t.kind = equal_;
					}else t.kind = assign_;
					break;
				case '<':
					nextCh();
					if ( lookaheadch == '=') {
						nextCh();
						t.kind = lessoreq_;
					}
					else t.kind = less_;
					break;
				case '>':
					nextCh();
					if ( lookaheadch == '=') {
						nextCh();
						t.kind = greateroreq_;
					}
					else t.kind = greater_;
					break;
				case '!':
					nextCh();
					if (lookaheadch == '=') {
						nextCh();
						t.kind = notequal_;
					}
					else t.kind = not_;
					break;
				case '&':
					nextCh();
					if( lookaheadch == '&') {
						nextCh();
						t.kind = and_;
					}
					else t.kind = none;
					break;
				case '|':
					nextCh();
					if( lookaheadch == '|') {
						nextCh();
						t.kind = or_;
					}
					else t.kind = none;
					break;
				//comments and slash
				case '/': nextCh();
						if(lookaheadch == '/') {
							nextCh();
							while(lookaheadch != '\n' && lookaheadch != eofCh) nextCh();
							t = next();
						}else if(lookaheadch == '*') {
							nextCh();
							while(true) {
								while(lookaheadch != '*' && lookaheadch != eofCh) nextCh();
								//naisao je na novu zvjezdicu ili kraj fajla
								if(lookaheadch == '*') {
									nextCh();
									if(lookaheadch == '/') {
										nextCh();
										t = next();
										break;
									}else continue;
								}else {
									t = next();
									break;
								}
							}
						}else t.kind = slash_;
						break;
				//invalid characters
				default: nextCh(); t.kind = none; break;
		}
		return t;
	}
	
	private static Token readName(Token t) {
		t.string += lookaheadch;
		nextCh();
		while(('0' <= lookaheadch && lookaheadch <= '9') || (lookaheadch == '_') 
				|| ('A' <= lookaheadch && lookaheadch <= 'Z') || ('a' <= lookaheadch && lookaheadch <= 'z') ) {
			t.string += lookaheadch;
			nextCh();
		}
		int index = Arrays.binarySearch(key, t.string);
		if (index > -1) {
			t.kind = keyVal[index];
			if(t.string.contentEquals("TRUE")) {
				t.b_val = true;
			}else if(t.string.contentEquals("FALSE")) {
				t.b_val = false;
			}
		}
		else {
			if(t.string.contentEquals("integer")) t.kind = integer_;
			else if(t.string.contentEquals("double")) t.kind = double_;
			else if(t.string.contentEquals("bool")) t.kind = bool_;
			else if(t.string.contentEquals("string")) t.kind = string_;
			else {
				t.kind = ident_;
				if(t.string.length() > 31) {
					t.kind = none;
					t.string = "";
				}
			}
		}
		return t;
	}
	
	private static Token readNumber(Token t) {
		String temp = "" + lookaheadch;
		nextCh();
		if( temp.charAt(0) == '0' && (lookaheadch == 'x' || lookaheadch == 'X')) {
			//int hex
			while(('0' <= lookaheadch && lookaheadch <= '9') || ('A' <= lookaheadch && lookaheadch <= 'F') 
					|| ('a' <= lookaheadch && lookaheadch <= 'f')) {
				temp += lookaheadch;
				nextCh();
			}
			t.kind = intConstant_;
			t.string = temp;
			try {
				t.val = Integer.parseInt(temp.substring(2),16);
			}catch(NumberFormatException e) {
				t.kind = none;
				t.val = 0;
				t.string = "";
			}
		}else {
			//int or double dec
			while('0' <= lookaheadch && lookaheadch <= '9') {
				temp += lookaheadch;
				nextCh();
			}
			if(lookaheadch == '.') {
				//double
				while(('0' <= lookaheadch && lookaheadch <= '9') || ('E' == lookaheadch || lookaheadch == 'e') 
						|| ('+' == lookaheadch || lookaheadch == '-')) {
					temp += lookaheadch;
					nextCh();
				}
				t.kind = doubleConstant_;
				t.string = temp;
				try {
					t.d_val = Double.parseDouble(temp);
				}catch(NumberFormatException e){
					t.d_val = 0;
					t.string = "";
					t.kind = none;
				}
			}else {
				//int
				t.kind = intConstant_;
				t.string = temp;
				try {
					t.val = Integer.parseInt(temp);
				}catch(NumberFormatException e) {
					t.val = 0;
					t.string = "";
					t.kind = none;
				}
			}
		}
		return t;
	}
	
	private static Token readString(Token t) {
		t.string = "";
		while(lookaheadch != '"' && lookaheadch != eol) {
			t.string += lookaheadch;
			nextCh();
		}
		//ako naidje na newline, stavlja se da je token tipa none?
		if(lookaheadch == eol) {
			t.string = "";
			t.s_val = "";
			t.kind = none;
		}else {
			t.kind = stringConstant_;
			t.s_val = new String(t.string);
		}
		nextCh();
		return t;
	}

}







