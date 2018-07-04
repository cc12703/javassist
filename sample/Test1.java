package sample;

import java.io.File;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.BadBytecode;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.LoadConstant;

public class Test1 {
	
	public static final String TEST_STR = "test-str";
	public static final String TEST_STR1 = "test-str-1";
	public static final String TEST_STR2 = "test-str-2";
	public static final String TEST_STR3 = "test-str-3";
	public static final String TEST_STR4 = "test-str-4";
	public static final String TEST_STR5 = "test-str-5";
	public static final String TEST_STR6 = "test-str-6";
	public static final String TEST_STR7 = "test-str-7";
	public static final String TEST_STR8 = "test-str-8";
	public static final String TEST_STR9 = "test-str-9";

    public static final String[] TEST_ARRAY = new String[] {
            "1ee", "2", "3", "4",
            "1ee3", "32", "33", "34",
            "1eei4", "42", "34", "3",
            "1eel", "2l2", "i3", "444",
            "1eedddd", "2444", "344", "455",
            "1eeddd", "2ddd", "3sss", "ssss4",
            "1eeddddd", "dddd2ddd", "ccccl3sss", "vvvvssss4",
            "1eedddccc", "2dddcc", "3sdddss", "ssssddd4",
            "1eedddddddd", "arraaa2ddd", "33333sss", "3r333sss4",
            "1eedssdddddd", "arrraaa2ddd", "33rr333sss", "3r333sss4",
            "1edssdddddd", "aaaa2ddd", "333rrr33sss", "3333sss4",
            "1esdddddd", "aaaa2ddd", "33333sss", "3333sss4",
            "1edddrrd", "a2rrrddd", "333sss", "3rrrjsss4",
            "1eeddrrkd", "aaddd", "rrrk3333s", "33ss4",
            "1eeeeeddrrkd", "eeeaaddd", "aaaarrrk3333s", "vvv33ss4",
            "1eeeeeddrrkd", "rrrraaddd", "wwwwrrrk3333s", "vvvvvv33ss4",
    };

	private static String mTestStr = "";
	private static Class mTestCls = null;
	
	public void g() {
//		String out = String.format("prefix_%s", TEST_STR);
		System.out.println(TEST_STR);
	}
	
	public void g1() {
		System.out.println(mTestStr);
	}


	public void g2() {
		mTestCls = Test1.class;
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


		CtMethod g2Method = cc.getDeclaredMethod("g2");
		g2Method.instrument(new ExprEditor() {
			public void edit(LoadConstant e) {
				try {
					if (e.getConstantClassName().equals("sample.Test1")) {
						e.replaceClassName("sample.Test");
					}
				}catch (BadBytecode exp) {
					exp.printStackTrace();
				}
			}
		});

		cc.writeFile();
		 
	}

}
