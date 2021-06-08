import java.util.LinkedList;

public class AST {

	Program program;
	
//	public void initProgram() {
//		this.program = new Program();
//		this.program.declarations = new Declarations();
//		this.program.commandSequence = new CommandSequence();
//	}
	
	@Override
	public String toString() {
		return "AST\nProgam:\n" + program;
	}

	public static class Program{
		Declarations declarations;
		CommandSequence commandSequence;
		@Override
		public String toString() {
			return "declarations:\n" + declarations + "\ncommandSequence:" + commandSequence;
		}
		
	}
	
//	public void initDeclarations() {
//			this.program.declarations.declarationsList = new LinkedList<Declaration>();
//	}
	
	public static class Declarations{
		LinkedList<Declaration> declarationsList;

		@Override
		public String toString() {
			String temp = "";
			for(int i = 0; i < declarationsList.size(); i++) {
				temp = temp + " " + declarationsList.get(i).toString() + "\n";
			}
			return "declarationsList:\n" + temp;
		}
		
	}
	
//	public void addDeclaration(String t, String i) {
//		this.program.declarations.declarationsList.add(new Declaration(t,i));	
//	}
	
	public static class Declaration{
		Type type;
		Identifier id;
		@Override
		public String toString() {
			return "Declaration: " + type + " " + id;
		}
		
	}
	
//	public void initCommandSequence() {
//		if(this.program.commandSequence.commandList != null)  return;
//		else this.program.commandSequence.commandList = new LinkedList<Command>();
//	}
	
	public static class CommandSequence{
		LinkedList<Command> commandList;

		@Override
		public String toString() {
			String temp = "";
			for(int i = 0; i < commandList.size(); i++) {
				temp = temp + commandList.get(i) + "\n";
			}
			return "\ncommandList:\n" + temp;
		}
		
	}
	
	public static class Command{
		Command command;

		@Override
		public String toString() {
			return "Command [command=" + "Obicna komanda" + "]";
		}
		
	}
	
//	public void addAssignCommand(String i, Expression e) {
//		this.program.commandSequence.commandList.add(new AssignCommand(i, e));
//	}
	
	public static class AssignCommand extends Command{
		//provjeriti da li je deklarisana promjenljiva
		Identifier id;
		Expression expression;
		@Override
		public String toString() {
			return "AssignCommand " + id + " = " + expression;
		}
		
	}
	
	public static class IfCommand extends Command{
		Expression expression;
		CommandSequence ifCommandSequence;
		CommandSequence elseCommandSequence;
		@Override
		public String toString() {
			return "IfCommand " + expression + ifCommandSequence
					+ "Else" + elseCommandSequence + "EndIF";
		}
		
	}
	
	public static class WhileCommand extends Command{
		Expression expression;
		CommandSequence whileCommandSequence;
		@Override
		public String toString() {
			return "WhileCommand " + expression + whileCommandSequence;
		}
		
	}
	
	public static class ForCommand extends Command{
		AssignCommand a1;
		Expression expr;
		AssignCommand a2;
		CommandSequence forCommandSequence;
		@Override
		public String toString() {
			return "ForCommand [" + a1 + " " + expr + " " + a2 + "]" + forCommandSequence;
		}
		
	}
	
	public static class RepeatCommand extends Command{
		CommandSequence repeatCommandSequence;
		Expression expression;
		@Override
		public String toString() {
			return "RepeatCommand " + repeatCommandSequence + " " + expression;
		}
		
	}
	
	public static class PrintCommand extends Command{
		Expression printExpression;

		@Override
		public String toString() {
			return "PrintCommand " + printExpression;
		}
		
	}
	
	public static class BreakCommand extends Command{
		boolean belongsToALoop;

		@Override
		public String toString() {
			return "BreakCommand [belongsToALoop=" + belongsToALoop + "]";
		}
	}
	
	public static class ReadCommand extends Command{
		Type readType;
		public ReadCommand(Type t) {
			this.readType = t;
		}
		@Override
		public String toString() {
			return "ReadCommand " + readType.toString();
		}
		
	}
	
	public static class Expression{
		Expression expression;

		@Override
		public String toString() {
			return "Expression [expression=" + "basic expression" + "]";
		}
		
	}

	public static class BinaryOperatorExpression extends Expression{
		Expression firstExpression;
		String binaryOperator;
		Expression secondExpression;
		@Override
		public String toString() {
			return "(" + firstExpression + binaryOperator + secondExpression + ")";
		}
		
	}
	
	public static class UnaryOperatorExpression extends Expression{
		String unaryOperator;
		Expression expression;
		@Override
		public String toString() {
			return unaryOperator + expression;
		}
		
		
	}
	
	public static class ReadExpression extends Expression{
		Type readType;
		public ReadExpression(Type t) {
			this.readType = t;
		}
		@Override
		public String toString() {
			return "ReadExpression " + readType.toString();
		}
	}
	
	public static class Constant extends Expression{
		Type constType;
		String value;
		public Constant(Type t, String v) {
			this.constType = t;
			this.value = new String(v);
		}
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static class Type {
		String type;
		public Type(String t) {
			this.type = new String(t);
		}
		@Override
		public String toString() {
			return "[" + type + "]";
		}
		
	}
	
	public static class Identifier extends Expression{
		String name;
		public Identifier(String n) {
			this.name = n;
		}
		@Override
		public String toString() {
			return "[" + name + "]";
		}
		
		
	}
}
