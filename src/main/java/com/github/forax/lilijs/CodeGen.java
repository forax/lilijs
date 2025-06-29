package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstAutoAccessor;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassMethod;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassProp;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstConstructor;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateMethod;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateProp;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstStaticBlock;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAssignExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstAwaitExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstClassExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCondExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstFnExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMetaPropExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstNewExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstOptChainExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstParenExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSeqExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSpreadElement;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSuperPropExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstThisExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUpdateExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstYieldExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBigInt;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNull;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstRegex;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstAssignTarget;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstObjectPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstRestPat;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBreakStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstContinueStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstDebuggerStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstDoWhileStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstEmptyStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForInStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForOfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstForStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstIfStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstLabeledStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstSwitchStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstThrowStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTryStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstUsingDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstWhileStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstWithStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsIndexSignature;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsKeywordType;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsParamProp;
import com.github.forax.lilijs.VarContext.VarData;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.invoke.MethodHandles.dropArguments;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodHandles.identity;
import static java.lang.invoke.MethodType.genericMethodType;
import static java.lang.invoke.MethodType.methodType;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.partitioningBy;
import static org.objectweb.asm.Opcodes.*;

final class CodeGen {

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  private static void ldcUndefined(MethodVisitor mv) {
    mv.visitLdcInsn(new ConstantDynamic("undefined", "Ljava/lang/Object;", BSM_UNDEFINED));
  }

  static void analyzeFunction(boolean lazy, List<String> parameters, ISwc4jAst body, DataMap dataMap) {
    var ctx = new VarContext();
    ctx.varDef("this");
    for (String parameter : parameters) {
      ctx.varDef(parameter);
    }
    var varAnalyzer = new VarAnalyzer(lazy, dataMap);
    varAnalyzer.visitVar(body, ctx);
  }

  static byte[] compile(String className, ISwc4jAst program) {
    var dataMap = new DataMap();
    analyzeFunction(false, List.of("this"), program, dataMap);

    var cv = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    cv.visit(V21, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
    cv.visitSource(className, null);

    var dict = new Dict();
    genFunction(cv, "main", List.of("this"), program, 0, dataMap, dict);

    for(var info : dict.infos()) {
      var toplevel = info.toplevel();
      var bodyOrClass = info.bodyOrClass();
      if (!toplevel || bodyOrClass instanceof Swc4jAstClass) {
        throw new UnsupportedOperationException("only top level functions are supported for now");
      }
      var name = info.name();
      var parameters = info.parameters();
      genFunction(cv, name, parameters, bodyOrClass, 0, dataMap, new Dict() /* FIXME */);
    }
    cv.visitEnd();
    var instrs = cv.toByteArray();
    dumpBytecode(instrs);
    return instrs;
  }

  private static void genFunction(ClassWriter cv, String name, List<String> parameters, ISwc4jAst body, int captureCount, DataMap dataMap, Dict dict) {
    var methodType = genericMethodType(1 + captureCount + parameters.size());
    var desc = methodType.toMethodDescriptorString();
    var mv = cv.visitMethod(ACC_PUBLIC | ACC_STATIC, name, desc, null, null);
    mv.visitCode();
    new CodeGen(mv, dict, dataMap).visitCode(body);
    ldcUndefined(mv);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  static MethodHandle createFunctionMH(String name, List<String> parameters, ISwc4jAst body, int captureCount, DataMap dataMap, JSObject global) {
    // do we need to do a data analysis ?
    if (dataMap == null) {
      dataMap = new DataMap();
      analyzeFunction(true, parameters, body, dataMap);
    }

    var cv = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    cv.visit(V21, ACC_PUBLIC | ACC_SUPER, "script", null, "java/lang/Object", null);
    cv.visitSource("script", null);
    var dict = new Dict();
    genFunction(cv, name, parameters, body, captureCount, dataMap, dict);

    cv.visitEnd();
    var instrs = cv.toByteArray();
    dumpBytecode(instrs);

    var classLoader = new IsolateClassLoader(dict, global);
    var type = classLoader.createClass("script", instrs);

    var methodType = genericMethodType(1 + captureCount + parameters.size());
    MethodHandle mh;
    try {
      mh = LOOKUP.findStatic(type, name, methodType);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
    return mh;
  }

  static MethodHandle createClassFunctionMH(String className, Swc4jAstClass astClass, int captureCount, DataMap dataMap, JSObject global) {
    var cv = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    cv.visit(V21, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
    cv.visitSource("script", null);

    var init = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    init.visitCode();
    init.visitVarInsn(ALOAD, 0);
    init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

    JSFunction constructor = null;
    var methods = new ArrayList<JSFunction>();
    for(var member : astClass.getBody()) {
      switch (member) {
        case Swc4jAstClassProp prop -> {
          var key = name(prop.getKey());
          //var type = prop.getTypeAnn().map(ann -> type(ann.getTypeAnn())).orElse(Type.ANY);
          var fv = cv.visitField(ACC_PUBLIC, key, "Ljava/lang/Object;", null, null);
          fv.visitEnd();
          init.visitVarInsn(ALOAD, 0);
          ldcUndefined(init);
          init.visitFieldInsn(PUTFIELD, className, key, "Ljava/lang/Object;");
        }
        case Swc4jAstClassMethod method -> {
          checkFunctionSupported(method.getFunction());
          var name = name(method.getKey());
          var parameters = parameters(method.getFunction().getParams());
          var body = method.getFunction().getBody().orElseThrow();
          // lazy creation
          var function = new JSFunction(name, () -> createFunctionMH(name, parameters, body, captureCount, dataMap, global));
          methods.add(function);
        }
        case Swc4jAstConstructor astConstructor -> {
          var parameters = parameterOrProps(astConstructor.getParams());
          var body = astConstructor.getBody().orElseThrow();  // FIXME
          // eager creation
          var mh = createFunctionMH("constructor", parameters, body, captureCount, dataMap, global);
          constructor = new JSFunction("constructor", mh);
        }
        case Swc4jAstAutoAccessor _, Swc4jAstEmptyStmt _,
             Swc4jAstPrivateMethod _, Swc4jAstPrivateProp _, Swc4jAstStaticBlock _, Swc4jAstTsIndexSignature _ -> {
          throw new UnsupportedOperationException("unsupported member " + member);
        }
        default -> throw new AssertionError("unknown member " + member);
      }
    }

    init.visitInsn(RETURN);
    init.visitMaxs(-1, -1);
    init.visitEnd();

    var instrs = cv.toByteArray();
    dumpClass(instrs);

    var classLoader = new IsolateClassLoader(new Dict(), global);
    var type = classLoader.createClass(className, instrs);

    // add methods to the class prototype
    var prototype = RT.prototype(type);
    for(var method : methods) {
      prototype.register(method.name(), method);
    }

    MethodHandle mh;
    try {
      mh = LOOKUP.findConstructor(type, methodType(void.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }

    if (constructor != null) {  // call the constructor after creation
      // FIXME, should set the constructor as a field of the JSFunction
      var cons = constructor.methodHandle();
      cons = cons.asType(cons.type().changeReturnType(void.class).changeParameterType(0, type));
      var identity = dropArguments(identity(type), 1, nCopies(cons.type().parameterCount() - 1, Object.class));
      var newFunction = foldArguments(identity, cons);
      mh = foldArguments(newFunction, mh);
    }

    mh = dropArguments(mh, 0, Object.class);

    return mh;
  }

  private static String name(ISwc4jAstPat pat) {
    return switch (pat) {
      case Swc4jAstBindingIdent bindingIdent -> name(bindingIdent.getId());
      case Swc4jAstIdent ident -> ident.getSym();
      case Swc4jAstObjectPat _, Swc4jAstRestPat _ -> throw new UnsupportedOperationException("unsupported name " + pat);
      default -> throw new AssertionError(pat.getClass().getName());
    };
  }
  private static String name(Swc4jAstParam param) {
    return name(param.getPat());
  }
  private static String name(ISwc4jAstMemberProp prop) {
    return switch (prop) {
      case Swc4jAstIdentName identName -> identName.getSym();
      case Swc4jAstComputedPropName _, Swc4jAstPrivateName _ -> throw new UnsupportedOperationException("unsupported name " + prop);
      default -> throw new AssertionError(prop);
    };
  }
  private static String name(ISwc4jAstPropName propName) {
    return switch (propName) {
      case Swc4jAstIdentName identName -> identName.getSym();
      default -> throw new AssertionError(propName);
    };
  }
  private static String name(ISwc4jAstAssignTarget assignTarget) {
    return switch (assignTarget) {
      case Swc4jAstBindingIdent ident -> ident.getId().getSym();
      default -> throw new AssertionError(assignTarget);
    };
  }

  private enum Type { ANY, BOOLEAN, NUMBER, STRING }

  private static Type type(ISwc4jAstTsType astType) {
    return switch (astType) {
      case Swc4jAstTsKeywordType keywordType -> switch (keywordType.getType()) {
        case Bool -> Type.BOOLEAN;
        case Number -> Type.NUMBER;
        case Str -> Type.STRING;
        default -> Type.ANY;
      };
      default -> throw new UnsupportedOperationException("type " + astType);
    };
  }

  private static List<String> parameterOrProps(List<ISwc4jAstParamOrTsParamProp> params) {
    return params.stream()
        .map(p -> switch (p) {
          case Swc4jAstParam param -> name(param);
          case Swc4jAstTsParamProp _ -> throw new UnsupportedOperationException("TODO");
          default -> throw new AssertionError();
        })
        .toList();
  }
  private static List<String> parameters(List<Swc4jAstParam> params) {
    return params.stream()
        .map(CodeGen::name)
        .toList();
  }
  private static List<String> parameterOrPatterns(List<ISwc4jAstPat> params) {
    return params.stream()
        .map(CodeGen::name)
        .toList();
  }

  private static void checkFunctionSupported(Swc4jAstFunction function) {
    if (function.isGenerator()) {
      throw new UnsupportedOperationException("generator function not supported");
    }
    if (function.isAsync()) {
      throw new UnsupportedOperationException("async function not supported");
    }
  }
  private static void checkFunctionSupported(Swc4jAstArrowExpr arrowExpr) {
    if (arrowExpr.isGenerator()) {
      throw new UnsupportedOperationException("generator arrow function not supported");
    }
    if (arrowExpr.isAsync()) {
      throw new UnsupportedOperationException("async arrow function not supported");
    }
  }

  static final class DataMap {
    private final HashMap<ISwc4jAst, Object> dataMap = new HashMap<>();

    public void registerVarData(ISwc4jAst ast, VarData varData) {
      dataMap.put(ast, varData);
    }

    public VarData getVarData(ISwc4jAst ast) {
      return (VarData) dataMap.get(ast);
    }

    public void registerCaptureInfo(ISwc4jAst ast, VarContext.CaptureInfo captureInfo) {
      dataMap.put(ast, captureInfo);
    }

    public VarContext.CaptureInfo getCaptureInfo(ISwc4jAst ast) {
      return (VarContext.CaptureInfo) dataMap.get(ast);
    }
  }

  private static final class VarAnalyzer {
    private final boolean lazy;
    private final DataMap dataMap;

    public VarAnalyzer(boolean lazy, DataMap dataMap) {
      this.lazy = lazy;
      this.dataMap = dataMap;
    }

    private void visitVar(ISwc4jAst ast, VarContext ctx) {
      switch (ast) {
        case Swc4jAstBool _, Swc4jAstNull _, Swc4jAstNumber _, Swc4jAstStr _, Swc4jAstEmptyStmt _ -> {
          // literal, do nothing
        }
        case ISwc4jAstProgram<?> program -> {
          for (var node : program.getBody()) {
            switch (node) {
              case Swc4jAstFnDecl fnDecl -> {
                // do not visit top level functions declaration if there are initialized lazily
                if (!lazy) {
                  var parameters = parameters(fnDecl.getFunction().getParams());
                  var body = fnDecl.getFunction().getBody().orElseThrow();
                  analyzeFunction(false, parameters, body, dataMap);
                }
              }
              case Swc4jAstClassDecl classDecl -> {
                // do not visit top level class declarations if there are initialized lazily
                if (!lazy) {
                  visitVar(classDecl, ctx);
                }
              }
              default -> visitVar(node, ctx);
            }
          }
        }
        case Swc4jAstBlockStmt blockStmt -> {
          var newCtx = ctx.newLocalContext();
          for (var stmt : blockStmt.getStmts()) {
            visitVar(stmt, newCtx);
          }
        }
        case Swc4jAstExprStmt exprStmt -> {
          visitVar(exprStmt.getExpr(), ctx);
        }
        case Swc4jAstExprOrSpread exprOrSpread -> {
          if (exprOrSpread.getSpread().isPresent()) {
            throw new UnsupportedOperationException("spread are not supported");
          }
          visitVar(exprOrSpread.getExpr(), ctx);
        }
        case Swc4jAstReturnStmt returnStmt -> {
          var argOpt = returnStmt.getArg();
          if (argOpt.isPresent()) {
            visitVar(argOpt.orElseThrow(), ctx);
          }
        }
        case Swc4jAstFnDecl fnDecl -> {
          // only non-toplevel function are visited here
          checkFunctionSupported(fnDecl.getFunction());
          var name = fnDecl.getIdent().getSym();
          var index = ctx.varDef(name); // will be stored in a local variable
          if (index == -1) {
            throw new Failure("variable " + name + " already defined");
          }
          dataMap.registerVarData(fnDecl, new VarData.Local(index, ctx.captureInfo));

          var parameters = parameters(fnDecl.getFunction().getParams());
          var body = fnDecl.getFunction().getBody().orElseThrow();
          var newCtx = ctx.newCaptureContext();
          newCtx.localCount++;   // "this" should be captured, function still has a first parameter
          parameters.forEach(newCtx::varDef);
          visitVar(body, newCtx);
          dataMap.registerCaptureInfo(fnDecl.getFunction(), newCtx.captureInfo);
        }
        case Swc4jAstArrowExpr arrowExpr -> {
          checkFunctionSupported(arrowExpr);
          var parameters = parameterOrPatterns(arrowExpr.getParams());
          var body = arrowExpr.getBody();
          var newCtx = ctx.newCaptureContext();
          newCtx.varDef("this");
          parameters.forEach(newCtx::varDef);
          visitVar(body, newCtx);
          dataMap.registerCaptureInfo(arrowExpr, newCtx.captureInfo);
        }
        case Swc4jAstFnExpr fnExpr -> {
          checkFunctionSupported(fnExpr.getFunction());
          var parameters = parameters(fnExpr.getFunction().getParams());
          var bodyOpt = fnExpr.getFunction().getBody();
          var newCtx = ctx.newCaptureContext();
          newCtx.varDef("this");
          parameters.forEach(newCtx::varDef);
          if (bodyOpt.isPresent()) {
            visitVar(bodyOpt.orElseThrow(), newCtx);
          }
          dataMap.registerCaptureInfo(fnExpr, newCtx.captureInfo);
        }
        case Swc4jAstFunction _ -> {
          throw new AssertionError();
        }
        case Swc4jAstClassDecl classDecl -> {
          // only non-toplevel class are visited here
          var name = classDecl.getIdent().getSym();
          var index = ctx.varDef(name); // will be stored in a local variable
          if (index == -1) {
            throw new Failure("variable " + name + " already defined");
          }
          dataMap.registerVarData(classDecl, new VarData.Local(index, ctx.captureInfo));

          visitVar(classDecl.getClazz(), ctx);
        }
        case Swc4jAstClassExpr classExpr -> {
          visitVar(classExpr.getClazz(), ctx);
        }
        case Swc4jAstClass astClass -> {
          var classCtx = ctx.newCaptureContext();
          for(var member : astClass.getBody()) {
            switch (member) {
              case Swc4jAstClassProp prop -> {
                var value = prop.getValue();
                if (value.isPresent()) {
                  throw new UnsupportedOperationException("field initialization not supported yet");
                }
              }
              case Swc4jAstClassMethod method -> {
                checkFunctionSupported(method.getFunction());
                var parameters = parameters(method.getFunction().getParams());
                var bodyOpt = method.getFunction().getBody();
                var newCtx = classCtx.newCaptureContext();
                newCtx.varDef("this");
                parameters.forEach(newCtx::varDef);
                if (bodyOpt.isPresent()) {
                  visitVar(bodyOpt.orElseThrow(), newCtx);
                }
                dataMap.registerCaptureInfo(method, newCtx.captureInfo);
              }
              case Swc4jAstConstructor constructor -> {
                var parameters = parameterOrProps(constructor.getParams());
                var bodyOpt = constructor.getBody();
                var newCtx = classCtx.newCaptureContext();
                newCtx.varDef("this");
                parameters.forEach(newCtx::varDef);
                if (bodyOpt.isPresent()) {
                  visitVar(bodyOpt.orElseThrow(), newCtx);
                }
                dataMap.registerCaptureInfo(constructor, newCtx.captureInfo);
              }
              default -> throw new UnsupportedOperationException("TODO " + member);
            }
          }
          dataMap.registerCaptureInfo(astClass, classCtx.captureInfo);
        }

        case Swc4jAstVarDecl varDecl -> {
          switch (varDecl.getKind()) {
            case Var -> throw new UnsupportedOperationException("declaration using var is not supported");
            case Const, Let -> {}
          }
          for (var varDeclarator : varDecl.getDecls()) {
            visitVar(varDeclarator, ctx);
          }
        }
        case Swc4jAstNewExpr newExpr -> {
          visitVar(newExpr.getCallee(), ctx);
          for(var arg : newExpr.getArgs().orElse(List.of())) {
            visitVar(arg, ctx);
          }
        }
        case Swc4jAstVarDeclarator varDeclarator -> {
          var initOpt = varDeclarator.getInit();
          if (initOpt.isPresent()) {
            visitVar(initOpt.orElseThrow(), ctx);
          }
          var name = name(varDeclarator.getName());
          var index = ctx.varDef(name);
          if (index == -1) {
            throw new Failure("variable " + name + " already defined");
          }
          dataMap.registerVarData(varDeclarator, new VarData.Local(index, ctx.captureInfo));
        }
        case Swc4jAstAssignExpr assignExpr -> {
          visitVar(assignExpr.getRight(), ctx);
          var left = assignExpr.getLeft();
          switch (left) {
            case Swc4jAstBindingIdent ident -> {  // local write
              var name = name(ident);
              var varData = ctx.varUse(name);
              switch (varData) {
                case VarData.Local _ -> dataMap.registerVarData(assignExpr, varData);
                case VarData.Capture _ -> throw new UnsupportedOperationException("can not assign a captured value " + name);
                case VarData.Global _ -> throw new Failure("unknown local variable " + name);
              }
            }
            case Swc4jAstMemberExpr memberExpr -> {  // field write
              var _ = name(memberExpr.getProp());
              visitVar(memberExpr.getObj(), ctx);
            }
            default -> throw new UnsupportedOperationException("unsupported " + left);
          }
        }
        case Swc4jAstUpdateExpr updateExpr -> {
          var name = name(updateExpr.getArg());
          var varData = ctx.varUse(name);
          switch (varData) {
            case VarData.Local _ -> dataMap.registerVarData(updateExpr, varData);
            case VarData.Capture _ -> throw new UnsupportedOperationException("can not assign a captured value " + name);
            case VarData.Global _ -> throw new Failure("unknown local variable " + name);
          }
        }
        case Swc4jAstBinExpr binaryExpr -> {
          visitVar(binaryExpr.getLeft(), ctx);
          visitVar(binaryExpr.getRight(), ctx);
        }
        case Swc4jAstUnaryExpr unaryExpr -> {
          visitVar(unaryExpr.getArg(), ctx);
        }
        case Swc4jAstCallExpr callExpr -> {
          visitVar(callExpr.getCallee(), ctx);
          for (var arg : callExpr.getArgs()) {
            visitVar(arg, ctx);
          }
        }
        case Swc4jAstIdent ident -> {
          var name = ident.getSym();
          var varData = ctx.varUse(name);
          dataMap.registerVarData(ident, varData);
        }
        case Swc4jAstIdentName identName -> {
          var name = identName.getSym();
          var varData = ctx.varUse(name);
          dataMap.registerVarData(identName, varData);
        }
        case Swc4jAstMemberExpr memberExpr -> {
          visitVar(memberExpr.getObj(), ctx);
          visitVar(memberExpr.getProp(), ctx);
        }
        case Swc4jAstParenExpr parenExpr -> {
          visitVar(parenExpr.getExpr(), ctx);
        }
        case Swc4jAstSeqExpr seqExpr -> {
          for(var expr : seqExpr.getExprs()) {
            visitVar(expr, ctx);
          }
        }
        case Swc4jAstThisExpr thisExpr -> {
          var varData = ctx.varUse("this");  // not needed
          dataMap.registerVarData(thisExpr, varData);
        }
        case Swc4jAstIfStmt ifStmt -> {
          visitVar(ifStmt.getTest(), ctx);
          visitVar(ifStmt.getCons(), ctx);
          var altOpt = ifStmt.getAlt();
          if (altOpt.isPresent()) {
            visitVar(altOpt.orElseThrow(), ctx);
          }
        }
        case Swc4jAstCondExpr condExpr -> {
          visitVar(condExpr.getTest(), ctx);
          visitVar(condExpr.getCons(), ctx);
          visitVar(condExpr.getAlt(), ctx);
        }
        case Swc4jAstWhileStmt whileStmt -> {
          visitVar(whileStmt.getTest(), ctx);
          var newCtx = ctx.newLocalContext();
          visitVar(whileStmt.getBody(), newCtx);
        }
        case Swc4jAstDoWhileStmt doWhileStmt -> {
          var bodyCtx = ctx.newLocalContext();
          visitVar(doWhileStmt.getBody(), bodyCtx);
          visitVar(doWhileStmt.getTest(), ctx);
        }
        case Swc4jAstForStmt forStmt -> {
          var forCtx = ctx.newLocalContext();
          var optInit = forStmt.getInit();
          if (optInit.isPresent()) {
            visitVar(optInit.orElseThrow(), forCtx);
          }
          var testOpt = forStmt.getTest();
          if (testOpt.isPresent()) {
            visitVar(testOpt.orElseThrow(), forCtx);
          }
          var bodyCtx = forCtx.newLocalContext();
          visitVar(forStmt.getBody(), bodyCtx);
          var updateOpt = forStmt.getUpdate();
          if (updateOpt.isPresent()) {
            visitVar(updateOpt.orElseThrow(), forCtx);
          }
        }
        case Swc4jAstBreakStmt _, Swc4jAstContinueStmt _, Swc4jAstLabeledStmt _,
             Swc4jAstDebuggerStmt _,
             Swc4jAstForInStmt _, Swc4jAstForOfStmt _,
             Swc4jAstSwitchStmt _, Swc4jAstThrowStmt _,
             Swc4jAstTryStmt _, Swc4jAstUsingDecl _, Swc4jAstWithStmt _,
             Swc4jAstAwaitExpr _,
             Swc4jAstMetaPropExpr _, Swc4jAstOptChainExpr _,
             Swc4jAstSpreadElement _, Swc4jAstSuperPropExpr _, Swc4jAstYieldExpr _,
             Swc4jAstArrayLit _, Swc4jAstBigInt _, Swc4jAstObjectLit _, Swc4jAstRegex _ -> {
          throw new UnsupportedOperationException("TODO " + ast.getClass().getName());
        }
        default -> throw new AssertionError("ERROR " + ast.getClass().getName());
      }
    }
  }

  private static void dumpBytecode(byte[] array) {
    var reader = new ClassReader(array);
    CheckClassAdapter.verify(reader, true, new PrintWriter(System.err, false, UTF_8));
  }

  private static void dumpClass(byte[] array) {
    var reader = new ClassReader(array);
    reader.accept(new TraceClassVisitor(new PrintWriter(System.err)), 0);
  }

  private static Handle bsm(String name, Class<?> returnType, Class<?>... parameterTypes) {
    return new Handle(H_INVOKESTATIC,
        RT_NAME, name,
        MethodType.methodType(returnType, parameterTypes).toMethodDescriptorString(), false);
  }

  private static final String RT_NAME = RT.class.getName().replace('.', '/');
  private static final Handle BSM_UNDEFINED = bsm("bsm_undefined", Object.class, MethodHandles.Lookup.class, String.class, Class.class);
  private static final Handle BSM_BOOL = bsm("bsm_bool", Object.class, MethodHandles.Lookup.class, String.class, Class.class, Object.class);
  private static final Handle BSM_CONST = bsm("bsm_const", Object.class, MethodHandles.Lookup.class, String.class, Class.class, Object.class);
  private static final Handle BSM_FNDECL = bsm("bsm_fndecl", Object.class, MethodHandles.Lookup.class, String.class, TypeDescriptor.class, int.class);
  private static final Handle BSM_LOOKUP = bsm("bsm_lookup", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
  private static final Handle BSM_CALL = bsm("bsm_call", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
  private static final Handle BSM_BUILTIN = bsm("bsm_builtin", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
  //private static final Handle BSM_REGISTER = bsm("bsm_register", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
  private static final Handle BSM_TRUTH = bsm("bsm_truth", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
  private static final Handle BSM_GET = bsm("bsm_get", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
  private static final Handle BSM_SET = bsm("bsm_set", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
  private static final Handle BSM_METHODCALL = bsm("bsm_methodcall", CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);

  private final MethodVisitor mv;
  private final Dict dict;
  private final DataMap dataMap;

  private int varIndex(ISwc4jAst ast) {
    var varData = dataMap.getVarData(ast);
    assert varData != null : "invalid varData for " + ast;
    return switch (varData) {
      case VarData.Global _ -> -1;
      case VarData.Capture(int index) -> index;
      case VarData.Local(int index, VarContext.CaptureInfo captureInfo) -> captureInfo.captures().size() + index;
    };
  }

  private void emitLine(ISwc4jAst node) {
    var label = new Label();
    var line = node.getSpan().getLine();
    mv.visitLabel(label);
    mv.visitLineNumber(line, label);
  }

  private void emitFonctionCreation(String name, List<String> parameters, ISwc4jAst bodyOrClass, boolean toplevel, List<Integer> captures) {
    var functionInfo = new Dict.Info(toplevel, name, parameters, bodyOrClass, toplevel ? null : dataMap);
    var infoId = dict.encodeInfo(functionInfo);
    if (captures.isEmpty()) {
      mv.visitLdcInsn(new ConstantDynamic(name, "Ljava/lang/Object;", BSM_FNDECL, infoId));
    } else {
      for(var varIndex : captures) {
        mv.visitVarInsn(ALOAD, varIndex);
      }
      var desc = "(" + "Ljava/lang/Object;".repeat(captures.size()) + ")Ljava/lang/Object;";
      mv.visitInvokeDynamicInsn(name, desc, BSM_FNDECL, infoId);
    }
  }

  CodeGen(MethodVisitor mv, Dict dict, DataMap dataMap) {
    this.mv = mv;
    this.dict = dict;
    this.dataMap = dataMap;
  }

  public void visitCode(ISwc4jAst ast) {
    switch (ast) {
      case Swc4jAstBool bool -> {
        mv.visitLdcInsn(new ConstantDynamic("bool", "Ljava/lang/Object;", BSM_BOOL, bool.isValue()));
      }
      case Swc4jAstNull _ -> {
        mv.visitInsn(ACONST_NULL);
      }
      case Swc4jAstNumber number -> {
        var raw = number.getRaw().orElseThrow();
        var value = raw.contains(".") ? (Object) Double.parseDouble(raw) : (Object) Integer.parseInt(raw);
        mv.visitLdcInsn(new ConstantDynamic("number", "Ljava/lang/Object;", BSM_CONST, value));
      }
      case Swc4jAstStr str -> {
        mv.visitLdcInsn(str.asString());
      }
      case ISwc4jAstProgram<?> program -> {
        var body = program.getBody();
        var partition = body.stream()
            .collect(partitioningBy(node -> node instanceof Swc4jAstFnDecl));
        // first, visit function declarations
        for(var fnDecl : partition.get(true)) {
          emitLine(fnDecl);
          visitCode(fnDecl);
        }
        // then visit all other nodes
        for(var node : partition.get(false)) {
          emitLine(node);
          visitCode(node);
        }
      }
      case Swc4jAstBlockStmt blockStmt -> {
        for(var stmt : blockStmt.getStmts()) {
          emitLine(stmt);
          visitCode(stmt);
        }
      }
      case Swc4jAstEmptyStmt emptyStmt -> {
        // just do nothing
      }
      case Swc4jAstExprStmt exprStmt -> {
        visitCode(exprStmt.getExpr());
        mv.visitInsn(POP);
      }
      case Swc4jAstExprOrSpread exprOrSpread -> {
        if (exprOrSpread.getSpread().isPresent()) {
          throw new UnsupportedOperationException("spread are not supported");
        }
        visitCode(exprOrSpread.getExpr());
      }
      case Swc4jAstReturnStmt returnStmt -> {
        var argOpt = returnStmt.getArg();
        if (argOpt.isPresent()) {
          visitCode(argOpt.orElseThrow());
        } else {
          ldcUndefined(mv);
        }
        mv.visitInsn(ARETURN);
      }
      case Swc4jAstFnDecl fnDecl -> {
        var name = fnDecl.getIdent().getSym();
        var parameters = parameters(fnDecl.getFunction().getParams());
        var body = fnDecl.getFunction().getBody().orElseThrow();  // FIXME ?
        var captureInfo = dataMap.getCaptureInfo(fnDecl.getFunction());
        var toplevel = captureInfo == null;
        var captures = toplevel ? List.<Integer>of() : captureInfo.captures();
        emitFonctionCreation(name, parameters, body, toplevel, captures);
        if (toplevel) {
          mv.visitInsn(POP);
        } else {
          var varIndex = varIndex(fnDecl);
          mv.visitVarInsn(ASTORE, varIndex);
        }
      }
      case Swc4jAstArrowExpr arrowExpr -> {
        var captures = dataMap.getCaptureInfo(arrowExpr).captures();
        var parameters = parameterOrPatterns(arrowExpr.getParams());
        var body = switch (arrowExpr.getBody()) {
          case Swc4jAstBlockStmt stmt -> stmt;
          case ISwc4jAstExpr expr -> new Swc4jAstReturnStmt(expr, expr.getSpan());
          default -> throw new AssertionError();
        };
        emitFonctionCreation("anonymous", parameters, body, false, captures);
      }
      case Swc4jAstFnExpr fnExpr -> {
        var captures = dataMap.getCaptureInfo(fnExpr).captures();
        var name = fnExpr.getIdent().map(Swc4jAstIdent::getSym).orElse("anonymous");
        var parameters = parameters(fnExpr.getFunction().getParams());
        var body = fnExpr.getFunction().getBody().orElseThrow();  // FIXME ?
        emitFonctionCreation(name, parameters, body, false, captures);
      }
      case Swc4jAstFunction _ -> {
        throw new AssertionError();
      }
      case Swc4jAstClassDecl classDecl -> {
        var name = name(classDecl.getIdent());
        var captureInfo = dataMap.getCaptureInfo(classDecl.getClazz());
        var toplevel = captureInfo == null;
        var captures = toplevel ? List.<Integer>of() : captureInfo.captures();
        emitFonctionCreation(name, List.of(), classDecl.getClazz(), toplevel, captures);
        if (toplevel) {
          mv.visitInsn(POP);
        } else {
          var varIndex = varIndex(classDecl);
          mv.visitVarInsn(ASTORE, varIndex);
        }
      }
      case Swc4jAstClassExpr classExpr -> {
        var name = classExpr.getIdent().map(CodeGen::name).orElse("anonymous");
        var captures = dataMap.getCaptureInfo(classExpr.getClazz()).captures();
        emitFonctionCreation(name, List.of(), classExpr.getClazz(), false, captures);
      }
      case Swc4jAstNewExpr newExpr -> {
        visitCode(newExpr.getCallee());
        ldcUndefined(mv);
        var args = newExpr.getArgs().orElse(List.of());
        for(var arg : args) {
          visitCode(arg);
        }
        var desc = "(" + "Ljava/lang/Object;".repeat(2 + args.size()) + ")Ljava/lang/Object;";
        mv.visitInvokeDynamicInsn("call", desc, BSM_CALL);
      }
      case Swc4jAstVarDecl varDecl -> {
        switch (varDecl.getKind()) {
          case Var -> throw new UnsupportedOperationException("declaration using var is not supported");
          case Const, Let -> {}
        }
        for(var varDeclarator : varDecl.getDecls()) {
          visitCode(varDeclarator);
        }
      }
      case Swc4jAstVarDeclarator varDeclarator -> {
        var initOpt = varDeclarator.getInit();
        if (initOpt.isPresent()) {
          visitCode(initOpt.orElseThrow());
        } else {
          ldcUndefined(mv);
        }
        var varIndex = varIndex(varDeclarator);
        mv.visitVarInsn(ASTORE, varIndex);
      }
      case Swc4jAstAssignExpr assignExpr -> {
        var left = assignExpr.getLeft();
        switch (left) {
          case Swc4jAstBindingIdent _ -> {  // local write
            visitCode(assignExpr.getRight());
            mv.visitInsn(DUP);
            var varIndex = varIndex(assignExpr);
            mv.visitVarInsn(ASTORE, varIndex);
          }
          case Swc4jAstMemberExpr memberExpr -> {  // field write
            var name = name(memberExpr.getProp());
            visitCode(assignExpr.getRight());
            mv.visitInsn(DUP);
            visitCode(memberExpr.getObj());
            mv.visitInsn(SWAP);
            mv.visitInvokeDynamicInsn("set", "(Ljava/lang/Object;Ljava/lang/Object;)V", BSM_SET, name);
          }
          default -> throw new UnsupportedOperationException("unsupported " + left);
        }
      }
      case Swc4jAstUpdateExpr updateExpr -> {
        var varIndex = varIndex(updateExpr);
        mv.visitVarInsn(ALOAD, varIndex);
        var prefix = updateExpr.isPrefix();
        if (!prefix) {
          mv.visitInsn(DUP);
        }
        var opName = switch (updateExpr.getOp()) {
          case PlusPlus -> "++";
          case MinusMinus -> "--";
        };
        mv.visitInvokeDynamicInsn("unary", "(Ljava/lang/Object;)Ljava/lang/Object;", BSM_BUILTIN, opName);
        if (prefix) {
          mv.visitInsn(DUP);
        }
        mv.visitVarInsn(ASTORE, varIndex);
      }
      case Swc4jAstBinExpr binaryExpr -> {
        var opName = binaryExpr.getOp().getName();
        visitCode(binaryExpr.getLeft());
        visitCode(binaryExpr.getRight());
        mv.visitInvokeDynamicInsn("binary", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", BSM_BUILTIN, opName);
      }
      case Swc4jAstUnaryExpr unaryExpr -> {
        var opName = unaryExpr.getOp().getName();
        visitCode(unaryExpr.getArg());
        mv.visitInvokeDynamicInsn("unary", "(Ljava/lang/Object;)Ljava/lang/Object;", BSM_BUILTIN, opName);
      }
      case Swc4jAstCallExpr callExpr -> {
        var callee = callExpr.getCallee();
        var args = callExpr.getArgs();
        if (callee instanceof Swc4jAstMemberExpr memberExpr) {  // method call
          var obj = memberExpr.getObj();
          var name = name(memberExpr.getProp());
          visitCode(obj);
          for(var arg : args) {
            visitCode(arg);
          }
          var desc = "(" + "Ljava/lang/Object;".repeat(1 + args.size()) + ")Ljava/lang/Object;";
          mv.visitInvokeDynamicInsn("methodcall", desc, BSM_METHODCALL, name);
          return;
        }
        // function call
        visitCode(callExpr.getCallee());
        ldcUndefined(mv);
        for(var arg : args) {
          visitCode(arg);
        }
        var desc = "(" + "Ljava/lang/Object;".repeat(2 + args.size()) + ")Ljava/lang/Object;";
        mv.visitInvokeDynamicInsn("call", desc, BSM_CALL);
      }
      case Swc4jAstIdent ident -> {
        var index = varIndex(ident);
        if (index == -1) {
          var name = ident.getSym();
          mv.visitInvokeDynamicInsn("lookup", "()Ljava/lang/Object;", BSM_LOOKUP, name);
        } else {
          mv.visitVarInsn(ALOAD, index);
        }
      }
      case Swc4jAstIdentName identName -> {
        var index = varIndex(identName);
        if (index == -1) {
          var name = identName.getSym();
          mv.visitInvokeDynamicInsn("lookup", "()Ljava/lang/Object;", BSM_LOOKUP, name);
        } else {
          mv.visitVarInsn(ALOAD, index);
        }
      }
      case Swc4jAstMemberExpr memberExpr -> {
        var name = name(memberExpr.getProp());
        visitCode(memberExpr.getObj());
        mv.visitInvokeDynamicInsn("get", "(Ljava/lang/Object;)Ljava/lang/Object;", BSM_GET, name);
      }
      case Swc4jAstParenExpr parenExpr -> {
        visitCode(parenExpr.getExpr());
      }
      case Swc4jAstSeqExpr seqExpr -> {
        var exprs = seqExpr.getExprs();
        for (int i = 0; i < exprs.size(); i++) {
          var expr = exprs.get(i);
          visitCode(expr);
          if (i != exprs.size() - 1) {
            mv.visitInsn(POP);
          }
        }
      }
      case Swc4jAstThisExpr expr -> {
        var index = varIndex(expr);
        mv.visitVarInsn(ALOAD, index);
      }
      case Swc4jAstIfStmt ifStmt -> {
        visitCode(ifStmt.getTest());
        mv.visitInvokeDynamicInsn("truth", "(Ljava/lang/Object;)Z", BSM_TRUTH);
        var elseLabel = new Label();
        mv.visitJumpInsn(IFEQ, elseLabel);
        visitCode(ifStmt.getCons());
        var altOpt = ifStmt.getAlt();
        if (altOpt.isPresent()) {
          var endLabel = new Label();
          mv.visitJumpInsn(GOTO, endLabel);
          mv.visitLabel(elseLabel);
          visitCode(altOpt.orElseThrow());
          mv.visitLabel(endLabel);
        } else {
          mv.visitLabel(elseLabel);
        }
      }
      case Swc4jAstCondExpr condExpr -> {
        visitCode(condExpr.getTest());
        mv.visitInvokeDynamicInsn("truth", "(Ljava/lang/Object;)Z", BSM_TRUTH);
        var elseLabel = new Label();
        mv.visitJumpInsn(IFEQ, elseLabel);
        visitCode(condExpr.getCons());
        var endLabel = new Label();
        mv.visitJumpInsn(GOTO, endLabel);
        mv.visitLabel(elseLabel);
        visitCode(condExpr.getAlt());
        mv.visitLabel(endLabel);
      }
      case Swc4jAstWhileStmt whileStmt -> {
        var testLabel = new Label();
        mv.visitJumpInsn(GOTO, testLabel);
        var startLabel = new Label();
        mv.visitLabel(startLabel);
        visitCode(whileStmt.getBody());
        mv.visitLabel(testLabel);
        visitCode(whileStmt.getTest());
        mv.visitInvokeDynamicInsn("truth", "(Ljava/lang/Object;)Z", BSM_TRUTH);
        mv.visitJumpInsn(IFNE, startLabel);
      }
      case Swc4jAstDoWhileStmt doWhileStmt -> {
        var startLabel = new Label();
        mv.visitLabel(startLabel);
        visitCode(doWhileStmt.getBody());
        visitCode(doWhileStmt.getTest());
        mv.visitInvokeDynamicInsn("truth", "(Ljava/lang/Object;)Z", BSM_TRUTH);
        mv.visitJumpInsn(IFNE, startLabel);
      }
      case Swc4jAstForStmt forStmt -> {
        var optInit = forStmt.getInit();
        if (optInit.isPresent()) {
          visitCode(optInit.orElseThrow());
        }
        var testLabel = new Label();
        var testOpt = forStmt.getTest();
        if (testOpt.isPresent()) {
          mv.visitJumpInsn(GOTO, testLabel);
        }
        var startLabel = new Label();
        mv.visitLabel(startLabel);
        visitCode(forStmt.getBody());
        var updateOpt = forStmt.getUpdate();
        if (updateOpt.isPresent()) {
          visitCode(updateOpt.orElseThrow());
          mv.visitInsn(POP);
        }
        mv.visitLabel(testLabel);
        if (testOpt.isPresent()) {
          visitCode(testOpt.orElseThrow());
          mv.visitInvokeDynamicInsn("truth", "(Ljava/lang/Object;)Z", BSM_TRUTH);
          mv.visitJumpInsn(IFNE, startLabel);
        } else {
          mv.visitJumpInsn(GOTO, startLabel);
        }
      }
      case Swc4jAstBreakStmt _, Swc4jAstContinueStmt _, Swc4jAstLabeledStmt _,
           Swc4jAstDebuggerStmt _,
           Swc4jAstForInStmt _, Swc4jAstForOfStmt _,
           Swc4jAstSwitchStmt _, Swc4jAstThrowStmt _,
           Swc4jAstTryStmt _, Swc4jAstUsingDecl _, Swc4jAstWithStmt _,
           Swc4jAstAwaitExpr _,
           Swc4jAstMetaPropExpr _, Swc4jAstOptChainExpr _,
           Swc4jAstSpreadElement _, Swc4jAstSuperPropExpr _, Swc4jAstYieldExpr _,
           Swc4jAstArrayLit _, Swc4jAstBigInt _, Swc4jAstObjectLit _, Swc4jAstRegex _
          -> {
        throw new UnsupportedOperationException("TODO " + ast.getClass().getName());
      }
      default -> throw new AssertionError("ERROR " + ast.getClass().getName());
    }
  }
}
