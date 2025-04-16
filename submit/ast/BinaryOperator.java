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
    MIPSResult leftRes = lhs.toMIPS(code, data, symbolTable, regAllocator);
    
    // Remember the left operand's register
    String leftReg = leftRes.getRegister();
    
    // Generate code for the right operand
    MIPSResult rightRes = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rightReg = rightRes.getRegister();
    
    if (type == BinaryOperatorType.PLUS) {
        code.append("add ").append(leftReg).append(" ").append(leftReg).append(" ").append(rightReg).append("\n");
    } else if (type == BinaryOperatorType.MINUS) {
        code.append("sub ").append(leftReg).append(" ").append(leftReg).append(" ").append(rightReg).append("\n");
    } else if (type == BinaryOperatorType.TIMES) {
        code.append("mult ").append(leftReg).append(" ").append(rightReg).append("\n");
        code.append("mflo ").append(leftReg).append("\n");
    } else if (type == BinaryOperatorType.DIVIDE) {
        code.append("div ").append(leftReg).append(" ").append(rightReg).append("\n");
        code.append("mflo ").append(leftReg).append("\n");
    }
    
    // Free the right operand's register since we no longer need it
    regAllocator.clear(rightReg);
    
    return MIPSResult.createRegisterResult(leftReg, VarType.INT);
  }
}
