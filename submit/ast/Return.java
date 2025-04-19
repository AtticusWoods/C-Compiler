/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
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
  public MIPSResult toMIPS(StringBuilder code, 
                          StringBuilder data,
                          SymbolTable symbolTable,
                          RegisterAllocator regAllocator) {
    // If there's an expression to return
    if (expr != null) {
      // Evaluate the expression first
      MIPSResult exprResult = expr.toMIPS(code, data, symbolTable, regAllocator);
      
      // Get the "return" special symbol from the symbol table
      int returnOffset = symbolTable.find("return").getOffset();
      
      // Store the result of the expression in the return value location
      code.append("# Store return value\n");
      code.append("sw $t0 ").append(returnOffset).append("($sp)\n");
      
      // Free the register used for the result
      regAllocator.clear(exprResult.getRegister());
    }
    
    // Always return to the caller with jr $ra
    code.append("jr $ra\n");
    
    return MIPSResult.createVoidResult();
  }
}
