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
public class UnaryOperator extends AbstractNode implements Expression {

  private final UnaryOperatorType type;
  private final Expression expression;

  public UnaryOperator(String type, Expression expression) {
    this.type = UnaryOperatorType.fromString(type);
    this.expression = expression;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(type);
    expression.toCminus(builder, prefix);
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {

    MIPSResult exprMips = expression.toMIPS(code, data, symbolTable, regAllocator);
    String reg = exprMips.getRegister();
    if (reg == null) {
      reg = regAllocator.getT();
    }

    if (type == UnaryOperatorType.NEG) {
      code.append("sub ").append(reg).append(" $zero ").append(reg).append("\n");
    } else {
      System.err.println("Unexpected unary operator");
    }
    return MIPSResult.createRegisterResult(reg, exprMips.getType());
  }
}
