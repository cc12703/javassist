package javassist.expr;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;


public class LoadConstant extends Expr {
	
	int opcode;

	protected LoadConstant(int pos, CodeIterator i, CtClass declaring,
			MethodInfo m, int op) {
		super(pos, i, declaring, m);
		opcode = op;
	}
	
	
	public Object getConstantValue() {
		ConstPool constPool = getConstPool();
		int index = -1;
		if(opcode == Opcode.LDC)
	        index = iterator.byteAt(currentPos + 1);
		else if(opcode == Opcode.LDC_W)
			index = iterator.u16bitAt(currentPos + 1);
		else
			return null;
		
		return constPool.getLdcValue(index);
	}
	
	
	private int getBytecodeSize() {
		int size = 0;
		if(opcode == Opcode.LDC)
			size = 2;
		else if(opcode == Opcode.LDC_W)
			size = 3;
		
		return size;
	}
	
	
	private CtClass getConstantValueType() throws NotFoundException {
		Object val = getConstantValue();
		if(val == null)
			return null;
		
		if(val instanceof java.lang.Float)
			return CtClass.floatType;
		else if(val instanceof java.lang.Integer)
			return CtClass.intType;
		else if(val instanceof java.lang.Long)
			return CtClass.longType;
		else if(val instanceof java.lang.Double)
			return CtClass.doubleType;
		else if(val instanceof java.lang.String)
			return Descriptor.toCtClass("Ljava/lang/String;", thisClass.getClassPool());
		else
			return null;
		
	}

	@Override
	public void replace(String statement) throws CannotCompileException {
		thisClass.getClassFile();   // to call checkModify().
        int pos = currentPos;
        
        Javac jc = new Javac(thisClass);
        CodeAttribute ca = iterator.get();
        try {
            CtClass[] params;
            CtClass retType;
           
            params = new CtClass[0];
            retType = getConstantValueType();
            

            int paramVar = ca.getMaxLocals();
            jc.recordParams(null, params,
                            false, paramVar, withinStatic());
            
            checkResultValue(retType, statement);

            int retVar = jc.recordReturnType(retType, true);
            

            Bytecode bytecode = jc.getBytecode();
            storeStack(params, true, paramVar, bytecode);
            jc.recordLocalVariables(ca, pos);

      
            bytecode.addConstZero(retType);
            bytecode.addStore(retVar, retType);     // initialize $_
               

            jc.compileStmnt(statement);
            bytecode.addLoad(retVar, retType);

            replace0(pos, bytecode, getBytecodeSize());
        }
        catch (CompileError e) { throw new CannotCompileException(e); }
        catch (NotFoundException e) { throw new CannotCompileException(e); }
        catch (BadBytecode e) {
            throw new CannotCompileException("broken method");
        }
		
	}

}
