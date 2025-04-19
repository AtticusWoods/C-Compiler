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
      
      // Move result to $t0 (our standard return register)
      if (exprResult.getRegister() != null && !exprResult.getRegister().equals("$t0")) {
        code.append("move $t0 ").append(exprResult.getRegister()).append("\n");
        regAllocator.clear(exprResult.getRegister());
      }
    }
    
    // Return to caller
    code.append("jr $ra\n");
    
    return MIPSResult.createVoidResult();
  }
}
