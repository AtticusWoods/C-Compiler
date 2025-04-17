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

    if (expr != null) {
      MIPSResult result = expr.toMIPS(code, data, symbolTable, regAllocator);
      SymbolInfo returnInfo = symbolTable.find("return");

      if (returnInfo != null) {
        int returnOffset = returnInfo.getOffset();
        if (result.getRegister() != null) {
          code.append("sw ").append(result.getRegister())
                  .append(" ").append(returnOffset).append("($sp)\n");
          regAllocator.clear(result.getRegister());
        }
      }
    }
    code.append("jr $ra\n");
    return MIPSResult.createVoidResult();
  }
}
