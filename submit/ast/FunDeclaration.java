/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;
import submit.SymbolInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class FunDeclaration extends AbstractNode implements Declaration, Node {

  private final VarType returnType;
  private final String id;
  private final ArrayList<Param> params;
  private final Statement statement;

  public FunDeclaration(VarType returnType, String id, List<Param> params,
          Statement statement) {
    this.returnType = returnType;
    this.id = id;
    this.params = new ArrayList<>(params);
    this.statement = statement;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    String rt = (returnType != null) ? returnType.toString() : "void";
    builder.append("\n").append(rt).append(" ");
    builder.append(id);
    builder.append("(");

    for (Param param : params) {
      param.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!params.isEmpty()) {
      builder.delete(builder.length() - 2, builder.length());
    }
    builder.append(")\n");
    statement.toCminus(builder, prefix);
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    symbolTable.setCurrentFunction(id);

    // Add return symbol
    symbolTable.addSymbol("return", new SymbolInfo("return", returnType, false));

    // Function prologue
    code.append("\n# code for ").append(id).append("\n");
    code.append(id).append(":\n");




    // Special handling for main function
    if (id.equals("main")) {
//      code.append("\n# code for ").append(id).append("\n");
//      code.append(id).append(":\n");
//
//      // Add variables to symbol table but don't allocate space yet
//      for (Param param : params) {
//        // Handle parameters if needed
//      }

      // Process the function body without initial allocation
      if (statement instanceof CompoundStatement) {
        // Create temporary symbol table for declarations
        SymbolTable tempTable = symbolTable.createChild();

        // Find all variable declarations
        CompoundStatement cs = (CompoundStatement) statement;
        for (Statement s : cs.getStatements()) {
          if (s instanceof VarDeclaration) {
            VarDeclaration varDecl = (VarDeclaration) s;
            for (String id : varDecl.getIds()) {
              tempTable.addSymbol(id, new SymbolInfo(id, varDecl.getType(), false));
            }
          }
        }
        // Add return symbol
        tempTable.addSymbol("return", new SymbolInfo("return", returnType, false));

        // Output initial scope with 0 allocation
        code.append("# Entering a new scope.\n");
        code.append("# Symbols in symbol table:\n");
        for (String symbol : tempTable.getSymbols()) {
          code.append("#  ").append(symbol).append("\n");
        }
        code.append("# Update the stack pointer.\n");
        code.append("addi $sp $sp -0\n");

        // Now process the actual statements
        for (Statement s : cs.getStatements()) {
          s.toMIPS(code, data, tempTable, regAllocator);
        }

        code.append("# Exiting scope.\n");
        code.append("addi $sp $sp ").append(0).append("\n");
        code.append("li $v0 10\n");
        code.append("syscall\n");
      }
    } else {
      // Generate code for function body
      statement.toMIPS(code, data, symbolTable, regAllocator);
      code.append("jr $ra\n");
    }

    return MIPSResult.createVoidResult();
  }
}
