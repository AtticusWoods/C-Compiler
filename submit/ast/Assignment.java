/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;
import submit.SymbolInfo;

/**
 *
 * @author edwajohn
 */
public class Assignment extends AbstractNode implements Expression {

  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  @Override
  public void toCminus(StringBuilder builder, final String prefix) {
    mutable.toCminus(builder, prefix);
    if (rhs != null) {
      builder.append(" ").append(type.toString()).append(" ");
      rhs.toCminus(builder, prefix);
    } else {
      builder.append(type.toString());
    }
  }
  
  @Override
  public MIPSResult toMIPS(StringBuilder code,
                          StringBuilder data,
                          SymbolTable symbolTable,
                          RegisterAllocator regAllocator) {
    // Get the variable name from the mutable
    String varName = mutable.getId();
    
    // Find the variable in the symbol table
    SymbolInfo symbolInfo = symbolTable.find(varName);
    if (symbolInfo != null) {
      // Get the offset of the variable from the symbol table
      int offset = symbolInfo.getOffset();
      
      // Get an available register to compute the address
      String addrRegister = regAllocator.getT();
      
      // Compute the address of the variable using its offset from $sp
      code.append("# Get ").append(varName).append("'s offset from $sp from the symbol table and initialize ").append(varName).append("'s address with it. We'll add $sp later.\n");
      code.append("li ").append(addrRegister).append(" ").append(offset).append("\n");
      code.append("# Add the stack pointer address to the offset.\n");
      code.append("add ").append(addrRegister).append(" ").append(addrRegister).append(" $sp\n");
      
      // Evaluate the right-hand side of the assignment
      code.append("# Compute rhs for assignment ").append(type).append("\n");
      MIPSResult rhsResult = rhs.toMIPS(code, data, symbolTable, regAllocator);
      String rhsRegister = rhsResult.getRegister();
      
      // Store the result in memory
      code.append("# complete assignment statement with store\n");
      if (mutable.getIndex() == null) {
        // Simple variable assignment
        code.append("sw ").append(rhsRegister).append(" 0(").append(addrRegister).append(")\n");
      } else {
        // Array assignment - would need to be implemented for arrays
      }
      
      // Free the registers
      regAllocator.clear(addrRegister);
      
      // The result of the assignment is the value assigned
      return rhsResult;
    }
    
    // If symbol not found, return a void result
    return MIPSResult.createVoidResult();
  }
}
