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

    switch (type) {
      case PLUS:
        code.append("add ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.INT);
        
      case MINUS:
        code.append("sub ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.INT);
        
      case TIMES:
        code.append("mult ").append(lhsReg).append(" ").append(rhsReg).append("\n").append("mflo ").append(lhsReg).append("\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.INT);
        
      case DIVIDE:
        code.append("div ").append(lhsReg).append(" ").append(rhsReg).append("\n").append("mflo ").append(lhsReg).append("\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.INT);
        
      case LT:
        code.append("slt").append(" ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);
        
      case GT:
        code.append("slt").append(" ").append(lhsReg).append(" ").append(rhsReg).append(" ").append(lhsReg).append("\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);
        
      case LE:
        code.append("slt").append(" ").append(lhsReg).append(" ").append(rhsReg).append(" ").append(lhsReg).append("\n");
        code.append("subi ").append(lhsReg).append(" ").append(lhsReg).append(" 1\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);
        
      case GE:
        code.append("slt").append(" ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        code.append("subi ").append(lhsReg).append(" ").append(lhsReg).append(" 1\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);
        
      case EQ:
        code.append("xor ").append(lhsReg).append(" ").append(lhsReg).append(" ").append(rhsReg).append("\n");
        code.append("slti ").append(lhsReg).append(" ").append(lhsReg).append(" 1\n");
        regAllocator.clear(rhsReg);
        return MIPSResult.createRegisterResult(lhsReg, VarType.BOOL);
        
      default:
        System.out.println("Binary operator not found");
        return super.toMIPS(code, data, symbolTable, regAllocator);
    }
  }
}

