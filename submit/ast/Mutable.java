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
public class Mutable extends AbstractNode implements Expression, Node {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  public String getId() {
    return id;
  }
  
  public Expression getIndex() {
    return index;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id);
    if (index != null) {
      builder.append("[");
      index.toCminus(builder, prefix);
      builder.append("]");
    }
  }
  
  @Override
  public MIPSResult toMIPS(StringBuilder code,
                          StringBuilder data,
                          SymbolTable symbolTable,
                          RegisterAllocator regAllocator) {
    // Find the variable in the symbol table
    SymbolInfo symbolInfo = symbolTable.find(id);
    if (symbolInfo != null) {
      // Get the offset of the variable
      int offset = symbolInfo.getOffset();
      
      // For parameters and local variables, use teacher's approach:
      // Use negative offsets from stack pointer
      String addrRegister = regAllocator.getT();
      String resultRegister = regAllocator.getT();
      
      code.append("# Get ").append(id).append("'s offset from $sp from the symbol table and initialize ").append(id).append("'s address with it. We'll add $sp later.\n");
      code.append("li ").append(addrRegister).append(" ").append(offset).append("\n");
      code.append("# Add the stack pointer address to the offset.\n");
      code.append("add ").append(addrRegister).append(" ").append(addrRegister).append(" $sp\n");
      code.append("# Load the value of ").append(id).append(".\n");
      code.append("lw ").append(resultRegister).append(" 0(").append(addrRegister).append(")\n");
      
      // Free the address register
      regAllocator.clear(addrRegister);
      
      return MIPSResult.createRegisterResult(resultRegister, symbolInfo.getType());
    }
    
    // If variable not found in symbol table
    return MIPSResult.createVoidResult();
  }
}
