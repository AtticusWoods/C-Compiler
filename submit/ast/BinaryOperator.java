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
          // Evaluate the left-hand side expression and get its result
      MIPSResult lhsResult = lhs.toMIPS(code, data, symbolTable, regAllocator);
      String lhsRegister = lhsResult.getRegister();
      
      // Evaluate the right-hand side expression and get its result
      MIPSResult rhsResult = rhs.toMIPS(code, data, symbolTable, regAllocator);
      String rhsRegister = rhsResult.getRegister();
      
      // The result will be stored in the LHS register
      String resultRegister = lhsRegister;
      
      // Generate MIPS code for the binary operation
      switch (type) {
        case PLUS:
          code.append("add ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case MINUS:
          code.append("sub ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case TIMES:
          code.append("mult ").append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          code.append("mflo ").append(resultRegister).append("\n");
          break;
        case DIVIDE:
          code.append("div ").append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          code.append("mflo ").append(resultRegister).append("\n");
          break;
        case MOD:
          code.append("div ").append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          code.append("mfhi ").append(resultRegister).append("\n");
          break;
        case EQ:
          code.append("seq ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case NE:
          code.append("sne ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case LT:
          code.append("slt ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case LE:
          code.append("sle ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case GT:
          code.append("sgt ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case GE:
          code.append("sge ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case AND:
          code.append("and ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        case OR:
          code.append("or ").append(resultRegister).append(" ")
              .append(lhsRegister).append(" ")
              .append(rhsRegister).append("\n");
          break;
        default:
          // Handle unsupported operation
          break;
      }
      
      // Free the right-hand side register as we've now used its value
      if (!rhsRegister.equals(resultRegister)) {
        regAllocator.clear(rhsRegister);
      }
      
      // Return a reference to the result register
      return MIPSResult.createRegisterResult(resultRegister, VarType.INT);
    }
  }
