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
        // Evaluate left operand first
    MIPSResult lhsResult = lhs.toMIPS(code, data, symbolTable, regAllocator);
    String lhsReg = lhsResult.getRegister();

    // Evaluate right operand
    MIPSResult rhsResult = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rhsReg = rhsResult.getRegister();

    // Ensure left operand is in a register
    if (lhsReg == null) {
      lhsReg = regAllocator.getT();
      if (lhsResult.getAddress() != null) {
        code.append("lw ").append(lhsReg).append(" ").append(lhsResult.getAddress()).append("\n");
      } else if (lhs instanceof NumConstant) {
        code.append("li ").append(lhsReg).append(" ").append(((NumConstant)lhs).getValue()).append("\n");
      }
    }

    // Ensure right operand is in a register
    if (rhsReg == null) {
      rhsReg = regAllocator.getT();
      if (rhsResult.getAddress() != null) {
        code.append("lw ").append(rhsReg).append(" ").append(rhsResult.getAddress()).append("\n");
      } else if (rhs instanceof NumConstant) {
        code.append("li ").append(rhsReg).append(" ").append(((NumConstant)rhs).getValue()).append("\n");
      }
    }

    // Use lhsReg as the result register
    String resultReg = lhsReg;

    // Generate the operation
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

    // Clean up registers that are no longer needed
    if (!rhsReg.equals(resultReg)) {
      regAllocator.clear(rhsReg);
    }

    return MIPSResult.createRegisterResult(resultReg, VarType.INT);
  }
}
