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
      // Generate code for main function
      code.append("\n# code for main\n");
      code.append("main:\n");
      
      // Enter a new scope for the function
      code.append("# Entering a new scope.\n");
      code.append("# Symbols in symbol table:\n");
      
      // Add println to the symbol table for the main function
      symbolTable.addSymbol("println", null); 
      code.append("#  println\n");
      
      // Handle return statement for proper stack management
      code.append("#  return\n");
      
      // Update stack pointer (for local variables)
      // For simple programs, this might just be 0
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -0\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // Exit the scope when done
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp 0\n");
      
      // Exit the program
      code.append("li $v0 10\n")
              .append("syscall\n");
      
      return MIPSResult.createVoidResult();
    } else {
      // Handle non-main functions
      code.append("\n# code for ").append(id).append("\n");
      code.append(id).append(":\n");
      
      // Enter a new scope for the function
      code.append("# Entering a new scope.\n");
      code.append("# Symbols in symbol table:\n");
      code.append("#  println\n");
      code.append("#  return\n");
      
      // Update stack pointer (for local variables)
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -0\n");
      
      // Generate the body of the function
      statement.toMIPS(code, data, symbolTable, regAllocator);
      
      // Exit the scope when done
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp 0\n");
      
      // Return from function
      code.append("jr $ra\n");
      
      return MIPSResult.createVoidResult();
    }
  }
}
