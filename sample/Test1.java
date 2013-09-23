package sample;

import java.io.File;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.LoadConstant;

public class Test1 {
	
	public static final String TEST_STR = "test-str";
	
	private static String mTestStr = "";
	
	public void g() {
//		String out = String.format("prefix_%s", TEST_STR);
		System.out.println(TEST_STR);
	}
	
	public void g1() {
		System.out.println(mTestStr);
	}
	
	
	private static String transfor(String code, String key) {
		return code + key;
	}
	
	
	public static void main(String[] args) throws Exception {
		ClassPool pool = ClassPool.getDefault();

		CtClass cc = pool.get("sample.Test1");
		 
		CtMethod gMethod = cc.getDeclaredMethod("g");
		gMethod.instrument(new ExprEditor() {
			public void edit(LoadConstant e) {
				System.out.println("load constant value = " + e.getConstantValue());
				if(e.getConstantValue().equals("test-str")) {
					try {
						e.replace("$_ = sample.Test1.transfor(\"dddddd\", \"aaa\");");
					} catch (CannotCompileException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		CtMethod g1Method = cc.getDeclaredMethod("g1");
		g1Method.instrument(new ExprEditor() {
			public void edit(FieldAccess f) {
				if(f.isReader()) {
					if(f.getFieldName().equals("mTestStr")) {
						try {
							f.replace("$_ = sample.Test1.transfor(\"dddddd\", \"aaa\");");
						} catch (CannotCompileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		cc.writeFile();
		 
	}

}
