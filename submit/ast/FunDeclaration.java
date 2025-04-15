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
    // Create a new symbol table for this function's scope
    SymbolTable functionTable = new SymbolTable();
    functionTable.setCurrentFunction(id);

    // Function prologue
    code.append("\n# code for ").append(id).append("\n");
    code.append(id).append(":\n");

    // For all functions (including main), add scope entry
    code.append("# Entering a new scope.\n");
    code.append("# Symbols in symbol table:\n");
    
    // Add function parameters to the symbol table
    for (Param param : params) {
      functionTable.addVariable(param.getId(), param.getType());
    }
    
    // Reset the offset counter for this function
    functionTable.resetOffset();

    // Print all symbols in this function's scope
    for (String symbol : functionTable.getSymbols()) {
      code.append("#  ").append(symbol).append("\n");
    }

    code.append("# Update the stack pointer.\n");
    code.append("addi $sp $sp -0\n");  // Will be adjusted by compound statements

    // Special handling for main function
    if (id.equals("main")) {
      // Process function body
      statement.toMIPS(code, data, functionTable, regAllocator);

      // Main function epilogue
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp 0\n");
      code.append("li $v0 10\n");  // Exit syscall
      code.append("syscall\n");
    } else {
      // Process function body
      statement.toMIPS(code, data, functionTable, regAllocator);

      // Regular function epilogue
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp 0\n");
      code.append("jr $ra\n");  // Return to caller
    }

    return MIPSResult.createVoidResult();
  }
}

