/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.Collections;
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
      }
      
      // Always add the "return" symbol for consistency - only once
      code.append("#  return\n");
      
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
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -").append(frameSize).append("\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // Exit the scope when done
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(frameSize).append("\n");
      
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
      
      // Add return symbol (important for function calls)
      code.append("#  return\n");
      
      // Add local variables from the function body
      int localVarSize = 0;
      if (statement instanceof CompoundStatement) {
        CompoundStatement compStmt = (CompoundStatement) statement;
        for (Statement stmt : compStmt.getStatements()) {
          if (stmt instanceof VarDeclaration) {
            VarDeclaration varDecl = (VarDeclaration) stmt;
            for (String varId : varDecl.getIds()) {
              code.append("#  ").append(varId).append("\n");
            }
            localVarSize += varDecl.getTotalSize();
          }
        }
      }
      
      // Update stack pointer for local variables
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -").append(localVarSize).append("\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // The Return statement will generate 'jr $ra', so we don't need to add it again here
      
      // Exit the scope when done - add this before the implied return
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(localVarSize).append("\n");
      
      // Add a jr $ra only if the statement doesn't already end with one
      // This ensures functions without explicit return statements still return properly
      if (!(statement instanceof CompoundStatement) || 
          !endsWithReturn(((CompoundStatement)statement).getStatements())) {
        code.append("jr $ra\n");
      }
      
      return MIPSResult.createVoidResult();
    }
  }
  
  /**
   * Check if a list of statements ends with a Return statement
   */
  private boolean endsWithReturn(List<Statement> statements) {
    if (statements == null || statements.isEmpty()) {
      return false;
    }
    Statement lastStmt = statements.get(statements.size() - 1);
    if (lastStmt instanceof Return) {
      return true;
    } else if (lastStmt instanceof CompoundStatement) {
      return endsWithReturn(((CompoundStatement)lastStmt).getStatements());
    } else if (lastStmt instanceof If) {
      If ifStmt = (If)lastStmt;
      return endsWithReturn(Collections.singletonList(ifStmt.getTrueStatement())) && 
             (ifStmt.getFalseStatement() == null || 
              endsWithReturn(Collections.singletonList(ifStmt.getFalseStatement())));
    }
    return false;
  }
}
