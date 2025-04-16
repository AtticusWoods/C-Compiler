/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Return extends AbstractNode implements Statement {

  private final Expression expr;

  public Return(Expression expr) {
    this.expr = expr;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix);
    if (expr == null) {
      builder.append("return;\n");
    } else {
      builder.append("return ");
      expr.toCminus(builder, prefix);
      builder.append(";\n");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
//    code.append("# Return statement\n");
    
    // If there's an expression to return
    if (expr != null) {
      // Evaluate the return expression
      MIPSResult result = expr.toMIPS(code, data, symbolTable, regAllocator);
      
      // Get the symbol info for the special "return" variable
      SymbolInfo returnSymbol = symbolTable.find("return");
      
      if (returnSymbol != null) {
        // Store the result in the "return" location on the stack
        if (result.getRegister() != null) {
          // If return value is in a register
          String offset = Integer.toString(returnSymbol.getOffset());
          code.append("sw ").append(result.getRegister()).append(", ").append(offset).append("($sp)\n");
          
          // Load the return value into $v0 (standard return value register)
          code.append("move $v0, ").append(result.getRegister()).append("\n");
          
          // Free the register
          regAllocator.clear(result.getRegister());
        } else if (result.getAddress() != null) {
          // If return value is an address (like a string)
          String tempReg = regAllocator.getT();
          code.append("la ").append(tempReg).append(", ").append(result.getAddress()).append("\n");
          
          String offset = Integer.toString(returnSymbol.getOffset());
          code.append("sw ").append(tempReg).append(", ").append(offset).append("($sp)\n");
          
          // Load the return value into $v0 (standard return value register)
          code.append("move $v0, ").append(tempReg).append("\n");
          
          // Free the register
          regAllocator.clear(tempReg);
        }
      }
    } else {
      // Void return - nothing to do but generate the jr $ra instruction
      code.append("# Void return\n");
    }
    
    // Return to caller
    code.append("jr $ra\n");
    
    return MIPSResult.createVoidResult();
  }

}
