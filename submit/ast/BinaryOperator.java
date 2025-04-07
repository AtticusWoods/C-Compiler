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
public class BinaryOperator extends AbstractNode implements Expression {

  private final Expression lhs, rhs;
  private final BinaryOperatorType type;

  public BinaryOperator(Expression lhs, BinaryOperatorType type, Expression rhs) {
    this.lhs = lhs;
    this.type = type;
    this.rhs = rhs;
  }

  public BinaryOperator(Expression lhs, String type, Expression rhs) {
    this.lhs = lhs;
    this.type = BinaryOperatorType.fromString(type);
    this.rhs = rhs;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    lhs.toCminus(builder, prefix);
    builder.append(" ").append(type).append(" ");
    rhs.toCminus(builder, prefix);
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    // Evaluate left operand
    MIPSResult lhsResult = lhs.toMIPS(code, data, symbolTable, regAllocator);
    String lhsReg = lhsResult.getRegister();

    // If left operand isn't in a register, load it into one
    if (lhsReg == null) {
      lhsReg = regAllocator.getT();
      if (lhsResult.getAddress() != null) {
        code.append("lw ").append(lhsReg).append(" ").append(lhsResult.getAddress()).append("\n");
      } else {
        // Handle immediate values
        code.append("li ").append(lhsReg).append(" ").append("fillerlh").append("\n");
      }
    }

    // Evaluate right operand
    MIPSResult rhsResult = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rhsReg = rhsResult.getRegister();

    // If right operand isn't in a register, load it into one
    if (rhsReg == null) {
      rhsReg = regAllocator.getT();
      if (rhsResult.getAddress() != null) {
        code.append("lw ").append(rhsReg).append(" ").append(rhsResult.getAddress()).append("\n");
      } else {
        // Handle immediate values
        code.append("li ").append(rhsReg).append(" ").append("fillerrh").append("\n");
      }
    }

    String resultReg = regAllocator.getT();

    switch (type) {
      case PLUS:
        code.append("add ").append(resultReg).append(" ")
                .append(lhsReg).append(" ").append(rhsReg).append("\n");
        break;
      case MINUS:
        code.append("sub ").append(resultReg).append(" ")
                .append(lhsReg).append(" ").append(rhsReg).append("\n");
        break;
      case TIMES:
        code.append("mult ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        code.append("mflo ").append(resultReg).append("\n");
        break;
      case DIVIDE:
        code.append("div ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        code.append("mflo ").append(resultReg).append("\n");
        break;
      default:
        throw new UnsupportedOperationException("Operator not implemented: " + type);
    }

    regAllocator.clear(lhsReg);
    regAllocator.clear(rhsReg);

    return MIPSResult.createRegisterResult(resultReg, VarType.INT);
  }

}
