
import java.io.*;
public class Main {

	public static void main(String[] args) {
//		InputStream s;
//		Reader r;
//		try {
//			s = new FileInputStream("src/test2.txt");
//			r = new InputStreamReader(s);
//			Scanner.init(r);
//			while(true) {
//				Token t = Scanner.next();
//				System.out.println("kind: " + t.kind + " value: " + t.val + " string: " + t.string);
//				if(t.kind == 1) break;
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		String source = "src/test2.txt";
		try {
			Scanner.init(new InputStreamReader(new FileInputStream(source)));
			Parser.parse();
			System.out.println(Parser.errors + " errors detected");
		} catch (IOException e) {
			System.out.println("-- cannot open input file " + source);
		}
		
	}

}
