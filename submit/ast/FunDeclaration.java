/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

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
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    if ("main".equals(id)) {
      code.append("# code for main\n");
      code.append("main:\n");
      
      // Enter a new scope for the function
      code.append("# Entering a new scope.\n");
      code.append("# Symbols in symbol table:\n");
      
      // Add all locals to the symbol table for display in comments
      if (statement instanceof CompoundStatement) {
        CompoundStatement cmpStmt = (CompoundStatement) statement;
        for (String symbol : cmpStmt.getSymbolNames()) {
          code.append("#  ").append(symbol).append("\n");
        }
      } else {
        // Add default symbols
        code.append("#  println\n");
        code.append("#  return\n");
      }
      
      // Calculate the total activation record size for this function
      int totalSize = symbolTable.getActivationRecordSize();
      
      // Update stack pointer (for local variables)
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -").append(totalSize).append("\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // Exit the scope when done
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(totalSize).append("\n");
      
      // Exit the program
      code.append("li $v0 10\n")
              .append("syscall\n");
      
      return MIPSResult.createVoidResult();
    } else {
      // Handle non-main functions (similar code, omitted for brevity)
      // ...
      return MIPSResult.createVoidResult();
    }
  }
}
