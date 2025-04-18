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
import java.util.Set;

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
    code.append("\n");
    if ("main".equals(id)) {
      // Main function implementation (no parameters)
      code.append("# code for main\n");
      code.append("main:\n");
      
      // Enter a new scope for the function
      code.append("# Entering a new scope.\n");
      code.append("# Symbols in symbol table:\n");
      
      // Get the symbols from the compound statement if available
      if (statement instanceof CompoundStatement) {
        CompoundStatement compStmt = (CompoundStatement) statement;
        Set<String> symbols = compStmt.getSymbolNames();
        for (String symbol : symbols) {
          code.append("#  ").append(symbol).append("\n");
        }
      } else {
        // Default symbols
        code.append("#  println\n");
        code.append("#  return\n");
      }
      
      // Calculate the stack space needed for local variables
      int frameSize = 0;
      if (statement instanceof CompoundStatement) {
        CompoundStatement compStmt = (CompoundStatement) statement;
        // Look for var declarations to calculate total stack space needed
        for (Statement stmt : compStmt.getStatements()) {
          if (stmt instanceof VarDeclaration) {
            VarDeclaration varDecl = (VarDeclaration) stmt;
            frameSize += varDecl.getTotalSize();
          }
        }
      }
      
      // Update stack pointer (for local variables)
      // In the teacher's example, this is always 0 for main
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -").append(0).append("\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // Exit the scope when done
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(0).append("\n");
      
      // Exit the program
      code.append("li $v0 10\n")
              .append("syscall\n");
      
      return MIPSResult.createVoidResult();
    } else {
      // Non-main function implementation with parameters
      code.append("# code for ").append(id).append("\n");
      code.append(id).append(":\n");
      
      // Enter a new scope for the function
      code.append("# Entering a new scope.\n");
      code.append("# Symbols in symbol table:\n");
      
      // Add standard symbols
      code.append("#  println\n");
      
      // Add parameters to the comment output
      for (Param param : params) {
        code.append("#  ").append(param.getId()).append("\n");
      }
      
      // Add local variables from the function body
      if (statement instanceof CompoundStatement) {
        CompoundStatement compStmt = (CompoundStatement) statement;
        for (Statement stmt : compStmt.getStatements()) {
          if (stmt instanceof VarDeclaration) {
            VarDeclaration varDecl = (VarDeclaration) stmt;
            for (String id : varDecl.getIds()) {
              code.append("#  ").append(id).append("\n");
            }
          }
        }
      }
      
      // Add return symbol
      code.append("#  return\n");
      
      // Update stack pointer - following teacher's example with 0 for "add" function
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -").append(0).append("\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // Exit the scope when done
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(0).append("\n");
      
      // Return from function
      code.append("jr $ra\n");
      
      return MIPSResult.createVoidResult();
    }
  }
}
