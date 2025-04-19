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
public class If extends AbstractNode implements Statement {

  private final Expression expression;
  private final Statement trueStatement;
  private final Statement falseStatement;

  public If(Expression expression, Statement trueStatement, Statement falseStatement) {
    this.expression = expression;
    this.trueStatement = trueStatement;
    this.falseStatement = falseStatement;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("if (");
    expression.toCminus(builder, prefix);
    builder.append(")\n");
    if (trueStatement instanceof CompoundStatement) {
      trueStatement.toCminus(builder, prefix);
    } else {
      trueStatement.toCminus(builder, prefix + " ");
    }
    if (falseStatement != null) {
      builder.append(prefix).append("else\n");

      if (falseStatement instanceof CompoundStatement) {
        falseStatement.toCminus(builder, prefix);
      } else {
        falseStatement.toCminus(builder, prefix + " ");
      }
    }

  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    MIPSResult exprMips = expression.toMIPS(code, data, symbolTable, regAllocator);
    String branchlabel = symbolTable.getUniqueLabel();
    String postElseLabel = symbolTable.getUniqueLabel();

    code.append("beq ").append(exprMips.getRegister()).append(" $zero ").append(branchlabel).append("\n");
    trueStatement.toMIPS(code, data, symbolTable, regAllocator);
    code.append("j ").append(postElseLabel).append("\n");

    code.append(branchlabel).append(":\n"); // branch to else staememtn
    if (falseStatement != null) {
      falseStatement.toMIPS(code, data, symbolTable, regAllocator);
    }
    code.append(postElseLabel).append(":\n"); // branch to if end
    regAllocator.clear(exprMips.getRegister());
    return MIPSResult.createVoidResult();
  }
}
