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
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {


    MIPSResult lhsMips = lhs.toMIPS(code, data, symbolTable, regAllocator);
    String lhsReg = lhsMips.getRegister();
    MIPSResult rhsMips = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rhsReg = rhsMips.getRegister();

    if (type == BinaryOperatorType.PLUS || type == BinaryOperatorType.MINUS) {
      if (type == BinaryOperatorType.PLUS) {
        code.append("add ");
      } else {
        code.append("sub ");
      }
      code.append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.INT);

    } else if (type == BinaryOperatorType.DIVIDE || type == BinaryOperatorType.TIMES) {
      if (type == BinaryOperatorType.TIMES) {
        code.append("mult ");
      } else {
        code.append("div ");
      }
      code.append(lhsReg).append(" ").append(rhsReg).append(" ").append("mflo ").append(lhsReg).append("\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.INT);

    } else if (type == BinaryOperatorType.LT) {
      code.append("slt").append(" ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);

    } else if (type == BinaryOperatorType.GT) {
      code.append("slt").append(" ").append(lhsReg).append(" ").append(rhsReg).append(" ").append(lhsReg).append("\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);

    } else if (type == BinaryOperatorType.LE) {
      code.append("slt").append(" ").append(lhsReg).append(" ").append(rhsReg).append(" ").append(lhsReg).append("\n");
      code.append("subi ").append(lhsReg).append(" ").append(lhsReg).append(" 1\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);

    } else if (type == BinaryOperatorType.GE) {
      code.append("slt").append(" ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
      code.append("subi ").append(lhsReg).append(" ").append(lhsReg).append(" 1\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);

    } else if (type == BinaryOperatorType.EQ) {
      code.append("xor ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
      code.append("slti ").append(lhsReg).append(" ").append(lhsReg).append(" 1\n");
      regAllocator.clear(rhsReg);
      return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);
    }
    System.out.println("Binary operator not found");
    return super.toMIPS(code, data, symbolTable, regAllocator);
  }
}
