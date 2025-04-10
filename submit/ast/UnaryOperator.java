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

  public UnaryOperatorType getType() {
    return type;
  }

  public Node getExpression() {
    return expression;
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    MIPSResult exprResult = expression.toMIPS(code, data, symbolTable, regAllocator);
    String exprReg = exprResult.getRegister();

//    // If the expression result wasn't in a register (e.g., it was a constant)
//    if (exprReg == null) {
//      exprReg = regAllocator.getAny();
//      if (exprResult.getAddress() != null) {
//        code.append("lw ").append(exprReg).append(" ").append(exprResult.getAddress()).append("\n");
//      } else {
//        // Handle if it was a constant (shouldn't happen for unary ops)
//        throw new RuntimeException("Unexpected non-register/non-address result for unary operation");
//      }
//    }

    String resultReg = regAllocator.getAny();

    switch (type) {
      case NEG:
        // Optimized negation: 0 - value
        code.append("sub ").append(exprReg).append(" ").append("$zero").append(" ").append(exprReg).append("\n");
        break;
      case NOT:
        // Logical NOT implementation
        code.append("seq ").append(resultReg).append(" ").append(resultReg).append(" $zero\n");
        break;
      case DEREF:
        // Pointer dereference implementation
        code.append("lw ").append(resultReg).append(" 0(").append(resultReg).append(")\n");
        break;
      case QUESTION:
        throw new RuntimeException("Unsupported unary operator: ?");
      default:
        throw new RuntimeException("Unknown unary operator");
    }

    // Clear the expression register if we're done with it
    regAllocator.clear(exprReg);
    regAllocator.clear(resultReg);

    return MIPSResult.createRegisterResult(exprReg, exprResult.getType());
  }
}
